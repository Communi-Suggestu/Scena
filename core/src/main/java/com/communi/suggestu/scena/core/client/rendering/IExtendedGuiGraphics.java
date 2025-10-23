package com.communi.suggestu.scena.core.client.rendering;

import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

/**
 * Extension interface which adds new methods to the gui graphics.
 */
public interface IExtendedGuiGraphics
{
    /**
     * Submit the picture in picture render state to be rendered.
     *
     * @param renderState The render state for the pip.
     */
    void submitPip(PictureInPictureRenderState renderState);
}
