package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import org.slf4j.Logger;

/**
 * All access is serialized by the owning dispatcher's copy lock.
 */
public final class TerrainMeshState
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<ChunkSectionLayer, TerrainLayerShards> layers = new EnumMap<>(ChunkSectionLayer.class);
    private TerrainSectionAccess owner;
    private CompiledSectionMesh parent;
    private long generation;
    private boolean released;
    private TerrainCpuBatch cpuBatch;

    public void cpuBatch(final TerrainCpuBatch batch)
    {
        cpuBatch = batch;
    }

    public TerrainCpuBatch cpuBatch()
    {
        return cpuBatch;
    }

    public void generation(final TerrainSectionAccess section, final long value)
    {
        owner = section;
        generation = value;
    }

    public boolean isCurrent(final TerrainSectionAccess section)
    {
        return owner == null || (owner == section && generation == section.scena$pending().generation());
    }

    public TerrainSectionAccess owner()
    {
        return owner;
    }

    public boolean isReleased()
    {
        return released;
    }

    public TerrainLayerShards layer(final ChunkSectionLayer layer)
    {
        return layers.get(layer);
    }

    public TerrainLayerShards begin(final TerrainSectionAccess section, final CompiledSectionMesh mesh,
                                   final ChunkSectionLayer layer, final List<ByteBuffer> sources, final int stride, final int budget)
    {
        if (released)
        {
            throw new IllegalStateException("Cannot initialize a released mesh");
        }
        if (parent == null)
        {
            if (owner == null)
            {
                generation(section, section.scena$pending().generation());
            }
            parent = mesh;
            section.scena$pending().add(mesh);
            ((TerrainDispatcherAccess) section.scena$dispatcher()).scena$uploads().add(this);
        }
        if (owner != section || parent != mesh)
        {
            throw new IllegalStateException("Terrain shard owner changed");
        }
        return layers.computeIfAbsent(layer, ignored -> new TerrainLayerShards(sources, stride, budget, layer));
    }

    public void uploaded(final ChunkSectionLayer layer)
    {
        owner.scena$dispatcher().lock();
        try
        {
            if (!released && generation == owner.scena$pending().generation()
                && !((TerrainDispatcherAccess) owner.scena$dispatcher()).scena$uploads().isClosed())
            {
                LOGGER.info("Experimental terrain shards uploaded: layer={}, shards={}", layer, layers.get(layer).keys().size());
                owner.scena$vertexUploaded(parent, layer);
            }
        }
        finally
        {
            owner.scena$dispatcher().unlock();
        }
    }

    public void release()
    {
        if (released)
        {
            return;
        }
        released = true;
        // A cancelled worker still owns its CPU MeshData until Results.release().
        // Closing it here could race argument evaluation before its next staging call.
        if (owner != null && parent != null)
        {
            final TerrainDispatcherAccess dispatcher = (TerrainDispatcherAccess) owner.scena$dispatcher();
            layers.forEach((layer, shards) -> shards.release(dispatcher.scena$vertices(layer)));
            owner.scena$pending().published(parent);
            dispatcher.scena$uploads().remove(this);
        }
        layers.clear();
    }
}
