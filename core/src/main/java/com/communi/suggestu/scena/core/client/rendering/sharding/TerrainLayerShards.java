package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.SectionMesh;

public final class TerrainLayerShards
{
    private final TerrainShardSet progress;
    private final List<SectionMesh> keys;
    private final List<SourceSlice> slices;
    private final List<Integer> sourceSizes;

    private record SourceSlice(int source, TerrainShardSlice slice)
    {
    }

    TerrainLayerShards(final List<ByteBuffer> sources, final int stride, final int budget, final ChunkSectionLayer layer)
    {
        sourceSizes = sources.stream().map(ByteBuffer::remaining).toList();
        final List<SourceSlice> ranges = new ArrayList<>();
        final List<SectionMesh> children = new ArrayList<>();
        for (int source = 0; source < sources.size(); source++)
        {
            final TerrainShardPlan plan = TerrainShardPlan.create(sources.get(source).remaining(), stride, budget);
            for (final TerrainShardSlice slice : plan.slices())
            {
                ranges.add(new SourceSlice(source, slice));
                children.add(new Child(layer, new SectionMesh.SectionDraw(slice.indexCount(), IndexType.INT, false)));
            }
        }
        slices = List.copyOf(ranges);
        progress = new TerrainShardSet(slices.size());
        keys = List.copyOf(children);
    }

    public List<SectionMesh> keys()
    {
        return keys;
    }

    public boolean stage(final List<ByteBuffer> sources, final UberGpuBuffer<SectionMesh> buffer, final Runnable completed)
    {
        if (progress.isReleased())
        {
            throw new IllegalStateException("Cannot stage a released terrain mesh");
        }
        if (sources.size() != sourceSizes.size())
        {
            throw new IllegalArgumentException("Terrain source count changed during retry");
        }
        for (int i = 0; i < sources.size(); i++)
        {
            if (sources.get(i).remaining() != sourceSizes.get(i))
            {
                throw new IllegalArgumentException("Terrain upload changed size during retry");
            }
        }
        while (!progress.isStagingComplete())
        {
            final int index = progress.nextStageIndex();
            final SourceSlice range = slices.get(index);
            final TerrainShardSlice slice = range.slice();
            final ByteBuffer vertices = sources.get(range.source());
            final ByteBuffer source = vertices.slice(Math.addExact(vertices.position(), slice.byteOffset()), slice.byteLength());
            if (!buffer.addAllocation(keys.get(index), ignored -> {
                if (progress.markUploaded(index))
                {
                    completed.run();
                }
            }, source))
            {
                return false;
            }
            progress.markStaged(index);
        }
        return true;
    }

    public void release(final UberGpuBuffer<SectionMesh> buffer)
    {
        if (progress.release())
        {
            for (final SectionMesh key : keys)
            {
                buffer.removeAllocation(key);
            }
        }
    }

    private static final class Child implements SectionMesh
    {
        private final ChunkSectionLayer layer;
        private final SectionDraw draw;

        private Child(final ChunkSectionLayer layer, final SectionDraw draw)
        {
            this.layer = layer;
            this.draw = draw;
        }

        @Override
        public boolean facesCanSeeEachother(final net.minecraft.core.Direction first, final net.minecraft.core.Direction second)
        {
            return false;
        }

        @Override
        public SectionDraw getSectionDraw(final ChunkSectionLayer requested)
        {
            return layer == requested ? draw : null;
        }
    }
}
