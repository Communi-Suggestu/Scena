package com.communi.suggestu.scena.forge.mixin.compat.shaders.sodium;

import com.communi.suggestu.scena.core.client.models.baked.BlockStateAwareQuad;
import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        AbstractBlockRenderContext.class
)
public class MixinAbstractBlockRenderContext {

    @Shadow
    protected BlockPos pos;

    @Shadow
    protected BlockAndTintGetter level;

    @Inject(method = "bufferDefaultModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/frapi/mesh/MutableQuadViewImpl;fromVanilla(Lnet/minecraft/client/renderer/block/model/BakedQuad;Lnet/fabricmc/fabric/api/renderer/v1/material/RenderMaterial;Lnet/minecraft/core/Direction;)Lnet/caffeinemc/mods/sodium/client/render/frapi/mesh/MutableQuadViewImpl;"
            )
    )
    private void beforeEmitVanillaQuad(BakedModel model, BlockState state, CallbackInfo ci, @Local BakedQuad q) {
        if ((Object) this instanceof BlockRenderer r && WorldRenderingSettings.INSTANCE.getBlockStateIds() != null && q instanceof BlockStateAwareQuad blockStateAwareQuad) {
            final BlockState blockState = blockStateAwareQuad.getBlockState();
            ((BlockSensitiveBufferBuilder) ((BlockRendererAccessor) r).getBuffers()).endBlock();
            ((BlockSensitiveBufferBuilder) ((BlockRendererAccessor) r).getBuffers()).beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(blockState), (byte) 0, (byte) blockState.getLightEmission(), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
