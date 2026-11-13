package com.communi.suggestu.scena.core.client.rendering.sharding;

/**
 * Mutable upload state of one layer of one parent section mesh.
 * Staging progresses strictly in order, while GPU upload completions may arrive in any order and more than once.
 * All methods are synchronized because completions can run on the render thread without the dispatcher copy lock.
 */
public final class TerrainShardSet {

    private final int size;
    private final boolean[] uploaded;
    private int stagedCount;
    private int uploadedCount;
    private boolean released;

    public TerrainShardSet(TerrainShardPlan plan) {
        this(plan.size());
    }

    public TerrainShardSet(int size) {
        if (size <= 0 || size > TerrainShardPlan.MAX_SLICE_COUNT) {
            throw new IllegalArgumentException("Invalid shard count: " + size);
        }
        this.size = size;
        this.uploaded = new boolean[size];
    }

    /**
     * @return The number of slices copied into staging so far, always a prefix of the plan.
     */
    public synchronized int stagedCount() {
        return stagedCount;
    }

    public synchronized int uploadedCount() {
        return uploadedCount;
    }

    public synchronized boolean isReleased() {
        return released;
    }

    public synchronized boolean isStagingComplete() {
        return stagedCount == size;
    }

    /**
     * @return The index of the next slice to stage, or the slice count when staging is complete.
     */
    public synchronized int nextStageIndex() {
        return stagedCount;
    }

    /**
     * Records that a slice was accepted into staging. Must be called in plan order.
     *
     * @param index The index that was staged, must equal {@link #nextStageIndex()}.
     * @throws IllegalStateException if released, or if the index is not the next slice.
     */
    public synchronized void markStaged(int index) {
        if (released) {
            throw new IllegalStateException("Cannot stage slice " + index + " of a released set");
        }
        if (index != stagedCount || stagedCount == size) {
            throw new IllegalStateException("Expected to stage slice " + stagedCount + " but got " + index);
        }
        stagedCount++;
    }

    /**
     * Records a GPU upload completion. Duplicates and completions after release are ignored.
     *
     * @param index The slice index that finished uploading.
     * @return {@code true} only for the completion that makes every slice uploaded, once per set.
     */
    public synchronized boolean markUploaded(int index) {
        if (index < 0 || index >= uploaded.length) {
            throw new IllegalArgumentException("Invalid shard index: " + index);
        }
        if (released || uploaded[index]) {
            return false;
        }
        if (index >= stagedCount) {
            throw new IllegalStateException("Shard has not been staged: " + index);
        }
        uploaded[index] = true;
        uploadedCount++;
        return !released && uploadedCount == uploaded.length;
    }

    public synchronized boolean isReady() {
        return !released && uploadedCount == uploaded.length;
    }

    /**
     * Marks the set as released, so that no further staging or completion is recorded.
     *
     * @return {@code true} the first time, {@code false} for every later call.
     */
    public synchronized boolean release() {
        if (released) {
            return false;
        }
        released = true;
        return true;
    }
}
