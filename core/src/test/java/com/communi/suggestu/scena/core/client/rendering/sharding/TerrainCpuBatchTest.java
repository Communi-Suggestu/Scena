package com.communi.suggestu.scena.core.client.rendering.sharding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerrainCpuBatchTest
{
    @Test
    void rotatesBeforeAQuadWouldExceedAnUnalignedByteBudget()
    {
        final int stride = 28;
        final int fullVertices = TerrainCpuBatch.MAX_VERTEX_BYTES / (4 * stride) * 4;

        assertFalse(TerrainCpuBatch.shouldRotate(0, stride));
        assertFalse(TerrainCpuBatch.shouldRotate(fullVertices - 4, stride));
        assertTrue(TerrainCpuBatch.shouldRotate(fullVertices, stride));
        assertFalse(TerrainCpuBatch.shouldRotate(fullVertices + 1, stride));
    }

    @Test
    void permitsAQuadThatExactlyFillsTheBudget()
    {
        final int stride = 32;
        final int fullVertices = TerrainCpuBatch.MAX_VERTEX_BYTES / stride;

        assertFalse(TerrainCpuBatch.shouldRotate(fullVertices - 4, stride));
        assertTrue(TerrainCpuBatch.shouldRotate(fullVertices, stride));
    }
}
