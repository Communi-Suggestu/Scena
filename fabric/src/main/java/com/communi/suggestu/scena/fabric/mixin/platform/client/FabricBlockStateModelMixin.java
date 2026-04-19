package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.DataAwareBlockStateModel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FabricBlockStateModel.class)
public interface FabricBlockStateModelMixin
{
    @Shadow
    Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state);

    @WrapOperation(
        method = "emitQuads(Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Ljava/util/function/Predicate;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;collectParts(Lnet/minecraft/util/RandomSource;Ljava/util/List;)V"
        )
    )
    private void adaptCollectParts(
        final BlockStateModel instance,
        final RandomSource randomSource,
        final List<BlockStateModelPart> blockStateModelParts,
        final Operation<Void> original,
        @Local(argsOnly = true, name = "level") BlockAndTintGetter level,
        @Local(argsOnly = true, name = "pos") BlockPos pos,
        @Local(argsOnly = true, name = "state") BlockState state) {

        if (!(this instanceof DataAwareBlockStateModel dataAwareBlockStateModel))
        {
            original.call(instance, randomSource, blockStateModelParts);
            return;
        }

        dataAwareBlockStateModel.collectParts(
            level,
            pos,
            state,
            randomSource,
            blockStateModelParts
        );
    }

    @Inject(
        method = "particleMaterial(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/sprite/Material$Baked;",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void adaptParticleMaterial(final BlockAndTintGetter level, final BlockPos pos, final BlockState state, final CallbackInfoReturnable<Material.Baked> cir) {
        if (this instanceof DataAwareBlockStateModel dataAwareBlockStateModel) {
            cir.setReturnValue(
                dataAwareBlockStateModel.particleMaterial(level, pos, state)
            );
        }
    }

    @Inject(
        method = "materialFlags(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)I",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void adaptMaterialFlags(
        final BlockAndTintGetter level,
        final BlockPos pos,
        final BlockState state,
        final RandomSource random,
        final CallbackInfoReturnable<Integer> cir) {
        if (this instanceof DataAwareBlockStateModel dataAwareBlockStateModel) {
            cir.setReturnValue(
                dataAwareBlockStateModel.materialFlags(level, pos, state)
            );
        }
    }
}
