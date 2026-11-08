package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;

/**
 * An event called after the world has been rendered.
 */
public interface IPostRenderWorldEvent extends IEvent {
    /**
     * Invoked when the world has rendered.
     *
     * @param levelRenderer The level renderer.
     * @param poseStack The pose stack.
     * @param collector The submit node collector.
     * @param levelRenderState The level render state.
     * @param partialTicks The partial ticks.
     */
    void handle(final LevelRenderer levelRenderer,
                final PoseStack poseStack,
                final SubmitNodeCollector collector,
                final LevelRenderState levelRenderState,
                final float partialTicks);
}
