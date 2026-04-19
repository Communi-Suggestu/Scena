package com.communi.suggestu.scena.forge.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.IExtendedGuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin implements IExtendedGuiGraphics
{

    @Override
    public void submitPip(final PictureInPictureRenderState renderState)
    {
        this.submitPictureInPictureRenderState(renderState);
    }

    @Shadow
    public abstract void submitPictureInPictureRenderState(final PictureInPictureRenderState state);

    @Override
    public @Nullable ScreenRectangle currentScissorArea()
    {
        return peekScissorStack();
    }

    @Shadow
    public abstract ScreenRectangle peekScissorStack();
}
