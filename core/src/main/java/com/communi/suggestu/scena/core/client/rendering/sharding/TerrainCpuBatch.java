package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.SectionCompiler;

/**
 * Compile-local CPU ownership, transferred to the parent mesh on successful compilation.
 */
public final class TerrainCpuBatch
{
    public static final int MAX_VERTEX_BYTES = 32 * 1024 * 1024;
    private static final ThreadLocal<TerrainCpuBatch> CURRENT = new ThreadLocal<>();

    private final Map<ChunkSectionLayer, List<OwnedMesh>> meshes = new EnumMap<>(ChunkSectionLayer.class);
    private final Map<ChunkSectionLayer, ByteBufferBuilder> current = new EnumMap<>(ChunkSectionLayer.class);
    private boolean finished;

    private record OwnedMesh(MeshData mesh, ByteBufferBuilder owner)
    {
        void close()
        {
            mesh.close();
            if (owner != null)
            {
                owner.close();
            }
        }
    }

    private TerrainCpuBatch()
    {
    }

    public static SectionCompiler.Results compile(final Supplier<SectionCompiler.Results> operation)
    {
        if (!TerrainShardGate.isEnabled())
        {
            return operation.get();
        }
        if (CURRENT.get() != null)
        {
            throw new IllegalStateException("Nested terrain compilation on the same thread");
        }
        final TerrainCpuBatch batch = new TerrainCpuBatch();
        CURRENT.set(batch);
        boolean transferred = false;
        try
        {
            final SectionCompiler.Results results = operation.get();
            batch.finish(results);
            ((TerrainCpuResultAccess) (Object) results).scena$cpuBatch(batch);
            transferred = true;
            return results;
        }
        finally
        {
            CURRENT.remove();
            if (!transferred)
            {
                batch.close();
            }
        }
    }

    public static BufferBuilder rotate(final Map<ChunkSectionLayer, BufferBuilder> builders,
                                       final ChunkSectionLayer layer, final BufferBuilder builder)
    {
        final TerrainCpuBatch batch = CURRENT.get();
        if (batch == null || layer == ChunkSectionLayer.TRANSLUCENT)
        {
            return builder;
        }
        final TerrainBuilderAccess access = (TerrainBuilderAccess) builder;
        final int vertices = access.scena$vertexCount();
        final int stride = access.scena$format().getVertexSize();
        if (!shouldRotate(vertices, stride))
        {
            return builder;
        }
        final MeshData mesh = builder.buildOrThrow();
        batch.meshes.computeIfAbsent(layer, ignored -> new ArrayList<>()).add(new OwnedMesh(mesh, batch.current.remove(layer)));
        final ByteBufferBuilder buffer = new ByteBufferBuilder(32 * 1024);
        batch.current.put(layer, buffer);
        final BufferBuilder next = new BufferBuilder(buffer, PrimitiveTopology.QUADS, access.scena$format());
        builders.put(layer, next);
        return next;
    }

    static boolean shouldRotate(final int vertices, final int stride)
    {
        return vertices > 0 && vertices % 4 == 0 && ((long) vertices + 4) * stride > MAX_VERTEX_BYTES;
    }

    private void finish(final SectionCompiler.Results results)
    {
        for (final Map.Entry<ChunkSectionLayer, List<OwnedMesh>> entry : meshes.entrySet())
        {
            final ChunkSectionLayer layer = entry.getKey();
            final MeshData tail = results.renderedLayers.get(layer);
            if (tail == null || tail.indexBuffer() != null)
            {
                throw new IllegalStateException("Sharded opaque terrain must have a nonempty implicit-index tail");
            }
            entry.getValue().add(new OwnedMesh(tail, current.remove(layer)));
        }
        finished = true;
    }

    public static long additionalQuadCount(final SectionCompiler.Results results)
    {
        if (!((Object) results instanceof TerrainCpuResultAccess access))
        {
            return 0;
        }
        final TerrainCpuBatch attached = access.scena$cpuBatch();
        final TerrainCpuBatch batch = attached == null ? CURRENT.get() : attached;
        return batch == null ? 0 : batch.quadCount();
    }

    public synchronized boolean hasLayer(final ChunkSectionLayer layer)
    {
        return meshes.containsKey(layer);
    }

    public synchronized List<ByteBuffer> buffers(final ChunkSectionLayer layer)
    {
        final List<OwnedMesh> owned = meshes.get(layer);
        if (owned == null)
        {
            throw new IllegalArgumentException("Layer has no CPU shards: " + layer);
        }
        return owned.stream().map(part -> part.mesh().vertexBuffer()).toList();
    }

    public long quadCount()
    {
        long count = 0;
        for (final List<OwnedMesh> parts : meshes.values())
        {
            // After finish, the final part is already present in Results.renderedLayers.
            for (int i = 0; i < parts.size() - (finished ? 1 : 0); i++)
            {
                count += parts.get(i).mesh().drawState().vertexCount() / 4;
            }
        }
        return count;
    }

    public synchronized void releaseLayer(final ChunkSectionLayer layer)
    {
        final List<OwnedMesh> owned = meshes.remove(layer);
        if (owned != null)
        {
            owned.forEach(OwnedMesh::close);
        }
    }

    public synchronized void close()
    {
        for (final ChunkSectionLayer layer : List.copyOf(meshes.keySet()))
        {
            releaseLayer(layer);
        }
        current.values().forEach(ByteBufferBuilder::close);
        current.clear();
    }
}
