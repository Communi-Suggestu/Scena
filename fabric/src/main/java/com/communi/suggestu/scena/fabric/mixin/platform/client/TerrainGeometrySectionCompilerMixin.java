package com.communi.suggestu.scena.fabric.mixin.platform.client;

import java.util.Map;

import com.communi.suggestu.scena.core.client.rendering.terrain.ITerrainGeometryContext;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainGeometryBlockStateModel;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainGeometryDispatcher;
import com.communi.suggestu.scena.core.client.rendering.terrain.VanillaTerrainGeometryContext;
import com.communi.suggestu.scena.fabric.platform.client.rendering.NativeTerrainCompatibility;
import com.communi.suggestu.scena.fabric.platform.client.rendering.ResolvedTerrainModel;
import com.communi.suggestu.scena.fabric.platform.client.rendering.ClippedTerrainOutput;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public abstract class TerrainGeometrySectionCompilerMixin
{
    @Shadow
    @Final
    private boolean cutoutLeaves;

    @Shadow
    @Final
    private boolean ambientOcclusion;

    @Shadow
    @Final
    private BlockColors blockColors;

    @Shadow
    protected abstract BufferBuilder getOrBeginLayer(
        Map<ChunkSectionLayer, BufferBuilder> startedLayers, SectionBufferBuilderPack buffers, ChunkSectionLayer layer
    );

    @WrapOperation(
        method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V"
        )
    )
    private void scena$emitNativeTerrainGeometry(
        final ModelBlockRenderer renderer,
        final BlockQuadOutput output,
        final float x,
        final float y,
        final float z,
        final BlockAndTintGetter region,
        final BlockPos pos,
        final BlockState state,
        final BlockStateModel model,
        final long seed,
        final Operation<Void> original,
        @Local final Map<ChunkSectionLayer, BufferBuilder> startedLayers,
        @Local(argsOnly = true) final SectionBufferBuilderPack buffers
    )
    {
        if (!(model instanceof TerrainGeometryBlockStateModel geometryModel))
        {
            original.call(renderer, output, x, y, z, region, pos, state, model, seed);
            return;
        }

        final ITerrainGeometryContext context = new VanillaTerrainGeometryContext(
            region,
            pos,
            state,
            bakedModel -> original.call(renderer, output, x, y, z, region, pos, state, bakedModel, seed),
            nativeModel -> null,
            geometry -> !NativeTerrainCompatibility.supports(geometry) ? null : () -> {
                final ResolvedTerrainModel resolved =
                    new ResolvedTerrainModel(geometry, ModelBlockRenderer.forceOpaque(cutoutLeaves, state));
                if (geometry.parts().stream().flatMap(part -> part.quads().stream()).noneMatch(quad -> quad.clipRegions() != null))
                {
                    original.call(renderer, output, x, y, z, region, pos, state, resolved, seed);
                    return;
                }
                final var offset = state.getOffset(pos);
                final ClippedTerrainOutput clipped = new ClippedTerrainOutput(
                    resolved, layer -> getOrBeginLayer(startedLayers, buffers, layer),
                    (float) (x + offset.x), (float) (y + offset.y), (float) (z + offset.z)
                );
                final var emitter = Renderer.get().quadEmitter(clipped::accept);
                Renderer.get().altModelBlockRenderer(ambientOcclusion, true, blockColors)
                    .tesselateBlock(emitter, x, y, z, region, pos, state, resolved, seed);
            }
        );
        TerrainGeometryDispatcher.emitOrFallback(
            geometryModel,
            context,
            () -> original.call(renderer, output, x, y, z, region, pos, state, model, seed)
        );
    }
}
