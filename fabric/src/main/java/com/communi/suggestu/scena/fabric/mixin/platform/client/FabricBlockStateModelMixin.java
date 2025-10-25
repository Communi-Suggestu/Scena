package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.DataAwareBlockStateModel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(FabricBlockStateModel.class)
public abstract class FabricBlockStateModelMixin
{
    @WrapOperation(
        method = "emitQuads(Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Ljava/util/function/Predicate;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/model/BlockStateModel;collectParts(Lnet/minecraft/util/RandomSource;)Ljava/util/List;"
        )
    )
    private List<BlockModelPart> adaptCollectParts(final BlockStateModel instance, final RandomSource random, final Operation<List<BlockModelPart>> original, @Local(argsOnly = true) BlockAndTintGetter blockAndTintGetter, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) BlockState blockState) {
        if (!(this instanceof DataAwareBlockStateModel dataAwareBlockStateModel))
            return original.call(instance, random);

        final List<BlockModelPart> result = new ArrayList<>();
        dataAwareBlockStateModel.collectParts(
            blockAndTintGetter,
            blockPos,
            blockState,
            random,
            result
        );

        return result;
    }

    @Inject(
        method = "particleSprite(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void adaptParticleSprite(final BlockAndTintGetter blockView, final BlockPos pos, final BlockState state, final CallbackInfoReturnable<TextureAtlasSprite> cir) {
        if (this instanceof DataAwareBlockStateModel dataAwareBlockStateModel) {
            cir.setReturnValue(
                dataAwareBlockStateModel.particleIcon(blockView, pos, state)
            );
        }
    }
}
