package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.slf4j.Logger;

public final class TerrainShardHooks
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private TerrainShardHooks()
    {
    }

    public static boolean eligible(final ChunkSectionLayer layer, final ByteBuffer vertices, final ByteBuffer indices)
    {
        return TerrainShardGate.isEnabled() && layer != ChunkSectionLayer.TRANSLUCENT && vertices != null && indices == null;
    }

    public static boolean needsShards(final TerrainSectionAccess owner, final CompiledSectionMesh mesh, final ChunkSectionLayer layer, final int bytes)
    {
        final TerrainCpuBatch batch = ((TerrainMeshAccess) mesh).scena$shards().cpuBatch();
        return (batch != null && batch.hasLayer(layer)) || bytes > budget((TerrainDispatcherAccess) owner.scena$dispatcher(), layer);
    }

    private static int budget(final TerrainDispatcherAccess dispatcher, final ChunkSectionLayer layer)
    {
        return Math.min(32 * 1024 * 1024, Math.min(dispatcher.scena$stagingCapacity(), dispatcher.scena$heapCapacity(layer)));
    }

    public static boolean stage(final TerrainSectionAccess owner, final ChunkSectionLayer layer, final CompiledSectionMesh parent, final ByteBuffer vertices)
    {
        final SectionRenderDispatcher dispatcher = owner.scena$dispatcher();
        dispatcher.lock();
        try
        {
            final TerrainMeshState state = ((TerrainMeshAccess) parent).scena$shards();
            final TerrainDispatcherAccess access = (TerrainDispatcherAccess) dispatcher;
            if (access.scena$uploads().isClosed())
            {
                owner.scena$cancelTasks();
                return false;
            }
            if (state.isReleased() || !state.isCurrent(owner))
            {
                return false;
            }
            final int stride = layer.pipeline().getVertexFormatBinding(0).getVertexSize();
            final TerrainCpuBatch batch = state.cpuBatch();
            final boolean cpuSharded = batch != null && batch.hasLayer(layer);
            final List<ByteBuffer> sources = cpuSharded ? batch.buffers(layer) : List.of(vertices);
            TerrainLayerShards shards = state.layer(layer);
            if (shards == null)
            {
                shards = state.begin(owner, parent, layer, sources, stride, budget(access, layer));
                LOGGER.info("Experimental terrain sharding: layer={}, bytes={}, shards={}, shardBudget={}",
                    layer, sources.stream().mapToLong(ByteBuffer::remaining).sum(), shards.keys().size(), budget(access, layer));
                parent.setIndexBufferUploaded(layer);
            }
            final boolean accepted = shards.stage(sources, access.scena$vertices(layer), () -> state.uploaded(layer));
            if (accepted && cpuSharded)
            {
                batch.releaseLayer(layer);
            }
            if (!accepted && RenderSystem.isOnRenderThread())
            {
                dispatcher.uploadTerrainBuffersToGpu();
            }
            return accepted;
        }
        finally
        {
            dispatcher.unlock();
        }
    }

    public static SectionMesh first(final SectionMesh mesh, final ChunkSectionLayer layer)
    {
        if (mesh instanceof TerrainMeshAccess access)
        {
            final TerrainLayerShards shards = access.scena$shards().layer(layer);
            if (shards != null)
            {
                return shards.keys().getFirst();
            }
        }
        return mesh;
    }

    public static <T> boolean appendDraws(final Consumer<RenderPass.Draw<T>> append,
                                     final RenderPass.Draw<T> first, final SectionMesh parent,
                                     final ChunkSectionLayer layer, final SectionRenderDispatcher dispatcher)
    {
        append.accept(first);
        if (parent instanceof TerrainMeshAccess access)
        {
            final TerrainLayerShards shards = access.scena$shards().layer(layer);
            if (shards != null)
            {
                final int stride = layer.pipeline().getVertexFormatBinding(0).getVertexSize();
                for (int i = 1; i < shards.keys().size(); i++)
                {
                    final SectionMesh key = shards.keys().get(i);
                    final SectionRenderDispatcher.RenderSectionBufferSlice slice = dispatcher.getRenderSectionSlice(key, layer);
                    if (slice == null)
                    {
                        throw new IllegalStateException("Published terrain shard has no GPU allocation");
                    }
                    final int base = Math.toIntExact(slice.vertexBufferOffset() / stride);
                    append.accept(new RenderPass.Draw<>(first.slot(), slice.vertexBuffer(), null, null, 0,
                        key.getSectionDraw(layer).indexCount(), base, first.uniformUploaderConsumer()));
                }
            }
        }
        return true;
    }
}
