package com.communi.suggestu.scena.forge.platform.registry;

import com.communi.suggestu.scena.core.registries.IPlatformRegistry;
import com.communi.suggestu.scena.core.registries.IPlatformRegistryManager;
import com.communi.suggestu.scena.core.registries.ISizedIdMap;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrarManager;
import com.communi.suggestu.scena.forge.platform.registry.delegates.ForgeIdMapperPlatformDelegate;
import com.communi.suggestu.scena.forge.platform.registry.delegates.ForgeRegistryPlatformDelegate;
import com.communi.suggestu.scena.forge.platform.registry.registrar.ForgeRegistrarManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.GameData;

public class ForgeRegistryManager implements IPlatformRegistryManager
{
    private static final ForgeRegistryManager INSTANCE = new ForgeRegistryManager();

    public static ForgeRegistryManager getInstance()
    {
        return INSTANCE;
    }

    private final ForgeRegistryPlatformDelegate<Item>  itemRegistry  = new ForgeRegistryPlatformDelegate<>(BuiltInRegistries.ITEM);
    private final ForgeRegistryPlatformDelegate<Block>      blockRegistry      = new ForgeRegistryPlatformDelegate<>(BuiltInRegistries.BLOCK);
    private final ForgeIdMapperPlatformDelegate<BlockState> blockStateIdMapper = new ForgeIdMapperPlatformDelegate<>(GameData.getBlockStateIDMap());
    private final ForgeRegistryPlatformDelegate<Fluid>      fluidRegistry      = new ForgeRegistryPlatformDelegate<>(BuiltInRegistries.FLUID);

    private ForgeRegistryManager()
    {
    }

    @Override
    public IRegistrarManager getRegistrarManager()
    {
        return ForgeRegistrarManager.getInstance();
    }

    @Override
    public IPlatformRegistry<Item> getItemRegistry()
    {
        return itemRegistry;
    }

    @Override
    public IPlatformRegistry<Block> getBlockRegistry()
    {
        return blockRegistry;
    }

    @Override
    public ISizedIdMap<BlockState> getBlockStateIdMap()
    {
        return blockStateIdMapper;
    }

    @Override
    public IPlatformRegistry<Fluid> getFluids()
    {
        return fluidRegistry;
    }
}
