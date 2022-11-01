package com.communi.suggestu.scena.core.event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * Event fired when a chunk is loaded.
 */
@FunctionalInterface
public interface IChunkLoadEvent extends IEvent {

    /**
     * Invoked when a chunk is loaded.
     *
     * @param level The level the chunk is loaded in.
     * @param chunkAccess The chunk that was loaded.
     */
    void handle(LevelAccessor level, ChunkAccess chunkAccess);
}
