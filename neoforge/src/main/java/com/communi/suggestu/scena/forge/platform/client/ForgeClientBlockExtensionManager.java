package com.communi.suggestu.scena.forge.platform.client;

import com.communi.suggestu.scena.core.client.effect.IEffectManager;
import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public class ForgeClientBlockExtensionManager
{
    private static final ForgeClientBlockExtensionManager INSTANCE = new ForgeClientBlockExtensionManager();

    public static ForgeClientBlockExtensionManager getInstance()
    {
        return INSTANCE;
    }

    private final List<Consumer<IColorManager.IDynamicBlockColorSetter>> dynamicBlockColorSetters = Collections.synchronizedList(new ArrayList<>());
    private final List<Consumer<IEffectManager.IHitEffectRegistrar>>     hitEffectRegistrars      = Collections.synchronizedList(new ArrayList<>());
    private final List<Consumer<IEffectManager.IDestroyEffectRegistrar>> destroyEffectRegistrars  = Collections.synchronizedList(new ArrayList<>());

    public void setupDynamicBlockColors(final Consumer<IColorManager.IDynamicBlockColorSetter> setter)
    {
        dynamicBlockColorSetters.add(setter);
    }

    public void setupHitEffectRegistrar(final Consumer<IEffectManager.IHitEffectRegistrar> setter)
    {
        hitEffectRegistrars.add(setter);
    }

    public void setupDestroyEffectRegistrar(final Consumer<IEffectManager.IDestroyEffectRegistrar> setter)
    {
        destroyEffectRegistrars.add(setter);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientBlockExtensionHandler(final RegisterClientExtensionsEvent event)
    {
        final Map<Block, IColorManager.IDynamicColorProvider> blockToColorProviders = new HashMap<>();
        final Map<Block, IEffectManager.IHitEffectHandler> blockToHitEffectHandler = new HashMap<>();
        final Map<Block, IEffectManager.IDestroyEffectHandler> blockToDestroyHandler = new HashMap<>();
        final Set<Block> blocks = new HashSet<>();

        getInstance().dynamicBlockColorSetters.forEach(dynamicBlockColorSetter -> dynamicBlockColorSetter.accept(
            (colorManager, blocks1) -> Arrays.stream(blocks1)
                .forEach(block -> {
                    blockToColorProviders.put(block, colorManager);
                    blocks.add(block);
                })));
        getInstance().hitEffectRegistrars.forEach(hitEffectRegistrar -> hitEffectRegistrar.accept(
            (handler, blocks1) -> Arrays.stream(blocks1)
                .forEach(block -> {
                    blockToHitEffectHandler.put(block, handler);
                    blocks.add(block);
                })
        ));
        getInstance().destroyEffectRegistrars.forEach(destroyEffectRegistrar -> destroyEffectRegistrar.accept(
            (handler, blocks1) -> Arrays.stream(blocks1)
                .forEach(block -> {
                    blockToDestroyHandler.put(block, handler);
                    blocks.add(block);
                })
        ));

        var handler = new IClientBlockExtensions() {
            @Override
            public void collectDynamicTintValues(final BlockState state, final @NonNull BlockAndTintGetter level, final @NonNull BlockPos pos, final @NonNull IntList tintValues)
            {
                if (!blockToColorProviders.containsKey(state.getBlock()))
                    return;

                blockToColorProviders.get(state.getBlock())
                    .collect(state, level, pos, tintValues);
            }

            @Override
            public boolean addHitEffects(final BlockState state, final @NonNull Level level, @Nullable final HitResult target, final @NonNull ParticleEngine manager)
            {
                if (!blockToHitEffectHandler.containsKey(state.getBlock()))
                    return IClientBlockExtensions.super.addHitEffects(state, level, target, manager);

                if (!(target instanceof BlockHitResult blockHitResult) || target.getType() == HitResult.Type.MISS)
                    return IClientBlockExtensions.super.addHitEffects(state, level, target, manager);

                return blockToHitEffectHandler.get(state.getBlock())
                    .addHitEffects(state, level, blockHitResult.getBlockPos(), blockHitResult.getDirection(), manager);
            }

            @Override
            public boolean addDestroyEffects(final BlockState state, final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull ParticleEngine manager)
            {
                if (!blockToDestroyHandler.containsKey(state.getBlock()))
                    return IClientBlockExtensions.super.addDestroyEffects(state, level, pos, manager);

                return blockToDestroyHandler.get(state.getBlock())
                    .addDestroyEffects(state, level, pos, manager);
            }
        };

        event.registerBlock(handler, blocks.toArray(Block[]::new));
    }
}
