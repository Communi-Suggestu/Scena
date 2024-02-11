package com.communi.suggestu.scena.core.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Event fired when a chunk is sent to a player.
 */
public interface IChunkSentEvent extends IEvent {

    /**
     * Invoked when a chunk is sent to a player.
     *
     * @param entity The player the chunk is sent to.
     * @param chunk The chunk that was sent.
     * @param level The level the chunk is sent in.
     */
    void handle(ServerPlayer entity, LevelChunk chunk, ServerLevel level);
}
