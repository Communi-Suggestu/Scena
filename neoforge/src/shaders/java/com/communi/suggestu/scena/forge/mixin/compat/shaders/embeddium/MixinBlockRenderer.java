package com.communi.suggestu.scena.forge.mixin.compat.shaders.embeddium;

import com.communi.suggestu.scena.core.client.models.baked.BlockStateAwareQuad;
import com.llamalad7.mixinextras.sugar.Local;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.A;
import org.embeddedt.embeddium.api.render.chunk.BlockRenderContext;
import org.embeddedt.embeddium.impl.model.color.ColorProvider;
import org.embeddedt.embeddium.impl.model.light.LightPipeline;
import org.embeddedt.embeddium.impl.model.quad.BakedQuadView;
import org.embeddedt.embeddium.impl.render.chunk.compile.ChunkBuildBuffers;
import org.embeddedt.embeddium.impl.render.chunk.compile.buffers.ChunkModelBuilder;
import org.embeddedt.embeddium.impl.render.chunk.compile.pipeline.BlockRenderer;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockRenderer.class)
public class MixinBlockRenderer {

    @Unique
    private ChunkBuildBuffers builders = null;

    @Inject(
            method="renderModel",
            at = @At("HEAD")
    )
    public void onRenderModel(BlockRenderContext ctx, ChunkBuildBuffers buffers, CallbackInfo ci) {
        this.builders = buffers;
    }


    @Inject(
            method="renderModel",
            at = @At("RETURN")
    )
    public void leaveRenderModel(BlockRenderContext ctx, ChunkBuildBuffers buffers, CallbackInfo ci) {
        this.builders = null;
    }

    @Inject(
        method = "renderQuadList",
        at = @At(
            value = "INVOKE",
            target = "Lorg/embeddedt/embeddium/impl/render/chunk/compile/pipeline/BlockRenderer;writeGeometry(Lorg/embeddedt/embeddium/api/render/chunk/BlockRenderContext;Lorg/embeddedt/embeddium/impl/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/minecraft/world/phys/Vec3;Lorg/embeddedt/embeddium/impl/render/chunk/terrain/material/Material;Lorg/embeddedt/embeddium/impl/model/quad/BakedQuadView;[ILorg/embeddedt/embeddium/impl/model/light/data/QuadLightData;)V"
        )
    )
    public void onRenderQuad(BlockRenderContext ctx, Material material, LightPipeline lighter, ColorProvider<BlockState> colorizer, Vec3 offset, ChunkModelBuilder builder, List<BakedQuad> quads, Direction cullFace, CallbackInfo ci, @Local BakedQuadView quad) {
        if ((Object) this instanceof BlockRenderer r && WorldRenderingSettings.INSTANCE.getBlockStateIds() != null && quad instanceof BlockStateAwareQuad blockStateAwareQuad && this.builders != null) {
            final BlockState blockState = blockStateAwareQuad.getBlockState();
            ((BlockSensitiveBufferBuilder) this.builders).endBlock();
            ((BlockSensitiveBufferBuilder) this.builders).beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(blockState), (byte) 0, (byte) blockState.getLightEmission(), ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ());
        }
    }
}
