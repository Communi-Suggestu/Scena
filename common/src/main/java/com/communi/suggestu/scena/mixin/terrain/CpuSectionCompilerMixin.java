package com.communi.suggestu.scena.mixin.terrain;

import java.util.Map;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCpuBatch;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionCompiler.class)
public abstract class CpuSectionCompilerMixin
{
    @WrapMethod(
        method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;"
    )
    private SectionCompiler.Results scena$batch(final SectionPos pos, final RenderSectionRegion region,
                                                final VertexSorting sorting, final SectionBufferBuilderPack buffers,
                                                final Operation<SectionCompiler.Results> original)
    {
        return TerrainCpuBatch.compile(pos, () -> original.call(pos, region, sorting, buffers));
    }

    @Inject(
        method = "getOrBeginLayer(Ljava/util/Map;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;)Lcom/mojang/blaze3d/vertex/BufferBuilder;",
        at = @At("RETURN"), cancellable = true
    )
    private void scena$rotate(final Map<ChunkSectionLayer, BufferBuilder> builders, final SectionBufferBuilderPack pack,
                              final ChunkSectionLayer layer, final CallbackInfoReturnable<BufferBuilder> cir)
    {
        cir.setReturnValue(TerrainCpuBatch.rotate(builders, layer, cir.getReturnValue()));
    }
}
