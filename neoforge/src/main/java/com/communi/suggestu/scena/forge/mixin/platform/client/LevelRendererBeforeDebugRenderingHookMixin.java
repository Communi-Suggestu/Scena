package com.communi.suggestu.scena.forge.mixin.platform.client;

import com.communi.suggestu.scena.core.client.integration.IOptifineCompatibilityManager;
import com.communi.suggestu.scena.forge.platform.client.event.ForgeScenaRenderWorldLastEvent;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererBeforeDebugRenderingHookMixin
{
    @Unique
    private boolean didRenderParticles;

    @Inject(
      method = {"renderLevel"},
      at = {@At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V"
      )}
    )
    private void onRenderParticles(CallbackInfo ci) {
        this.didRenderParticles = !IOptifineCompatibilityManager.getInstance().isInstalled();
    }

    @Inject(
      method = {"renderLevel"},
      at = {@At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V"
      )}
    )
    private void beforeDebugRender(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local PoseStack poseStack) {
        if (!didRenderParticles)
            return;

        didRenderParticles = false;
        final LevelRenderer levelRenderer = (LevelRenderer) (Object) this;

        NeoForge.EVENT_BUS.post(new ForgeScenaRenderWorldLastEvent(levelRenderer, camera.getPartialTickTime(), poseStack));
    }
}
