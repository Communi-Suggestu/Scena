package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.IGuiGraphicsTooltipHandler;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Inject(
            method = "extractTooltip(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"
            )
    )
    private void preRenderTooltipInContainer(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci, @Local(name = "item") ItemStack item) {
        final IGuiGraphicsTooltipHandler handler = ((IGuiGraphicsTooltipHandler) graphics);
        handler.scena$setCurrentStack(item);
    }

    @Inject(
            method = "extractTooltip(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
            at = @At(
                    value = "RETURN"
            )
    )
    private void postRenderTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        final IGuiGraphicsTooltipHandler handler = ((IGuiGraphicsTooltipHandler) graphics);
        handler.scena$setCurrentStack(ItemStack.EMPTY);
    }
}
