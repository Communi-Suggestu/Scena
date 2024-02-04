package com.communi.suggestu.scena.forge.platform.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.neoforged.bus.api.Event;

public class ForgeScenaRenderWorldLastEvent extends Event {
    private final LevelRenderer levelRenderer;
    private final float partialTicks;
    private final PoseStack poseStack;

    public ForgeScenaRenderWorldLastEvent(LevelRenderer levelRenderer, float partialTicks, PoseStack poseStack) {
        this.levelRenderer = levelRenderer;
        this.partialTicks = partialTicks;
        this.poseStack = poseStack;
    }

    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
