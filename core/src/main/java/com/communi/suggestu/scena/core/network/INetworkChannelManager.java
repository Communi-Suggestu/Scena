package com.communi.suggestu.scena.core.network;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Manager for network channels on a given platform.
 */
public interface INetworkChannelManager
{

    /**
     * Gives access to the current network manager.
     *
     * @return The current network manager.
     */
    static INetworkChannelManager getInstance() {
        return IScenaPlatform.getInstance().getNetworkChannelManager();
    }

    /**
     * Register a new network channel.
     * @return The new network channel, without configuration.
     */
    default INetworkChannel create() {
        return INetworkChannelManager.getInstance().create(channel -> {});
    }

    /**
     * Register a new network channel.
     *
     * @param configurator The configurator for the channel.
     * @return The new network channel.
     */
    INetworkChannel create(Consumer<INetworkChannel> configurator);
}
