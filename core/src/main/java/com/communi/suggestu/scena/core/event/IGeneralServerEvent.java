package com.communi.suggestu.scena.core.event;

import net.minecraft.server.MinecraftServer;

/**
 * Event fired during a lifecycle of the server
 */
@FunctionalInterface
public interface IGeneralServerEvent extends IEvent {

    /**
     * Invoked when the event is triggered.
     *
     * @param minecraftServer The server that triggered the event.
     */
    void handle(MinecraftServer minecraftServer);
}
