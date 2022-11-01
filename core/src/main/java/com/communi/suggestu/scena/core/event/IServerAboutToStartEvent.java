package com.communi.suggestu.scena.core.event;

import net.minecraft.server.MinecraftServer;

/**
 * Event fired when a server is about to start.
 */
@FunctionalInterface
public interface IServerAboutToStartEvent extends IEvent {

    /**
     * Invoked when a server is about to start.
     *
     * @param minecraftServer The server that is about to start.
     */
    void handle(MinecraftServer minecraftServer);
}
