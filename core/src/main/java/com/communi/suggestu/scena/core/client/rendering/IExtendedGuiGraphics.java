package com.communi.suggestu.scena.core.client.rendering;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

import javax.annotation.Nullable;

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

    /**
     * Returns the current scissor area that is on the top of the scissor stack.
     *
     * @return The scissor stack
     */
    @Nullable
    public ScreenRectangle currentScissorArea();
}
