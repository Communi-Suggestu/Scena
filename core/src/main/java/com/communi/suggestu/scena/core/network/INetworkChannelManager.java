package com.communi.suggestu.scena.core.network;

import com.communi.suggestu.scena.core.IScenaPlatform;

import java.util.function.Consumer;

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
     * @param version The version of the channel.
     * @return The new network channel, without configuration.
     */
    default INetworkChannel create(final String version) {
        return INetworkChannelManager.getInstance().create(version, channel -> {});
    }

    /**
     * Register a new network channel.
     *
     * @param version The version of the channel.
     * @param configurator The configurator for the channel.
     * @return The new network channel.
     */
    INetworkChannel create(final String version, Consumer<INetworkChannel> configurator);
}
