package com.communi.suggestu.scena.fabric.platform;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.communi.suggestu.scena.core.entity.block.IBlockEntityManager;
import com.communi.suggestu.scena.core.event.IGameEvents;
import com.communi.suggestu.scena.fabric.platform.client.FabricClientManager;
import com.communi.suggestu.scena.fabric.platform.configuration.FabricConfigurationManager;
import com.communi.suggestu.scena.fabric.platform.creativetab.FabricCreativeTabManager;
import com.communi.suggestu.scena.fabric.platform.dist.FabricDistributionManager;
import com.communi.suggestu.scena.fabric.platform.entity.FabricEntityInformationManager;
import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import com.communi.suggestu.scena.fabric.platform.fluid.FabricFluidManager;
import com.communi.suggestu.scena.fabric.platform.inventory.FabricPlayerInventoryManager;
import com.communi.suggestu.scena.fabric.platform.item.FabricDyeItemHelper;
import com.communi.suggestu.scena.fabric.platform.item.FabricItemComparisonHelper;
import com.communi.suggestu.scena.fabric.platform.level.FabricLevelBasedPropertyAccessor;
import com.communi.suggestu.scena.fabric.platform.network.FabricNetworkChannelManager;
import com.communi.suggestu.scena.fabric.platform.registry.FabricRegistryManager;
import com.communi.suggestu.scena.fabric.platform.server.FabricServerLifecycleManager;
import com.communi.suggestu.scena.core.blockstate.ILevelBasedPropertyAccessor;
import com.communi.suggestu.scena.core.client.IClientManager;
import com.communi.suggestu.scena.core.config.IConfigurationManager;
import com.communi.suggestu.scena.core.creativetab.ICreativeTabManager;
import com.communi.suggestu.scena.core.dist.IDistributionManager;
import com.communi.suggestu.scena.core.entity.IEntityInformationManager;
import com.communi.suggestu.scena.core.entity.IPlayerInventoryManager;
import com.communi.suggestu.scena.core.fluid.IFluidManager;
import com.communi.suggestu.scena.core.item.IDyeItemHelper;
import com.communi.suggestu.scena.core.item.IItemComparisonHelper;
import com.communi.suggestu.scena.core.network.INetworkChannelManager;
import com.communi.suggestu.scena.core.registries.IPlatformRegistryManager;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class FabricScenaPlatform implements IScenaPlatform
{
    private static final FabricScenaPlatform INSTANCE = new FabricScenaPlatform();

    public static FabricScenaPlatform getInstance()
    {
        return INSTANCE;
    }

    private FabricScenaPlatform()
    {
    }

    @Override
    public @NotNull IPlatformRegistryManager getPlatformRegistryManager()
    {
        return FabricRegistryManager.getInstance();
    }

    @Override
    public @NotNull IEntityInformationManager getEntityInformationManager()
    {
        return FabricEntityInformationManager.getInstance();
    }

    @Override
    public @NotNull IFluidManager getFluidManager()
    {
        return FabricFluidManager.getInstance();
    }

    @Override
    public @NotNull IClientManager getClientManager()
    {
        return FabricClientManager.getInstance();
    }

    @Override
    public @NotNull ILevelBasedPropertyAccessor getLevelBasedPropertyAccessor()
    {
        return FabricLevelBasedPropertyAccessor.getInstance();
    }

    @Override
    public @NotNull IItemComparisonHelper getItemComparisonHelper()
    {
        return FabricItemComparisonHelper.getInstance();
    }

    @Override
    public @NotNull IPlayerInventoryManager getPlayerInventoryManager()
    {
        return FabricPlayerInventoryManager.getInstance();
    }

    @Override
    public @NotNull IDistributionManager getDistributionManager()
    {
        return FabricDistributionManager.getInstance();
    }

    @Override
    public @NotNull INetworkChannelManager getNetworkChannelManager()
    {
        return FabricNetworkChannelManager.getInstance();
    }

    @Override
    public @NotNull IDyeItemHelper getDyeItemHelper()
    {
        return FabricDyeItemHelper.getInstance();
    }

    @Override
    public @NotNull IConfigurationManager getConfigurationManager()
    {
        return FabricConfigurationManager.getInstance();
    }

    @Override
    public @NotNull MinecraftServer getCurrentServer()
    {
        return FabricServerLifecycleManager.getInstance().getServer();
    }

    @Override
    public @NotNull ICreativeTabManager getCreativeTabManager()
    {
        return FabricCreativeTabManager.getInstance();
    }

    @Override
    public @NotNull IBlockEntityManager getBlockEntityManager()
    {
        return new IBlockEntityManager() {};
    }

    @Override
    public @NotNull IGameEvents getGameEvents() {
        return FabricGameEvents.getInstance();
    }
}
