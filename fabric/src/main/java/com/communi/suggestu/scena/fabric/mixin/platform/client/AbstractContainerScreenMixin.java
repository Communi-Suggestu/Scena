package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.IGuiGraphicsTooltipHandler;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Inject(
            method = "renderTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"
            )
    )
    private void preRenderTooltipInContainer(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci, @Local ItemStack itemStack) {
        final IGuiGraphicsTooltipHandler handler = ((IGuiGraphicsTooltipHandler) guiGraphics);
        handler.scena$setCurrentStack(itemStack);
    }

    @Inject(
            method = "renderTooltip",
            at = @At(
                    value = "RETURN"
            )
    )
    private void postRenderTooltip(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        final IGuiGraphicsTooltipHandler handler = ((IGuiGraphicsTooltipHandler) guiGraphics);
        handler.scena$setCurrentStack(ItemStack.EMPTY);
    }
}
