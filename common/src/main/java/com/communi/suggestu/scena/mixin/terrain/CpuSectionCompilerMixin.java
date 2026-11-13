package com.communi.suggestu.scena.mixin.terrain;

import java.util.Map;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCpuBatch;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionCompiler.class)
public abstract class CpuSectionCompilerMixin
{
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
