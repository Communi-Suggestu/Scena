package com.communi.suggestu.scena.core.network;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.resources.ResourceLocation;

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
     *
     * @param name The name of the channel
     * @param networkProtocolVersion The version of the protocol on this logical side of the channel.
     * @param clientAcceptedVersions The versions of the client protocol that are accepted.
     * @param serverAcceptedVersions The versions of the server protocol that are accepted.
     */
    INetworkChannel create(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions);
}
