package com.communi.suggestu.scena.forge.mixin.platform.client;

import java.util.List;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCpuBatch;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.SectionPos;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SectionCompiler.class)
public abstract class TerrainCompileScopeMixin
{
    @WrapMethod(
        method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;"
    )
    private SectionCompiler.Results scena$compile(final SectionPos pos, final RenderSectionRegion region, final VertexSorting sorting,
                                                  final SectionBufferBuilderPack buffers,
                                                  final List<AddSectionGeometryEvent.AdditionalSectionRenderer> additional,
                                                  final Operation<SectionCompiler.Results> original)
    {
        return TerrainCpuBatch.compile(() -> original.call(pos, region, sorting, buffers, additional));
    }
}
