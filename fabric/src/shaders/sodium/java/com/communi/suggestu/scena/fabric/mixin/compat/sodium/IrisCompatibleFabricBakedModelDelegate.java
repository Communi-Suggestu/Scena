package com.communi.suggestu.scena.fabric.mixin.compat.sodium;

import com.communi.suggestu.scena.core.client.models.baked.BlockStateAwareQuad;
import com.communi.suggestu.scena.core.client.models.baked.IDataAwareBakedModel;
import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader.FabricBakedModelDelegate;
import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(FabricBakedModelDelegate.class)
public class IrisCompatibleFabricBakedModelDelegate {

    @Inject(
            method = "emitBlockQuads(Lcom/communi/suggestu/scena/core/client/models/baked/IDataAwareBakedModel;Lcom/communi/suggestu/scena/core/client/models/data/IBlockModelData;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;fromVanilla(Lnet/minecraft/client/renderer/block/model/BakedQuad;Lnet/fabricmc/fabric/api/renderer/v1/material/RenderMaterial;Lnet/minecraft/core/Direction;)Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;"
            )
    )
    private void emitBlockQuads(IDataAwareBakedModel dataAwareBakedModel, IBlockModelData blockModelData, BlockState blockState, BlockPos blockPos, Direction direction, Supplier<RandomSource> supplier, RenderContext renderContext, RenderType renderType, CallbackInfo ci, @Local BakedQuad quad) {
        if (renderContext instanceof BlockRendererAccessor blockRendererAccessor &&
                blockRendererAccessor.getBuffers() instanceof BlockSensitiveBufferBuilder blockSensitiveBufferBuilder &&
                quad instanceof BlockStateAwareQuad blockStateAwareQuad &&
                WorldRenderingSettings.INSTANCE.getBlockStateIds() != null
        ) {
            blockSensitiveBufferBuilder.endBlock();
            blockSensitiveBufferBuilder.beginBlock(
                    WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(blockStateAwareQuad.getBlockState()), (byte) 0, (byte) blockState.getLightEmission(), blockPos.getX(), blockPos.getY(), blockPos.getZ()
            );
        }

    }
}
