package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;

import java.util.function.Consumer;

/**
 * An event fired when a highlight around a box is about to be drawn.
 */
@FunctionalInterface
public interface IDrawHighlightEvent extends IEvent {

    /**
     * Invoked when a highlight around a box is about to be drawn.
     */
    void handle(Consumer<CustomBlockOutlineRenderer> rendererRegistrar);

    interface CustomBlockOutlineRenderer {
        /**
         * Called when the block outline described by the provided {@link BlockOutlineRenderState} is about to be rendered.
         *
         * @return {@code true} to suppress vanilla outline rendering
         */
        boolean render(BlockOutlineRenderState renderState, SubmitNodeCollector submitNodeCollector, PoseStack poseStack, LevelRenderState levelRenderState);
    }
}
