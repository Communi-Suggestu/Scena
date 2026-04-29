package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.effect.FabricEffectManager;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    @Shadow
    @Final
    private BlockStatePredictionHandler blockStatePredictionHandler;

    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(
        method = "addDestroyBlockEffect",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;shouldSpawnTerrainParticles()Z"
        )
    )
    private boolean shouldInvokeCustomDestroyParticleSpawn(final BlockState instance, final Operation<Boolean> original, final BlockPos pos) {
        if (!FabricEffectManager.getInstance().getDestroyEffectHandlerMap()
            .containsKey(instance.getBlock())) {
            return original.call(instance);
        }

        final var destroyHandler = FabricEffectManager.getInstance().getDestroyEffectHandlerMap().get(instance.getBlock());
        return !destroyHandler.addDestroyEffects(
            instance,
            (Level) (Object) this,
            pos,
            this.minecraft.particleEngine
        );
    }

    @WrapOperation(
        method = "addBreakingBlockEffect",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;shouldSpawnTerrainParticles()Z"
        )
    )
    private boolean shouldInvokeCustomHitParticleSpawn(final BlockState instance, final Operation<Boolean> original, final BlockPos pos, final Direction direction) {
        if (!FabricEffectManager.getInstance().getHitEffectHandlerMap()
            .containsKey(instance.getBlock())) {
            return original.call(instance);
        }

        final var destroyHandler = FabricEffectManager.getInstance().getHitEffectHandlerMap().get(instance.getBlock());
        return !destroyHandler.addHitEffects(
            instance,
            (Level) (Object) this,
            pos,
            direction,
            this.minecraft.particleEngine
        );
    }
}
