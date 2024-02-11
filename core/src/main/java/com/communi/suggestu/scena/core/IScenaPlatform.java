package com.communi.suggestu.scena.core;

import com.communi.suggestu.scena.core.entity.block.IBlockEntityPositionManager;
import com.communi.suggestu.scena.core.blockstate.ILevelBasedPropertyAccessor;
import com.communi.suggestu.scena.core.client.IClientManager;
import com.communi.suggestu.scena.core.config.IConfigurationManager;
import com.communi.suggestu.scena.core.creativetab.ICreativeTabManager;
import com.communi.suggestu.scena.core.dist.IDistributionManager;
import com.communi.suggestu.scena.core.entity.IEntityInformationManager;
import com.communi.suggestu.scena.core.entity.IPlayerInventoryManager;
import com.communi.suggestu.scena.core.entity.block.IBlockEntityManager;
import com.communi.suggestu.scena.core.event.IGameEvents;
import com.communi.suggestu.scena.core.fluid.IFluidManager;
import com.communi.suggestu.scena.core.item.IDyeItemHelper;
import com.communi.suggestu.scena.core.item.IItemComparisonHelper;
import com.communi.suggestu.scena.core.network.INetworkChannelManager;
import com.communi.suggestu.scena.core.registries.IPlatformRegistryManager;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

/**
 * API Surface of a given game platform that is required to run scena mods.
 * Each game platform, like forge or fabric, has to provide this logic, outside
 * of Minecraft.
 *
 * Examples are registry access and systems to interact with the world.
 */
public interface IScenaPlatform
{
    /**
     * Gives access to the api instance.
     *
     * @return The api.
     */
    static IScenaPlatform getInstance()
    {
        return Holder.getInstance();
    }

    /**
     * Gives access to the current platform's registry.
     *
     * @return The registry manager for the current platform.
     */
    @NotNull
    IPlatformRegistryManager getPlatformRegistryManager();

    /**
     * Gives access to the current platform's way of processing entity information.
     *
     * @return The entity information manager.
     */
    @NotNull
    IEntityInformationManager getEntityInformationManager();

    /**
     * Gives access to the fluid manager of the current platform.
     *
     * @return The fluid manager of the current platform.
     */
    @NotNull
    IFluidManager getFluidManager();

    /**
     * The client manager for this platform.
     * Invoking this method on the server side, will throw an exception!
     *
     * @return The client manager.
     */
    @NotNull
    IClientManager getClientManager();

    /**
     * Gives access to level based property accessors.
     *
     * @return The accessor for level based properties.
     */
    @NotNull
    ILevelBasedPropertyAccessor getLevelBasedPropertyAccessor();

    /**
     * The item comparison helper of the platform at large.
     * Some platforms extend the functionality of itemstacks beyond item, meta and nbt.
     * And sometimes these values have to be taken into account while comparing itemstacks.
     *
     * @return The item comparison helper.
     */
    @NotNull
    IItemComparisonHelper getItemComparisonHelper();

    /**
     * Gives access to the player inventory manager.
     *
     * @return The player inventory manager.
     */
    @NotNull
    IPlayerInventoryManager getPlayerInventoryManager();

    /**
     * Gives access to the distribution manager.
     *
     * @return The distribution manager.
     */
    @NotNull
    IDistributionManager getDistributionManager();

    /**
     * Gives access to the network manager.
     *
     * @return The network manager.
     */
    @NotNull
    INetworkChannelManager getNetworkChannelManager();

    /**
     * Gives access to the dye item helper on the platform.
     *
     * @return The dye item helper.
     */
    @NotNull
    IDyeItemHelper getDyeItemHelper();

    /**
     * The configuration manager for the current platform.
     *
     * @return The configuration manager.
     */
    @NotNull
    IConfigurationManager getConfigurationManager();

    /**
     * Gives access to the current server platform.
     *
     * @return The current server running.
     */
    @NotNull
    MinecraftServer getCurrentServer();

    /**
     * The manager for handling creative tabs on the platform.
     *
     * @return The platforms creative tab manager.
     */
    @NotNull
    ICreativeTabManager getCreativeTabManager();

    /**
     * The block entity manager for the current platform.
     *
     * @return The block entity manager.
     */
    @NotNull
    IBlockEntityManager getBlockEntityManager();

    /**
     * The game events for the current platform.
     *
     * @return The game events.
     */
    @NotNull
    IGameEvents getGameEvents();

    /**
     * The block entity position manager for the current platform.
     *
     * @return The block entity position manager.
     */
    @NotNull
    IBlockEntityPositionManager getBlockEntityPositionManager();


    class Holder {
        private static IScenaPlatform apiInstance;

        public static IScenaPlatform getInstance()
        {
            return apiInstance;
        }

        public static void setInstance(final IScenaPlatform instance)
        {
            if (apiInstance != null)
                throw new IllegalStateException("Can not setup API twice!");

            apiInstance = instance;
        }
    }
}
