package com.communi.suggestu.scena.core.client.rendering.sharding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerrainShardPlanTest {

    private static final int STRIDE = 32;
    private static final int QUAD = 4 * STRIDE;

    @Test
    void singleSliceWhenDataFitsWithinLimit() {
        TerrainShardPlan plan = TerrainShardPlan.create(10 * QUAD, STRIDE, 100L * QUAD);

        assertEquals(1, plan.size());
        TerrainShardSlice slice = plan.slice(0);
        assertEquals(0, slice.byteOffset());
        assertEquals(10 * QUAD, slice.byteLength());
        assertEquals(40, slice.vertexCount());
        assertEquals(60, slice.indexCount());
    }

    @Test
    void slicesAreQuadAlignedAndTailIsExact() {
        long total = 10L * QUAD;
        // Limit is not a quad multiple, so it must round down to 3 quads per slice.
        TerrainShardPlan plan = TerrainShardPlan.create(total, STRIDE, 3L * QUAD + 7);

        assertEquals(4, plan.size());
        long covered = 0;
        for (TerrainShardSlice slice : plan.slices()) {
            assertEquals(0, slice.byteLength() % QUAD);
            assertEquals(covered, slice.byteOffset());
            assertEquals(slice.vertexCount() / 4 * 6, slice.indexCount());
            covered += slice.byteLength();
        }
        assertEquals(total, covered);
        assertEquals(3 * QUAD, plan.slice(0).byteLength());
        assertEquals(QUAD, plan.slice(3).byteLength());
    }

    @Test
    void exactMultipleOfLimitProducesFullSlices() {
        TerrainShardPlan plan = TerrainShardPlan.create(4L * QUAD, STRIDE, 2L * QUAD);

        assertEquals(2, plan.size());
        assertEquals(2 * QUAD, plan.slice(0).byteLength());
        assertEquals(2 * QUAD, plan.slice(1).byteLength());
        assertEquals(2 * QUAD, plan.slice(1).byteOffset());
    }

    @Test
    void rejectsMisalignedOrInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> TerrainShardPlan.create(QUAD + 4, STRIDE, QUAD));
        assertThrows(IllegalArgumentException.class, () -> TerrainShardPlan.create(0, STRIDE, QUAD));
        assertThrows(IllegalArgumentException.class, () -> TerrainShardPlan.create(QUAD, 0, QUAD));
        assertThrows(IllegalArgumentException.class, () -> TerrainShardPlan.create(QUAD, STRIDE, QUAD - 1));
        assertThrows(IllegalArgumentException.class, () -> TerrainShardPlan.create((long) Integer.MAX_VALUE + 1, STRIDE, QUAD));
    }

    @Test
    void rejectsPlansAboveSliceLimit() {
        long total = (long) (TerrainShardPlan.MAX_SLICE_COUNT + 1) * QUAD;
        assertThrows(IllegalArgumentException.class, () -> TerrainShardPlan.create(total, STRIDE, QUAD));
        assertEquals(TerrainShardPlan.MAX_SLICE_COUNT,
                TerrainShardPlan.create((long) TerrainShardPlan.MAX_SLICE_COUNT * QUAD, STRIDE, QUAD).size());
    }

    @Test
    void boundsLocalVertexAndIndexCountsEvenWithTinyVertexStride() {
        final TerrainShardPlan plan = TerrainShardPlan.create(100_000_000, 1, Integer.MAX_VALUE);
        long covered = 0;
        for (final TerrainShardSlice slice : plan.slices()) {
            assertTrue(slice.vertexCount() <= 16_777_212);
            assertEquals(slice.vertexCount() / 4 * 6, slice.indexCount());
            assertTrue(slice.indexCount() > 0);
            covered += slice.byteLength();
        }
        assertEquals(100_000_000, covered);
    }
}
