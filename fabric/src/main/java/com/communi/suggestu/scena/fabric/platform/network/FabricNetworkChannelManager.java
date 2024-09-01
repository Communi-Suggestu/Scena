package com.communi.suggestu.scena.fabric.platform.network;

import com.communi.suggestu.scena.core.network.INetworkChannel;
import com.communi.suggestu.scena.core.network.INetworkChannelManager;

import java.util.function.Consumer;

public final class FabricNetworkChannelManager implements INetworkChannelManager
{
    private static final FabricNetworkChannelManager INSTANCE = new FabricNetworkChannelManager();

    public static FabricNetworkChannelManager getInstance()
    {
        return INSTANCE;
    }

    private FabricNetworkChannelManager()
    {
    }

    @Override
    public INetworkChannel create(Consumer<INetworkChannel> configurator) {
        final FabricNetworkChannel channel = new FabricNetworkChannel();
        configurator.accept(channel);
        return channel;
    }
}
