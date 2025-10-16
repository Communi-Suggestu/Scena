package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;

/**
 * An event called after the world has been rendered.
 */
public interface IPostRenderWorldEvent extends IEvent {
    /**
     * Invoked when the world has rendered.
     *
     * @param levelRenderer The level renderer.
     * @param poseStack The pose stack.
     * @param partialTicks The partial ticks.
     */
    void handle(final LevelRenderer levelRenderer,
                final PoseStack poseStack,
                final MultiBufferSource.BufferSource buffer,
                final boolean translucentPass,
                final LevelRenderState levelRenderState,
                final float partialTicks);
}
