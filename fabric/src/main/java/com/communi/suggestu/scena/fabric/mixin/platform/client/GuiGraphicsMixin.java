package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.IGuiGraphicsTooltipHandler;
import com.communi.suggestu.scena.fabric.platform.client.tooltip.TooltipUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements IGuiGraphicsTooltipHandler {

    @Unique
    private ItemStack currentStack = ItemStack.EMPTY;

    @Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD")
    )
    public void storeItemStack(Font font, ItemStack stack, int mouseX, int mouseY, CallbackInfo ci) {
        this.currentStack = stack;
    }

    @Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("RETURN")
    )
    public void clearItemStack(Font font, ItemStack stack, int mouseX, int mouseY, CallbackInfo ci) {
        this.currentStack = ItemStack.EMPTY;
    }

    @Inject(method = "renderTooltipInternal", at = @At(value = "HEAD"))
    private void collectCustomComponentsInRenderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner, CallbackInfo ci) {
        TooltipUtils.gatherTooltipComponents(
                this.currentStack,
                components
        );
    }

    @Override
    public void scena$setCurrentStack(ItemStack stack) {
        this.currentStack = stack;
    }
}
