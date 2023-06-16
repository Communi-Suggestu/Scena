package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import net.minecraft.client.gui.GuiGraphics;

/**
 * An event fired after the HUD has been rendered.
 */
@FunctionalInterface
public interface IHudRenderEvent extends IEvent {

    /**
     * Invoked after the HUD has been rendered.
     *
     * @param poseStack The posestack used to perform rendering.
     */
    void handle(GuiGraphics poseStack);
}
