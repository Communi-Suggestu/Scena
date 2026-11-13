package com.communi.suggestu.scena.mixin.terrain;

import java.util.List;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainShardHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    @Shadow
    private SectionRenderDispatcher sectionRenderDispatcher;

    @WrapOperation(
        method = "prepareChunkRenders(Lorg/joml/Matrix4fc;)Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/SectionMesh;getSectionDraw(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;)Lnet/minecraft/client/renderer/chunk/SectionMesh$SectionDraw;")
    )
    private SectionMesh.SectionDraw scena$firstDraw(final SectionMesh mesh, final ChunkSectionLayer layer,
                                                   final Operation<SectionMesh.SectionDraw> original)
    {
        return original.call(TerrainShardHooks.first(mesh, layer), layer);
    }

    @WrapOperation(
        method = "prepareChunkRenders(Lorg/joml/Matrix4fc;)Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher;getRenderSectionSlice(Lnet/minecraft/client/renderer/chunk/SectionMesh;Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSectionBufferSlice;")
    )
    private SectionRenderDispatcher.RenderSectionBufferSlice scena$firstSlice(
        final SectionRenderDispatcher dispatcher, final SectionMesh mesh, final ChunkSectionLayer layer,
        final Operation<SectionRenderDispatcher.RenderSectionBufferSlice> original)
    {
        return original.call(dispatcher, TerrainShardHooks.first(mesh, layer), layer);
    }

    @WrapOperation(
        method = "prepareChunkRenders(Lorg/joml/Matrix4fc;)Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;",
        at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1)
    )
    private boolean scena$drawChildren(final List<?> draws, final Object value, final Operation<Boolean> original,
                                      @Local final SectionMesh mesh, @Local final ChunkSectionLayer layer)
    {
        if (!(value instanceof RenderPass.Draw<?> draw))
        {
            throw new IllegalStateException("Expected a native terrain draw");
        }
        return TerrainShardHooks.appendDraws(next -> original.call(draws, next), draw, mesh, layer, sectionRenderDispatcher);
    }
}
