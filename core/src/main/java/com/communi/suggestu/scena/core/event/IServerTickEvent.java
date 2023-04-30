package com.communi.suggestu.scena.core.event;

import net.minecraft.server.MinecraftServer;

/**
 * Event fired twice (once pre and once post) every tick on the server.
 */
@FunctionalInterface
public interface IServerTickEvent extends IEvent {

    /**
     * Called every tick on the server.
     *
     * @param server The server instance.
     */
    void onTick(final MinecraftServer server);
}
