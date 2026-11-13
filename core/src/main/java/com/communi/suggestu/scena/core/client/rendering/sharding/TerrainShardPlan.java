package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable partition of one parent vertex byte range into quad-aligned slices.
 * All slices except possibly the last have the same size, so slice zero is always a maximal slice.
 */
public final class TerrainShardPlan {

    /**
     * Upper bound on slices per parent section. Keeps per-section bookkeeping and draw counts bounded.
     */
    public static final int MAX_SLICE_COUNT = 1024;

    private final int vertexStride;
    private final int totalBytes;
    private final List<TerrainShardSlice> slices;

    private TerrainShardPlan(int vertexStride, int totalBytes, List<TerrainShardSlice> slices) {
        this.vertexStride = vertexStride;
        this.totalBytes = totalBytes;
        this.slices = Collections.unmodifiableList(slices);
    }

    /**
     * Partitions {@code totalBytes} into slices of at most {@code maxSliceBytes}, each a whole number of quads.
     *
     * @param totalBytes    Size of the parent vertex data, must be a positive multiple of one quad.
     * @param vertexStride  Bytes per vertex of the active vertex format.
     * @param maxSliceBytes Upper bound on a slice, rounded down to whole quads.
     * @return The plan.
     * @throws IllegalArgumentException if the inputs are invalid, if the data would exceed {@link Integer#MAX_VALUE} bytes,
     *                                  or if the plan would need more than {@link #MAX_SLICE_COUNT} slices.
     */
    public static TerrainShardPlan create(long totalBytes, int vertexStride, long maxSliceBytes) {
        if (vertexStride <= 0) {
            throw new IllegalArgumentException("Vertex stride must be positive: " + vertexStride);
        }
        long quadBytes = 4L * vertexStride;
        if (quadBytes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Quad size overflows: " + quadBytes);
        }
        if (totalBytes <= 0 || totalBytes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Total bytes out of range: " + totalBytes);
        }
        if (totalBytes % quadBytes != 0) {
            throw new IllegalArgumentException("Total bytes " + totalBytes + " is not a multiple of quad size " + quadBytes);
        }

        long sliceBytes = (Math.min(Math.min(maxSliceBytes, totalBytes), 16_777_212L * vertexStride) / quadBytes) * quadBytes;
        if (sliceBytes <= 0) {
            throw new IllegalArgumentException("Max slice bytes " + maxSliceBytes + " is smaller than one quad " + quadBytes);
        }

        long sliceCount = (totalBytes + sliceBytes - 1) / sliceBytes;
        if (sliceCount > MAX_SLICE_COUNT) {
            throw new IllegalArgumentException("Plan needs " + sliceCount + " slices, above the limit of " + MAX_SLICE_COUNT);
        }

        List<TerrainShardSlice> slices = new ArrayList<>((int) sliceCount);
        for (int index = 0; index < sliceCount; index++) {
            long offset = index * sliceBytes;
            long length = Math.min(sliceBytes, totalBytes - offset);
            int vertexCount = (int) (length / vertexStride);
            slices.add(new TerrainShardSlice(index, (int) offset, (int) length, vertexCount, vertexCount / 4 * 6));
        }

        return new TerrainShardPlan(vertexStride, (int) totalBytes, slices);
    }

    public int vertexStride() {
        return vertexStride;
    }

    public int totalBytes() {
        return totalBytes;
    }

    public int size() {
        return slices.size();
    }

    public List<TerrainShardSlice> slices() {
        return slices;
    }

    public TerrainShardSlice slice(int index) {
        return slices.get(index);
    }
}
