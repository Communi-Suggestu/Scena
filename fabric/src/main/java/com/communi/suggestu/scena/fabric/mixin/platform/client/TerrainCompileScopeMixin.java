package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCpuBatch;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SectionCompiler.class)
public abstract class TerrainCompileScopeMixin
{
    @WrapMethod(
        method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;"
    )
    private SectionCompiler.Results scena$compile(final SectionPos pos, final RenderSectionRegion region, final VertexSorting sorting,
                                                  final SectionBufferBuilderPack buffers, final Operation<SectionCompiler.Results> original)
    {
        return TerrainCpuBatch.compile(() -> original.call(pos, region, sorting, buffers));
    }
}
