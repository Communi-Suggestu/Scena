package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.IGuiGraphicsTooltipHandler;
import com.communi.suggestu.scena.fabric.platform.client.tooltip.TooltipUtils;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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

import java.util.ArrayList;
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

    @WrapMethod(method = "renderTooltipInternal")
    private void collectCustomComponentsInRenderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner, Operation<Void> original) {
        if (!this.currentStack.isEmpty()) {
            components = new ArrayList<>(components);

            TooltipUtils.gatherTooltipComponents(
                    this.currentStack,
                    components
            );
        }

        original.call(font, components, mouseX, mouseY, tooltipPositioner);
    }

    @Override
    public void scena$setCurrentStack(ItemStack stack) {
        this.currentStack = stack;
    }
}
