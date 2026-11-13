package com.communi.suggestu.scena.mixin.terrain;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCompileGeneration;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainSectionAccess;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection$CompileTask")
public abstract class CompileTaskMixin
{
    @Shadow
    @Final
    private SectionRenderDispatcher.RenderSection this$1;

    @WrapMethod(
        method = "doTask(Lnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$SectionTask$SectionTaskResult;"
    )
    private SectionRenderDispatcher.RenderSection.SectionTask.SectionTaskResult scena$generation(
        final SectionBufferBuilderPack buffers,
        final Operation<SectionRenderDispatcher.RenderSection.SectionTask.SectionTaskResult> original)
    {
        return TerrainCompileGeneration.run((TerrainSectionAccess) this$1, () -> original.call(buffers));
    }
}
