package com.communi.suggestu.scena.forge.platform;

import com.communi.suggestu.scena.core.IScenaPlatform;
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
import com.communi.suggestu.scena.forge.platform.client.ForgeClientManager;
import com.communi.suggestu.scena.forge.platform.configuration.ForgeConfigurationManager;
import com.communi.suggestu.scena.forge.platform.creativetab.ForgeCreativeTabManager;
import com.communi.suggestu.scena.forge.platform.distribution.ForgeDistributionManager;
import com.communi.suggestu.scena.forge.platform.entity.ForgeEntityInformationManager;
import com.communi.suggestu.scena.forge.platform.entity.ForgePlayerInventoryManager;
import com.communi.suggestu.scena.forge.platform.event.ForgeGameEvents;
import com.communi.suggestu.scena.forge.platform.fluid.ForgeFluidManager;
import com.communi.suggestu.scena.forge.platform.item.DyeItemHelper;
import com.communi.suggestu.scena.forge.platform.item.ForgeItemComparisonHelper;
import com.communi.suggestu.scena.forge.platform.level.ForgeLevelBasedPropertyAccessor;
import com.communi.suggestu.scena.forge.platform.network.ForgeNetworkChannelManager;
import com.communi.suggestu.scena.forge.platform.registry.ForgeRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public final class ForgeScenaPlatform implements IScenaPlatform
{
    private static final ForgeScenaPlatform INSTANCE = new ForgeScenaPlatform();

    public static ForgeScenaPlatform getInstance()
    {
        return INSTANCE;
    }

    private ForgeScenaPlatform()
    {
    }

    @Override
    public @NotNull IPlatformRegistryManager getPlatformRegistryManager()
    {
        return ForgeRegistryManager.getInstance();
    }

    @Override
    public @NotNull IEntityInformationManager getEntityInformationManager()
    {
        return ForgeEntityInformationManager.getInstance();
    }

    @Override
    public @NotNull IFluidManager getFluidManager()
    {
        return ForgeFluidManager.getInstance();
    }

    @Override
    public @NotNull IClientManager getClientManager()
    {
        return ForgeClientManager.getInstance();
    }

    @Override
    public @NotNull ILevelBasedPropertyAccessor getLevelBasedPropertyAccessor()
    {
        return ForgeLevelBasedPropertyAccessor.getInstance();
    }

    @Override
    public @NotNull IItemComparisonHelper getItemComparisonHelper()
    {
        return ForgeItemComparisonHelper.getInstance();
    }

    @Override
    public @NotNull IPlayerInventoryManager getPlayerInventoryManager()
    {
        return ForgePlayerInventoryManager.getInstance();
    }

    @Override
    public @NotNull IDistributionManager getDistributionManager()
    {
        return ForgeDistributionManager.getInstance();
    }

    @Override
    public @NotNull INetworkChannelManager getNetworkChannelManager()
    {
        return ForgeNetworkChannelManager.getInstance();
    }

    @Override
    public @NotNull IDyeItemHelper getDyeItemHelper()
    {
        return DyeItemHelper.getInstance();
    }

    @Override
    public @NotNull IConfigurationManager getConfigurationManager()
    {
        return ForgeConfigurationManager.getInstance();
    }

    @Override
    public @NotNull MinecraftServer getCurrentServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public @NotNull ICreativeTabManager getCreativeTabManager()
    {
        return ForgeCreativeTabManager.getInstance();
    }

    @Override
    public @NotNull IBlockEntityManager getBlockEntityManager()
    {
        return new IBlockEntityManager() {};
    }

    @Override
    public @NotNull IGameEvents getGameEvents() {
        return ForgeGameEvents.getInstance();
    }
}
