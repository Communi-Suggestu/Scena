package com.communi.suggestu.scena.core.client.rendering.sharding;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Counters for the sharded upload path, read by acceptance harnesses and diagnostics.
 * Zero when sharding is disabled; the counters never reset during a session.
 */
public final class TerrainShardStats
{
    private static final AtomicLong COMPILES = new AtomicLong();
    private static final AtomicLong LAYERS_SHARDED = new AtomicLong();
    private static final AtomicLong SHARDS_STAGED = new AtomicLong();
    private static final AtomicLong SHARDS_UPLOADED = new AtomicLong();
    private static final AtomicLong DRAWS_APPENDED = new AtomicLong();
    private static final AtomicLong BUILDER_ROTATIONS = new AtomicLong();

    private TerrainShardStats()
    {
    }

    public static void recordCompile()
    {
        COMPILES.incrementAndGet();
    }

    public static void recordLayerSharded(final int shardCount)
    {
        LAYERS_SHARDED.incrementAndGet();
        SHARDS_STAGED.addAndGet(shardCount);
    }

    public static void recordShardsUploaded(final int shardCount)
    {
        SHARDS_UPLOADED.addAndGet(shardCount);
    }

    public static void recordDrawsAppended(final int extraDraws)
    {
        DRAWS_APPENDED.addAndGet(extraDraws);
    }

    public static void recordBuilderRotation()
    {
        BUILDER_ROTATIONS.incrementAndGet();
    }

    public static Snapshot snapshot()
    {
        return new Snapshot(
            COMPILES.get(),
            LAYERS_SHARDED.get(),
            SHARDS_STAGED.get(),
            SHARDS_UPLOADED.get(),
            DRAWS_APPENDED.get(),
            BUILDER_ROTATIONS.get()
        );
    }

    public record Snapshot(long compiles, long layersSharded, long shardsStaged, long shardsUploaded,
                           long drawsAppended, long builderRotations)
    {
    }
}
