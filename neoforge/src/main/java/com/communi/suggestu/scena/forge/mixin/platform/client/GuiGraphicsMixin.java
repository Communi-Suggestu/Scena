package com.communi.suggestu.scena.forge.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.IExtendedGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements IExtendedGuiGraphics
{

    @Override
    public void submitPip(final PictureInPictureRenderState renderState)
    {
        this.submitPictureInPictureRenderState(renderState);
    }

    @Shadow
    public abstract void submitPictureInPictureRenderState(final PictureInPictureRenderState par1);
}
