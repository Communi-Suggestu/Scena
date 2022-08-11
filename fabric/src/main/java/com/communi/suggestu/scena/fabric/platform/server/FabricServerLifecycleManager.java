package com.communi.suggestu.scena.fabric.platform.server;

import net.minecraft.server.MinecraftServer;

public final class FabricServerLifecycleManager
{
    private static final FabricServerLifecycleManager INSTANCE = new FabricServerLifecycleManager();

    public static FabricServerLifecycleManager getInstance()
    {
        return INSTANCE;
    }

    private MinecraftServer server = null;

    private FabricServerLifecycleManager()
    {
    }

    public MinecraftServer getServer()
    {
        return server;
    }

    public void setServer(final MinecraftServer server)
    {
        this.server = server;
    }

    public void clearServer() {
        this.server = null;
    }
}
