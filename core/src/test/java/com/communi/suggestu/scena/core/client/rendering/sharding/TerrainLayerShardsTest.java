package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerrainLayerShardsTest
{
    @Test
    void multipleCpuResultsCanExceedOneJavaBufferWithoutOversizedDraws()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(32 * 1024 * 1024);
        final List<ByteBuffer> sources = new ArrayList<>();
        for (int i = 0; i < 80; i++)
        {
            sources.add(buffer.asReadOnlyBuffer());
        }
        sources.add(ByteBuffer.allocate(128));
        final TerrainLayerShards shards = new TerrainLayerShards(sources, 32, buffer.capacity(), ChunkSectionLayer.SOLID);
        assertEquals(81, shards.keys().size());
        assertEquals(81, new HashSet<>(shards.keys()).size());
        assertEquals(buffer.capacity() / 128 * 6, shards.keys().getFirst().getSectionDraw(ChunkSectionLayer.SOLID).indexCount());
        assertEquals(6, shards.keys().getLast().getSectionDraw(ChunkSectionLayer.SOLID).indexCount());
    }
}
