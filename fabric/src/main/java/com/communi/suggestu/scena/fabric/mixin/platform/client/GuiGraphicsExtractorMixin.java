package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.IExtendedGuiGraphics;
import com.communi.suggestu.scena.fabric.platform.client.rendering.IGuiGraphicsTooltipHandler;
import com.communi.suggestu.scena.fabric.platform.client.tooltip.TooltipUtils;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin implements IGuiGraphicsTooltipHandler, IExtendedGuiGraphics
{

    @Unique
    private ItemStack currentStack = ItemStack.EMPTY;

    @Shadow
    @Final
    public GuiRenderState guiRenderState;

    @Shadow
    @Final
    public GuiGraphicsExtractor.ScissorStack scissorStack;

    @Inject(
            method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD")
    )
    public void storeItemStack(Font font, ItemStack itemStack, int xo, int yo, CallbackInfo ci) {
        this.currentStack = itemStack;
    }

    @Inject(
            method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("RETURN")
    )
    public void clearItemStack(Font font, ItemStack itemStack, int xo, int yo, CallbackInfo ci) {
        this.currentStack = ItemStack.EMPTY;
    }

    @WrapMethod(method = "setTooltipForNextFrameInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;ZZ)V")
    private void collectCustomComponentsInRenderTooltipInternal(
            Font font,
            List<ClientTooltipComponent> lines,
            int xo,
            int yo,
            ClientTooltipPositioner positioner,
            @Nullable Identifier style,
            boolean replaceExisting,
            boolean extraSpaceAfterFirstLine,
            Operation<Void> original) {
        if (!this.currentStack.isEmpty()) {
            lines = new ArrayList<>(lines);

            TooltipUtils.gatherTooltipComponents(
                    this.currentStack,
                    lines
            );
        }

        original.call(font, lines, xo, yo, positioner, style, replaceExisting, extraSpaceAfterFirstLine);
    }

    @Override
    public void scena$setCurrentStack(ItemStack stack) {
        this.currentStack = stack;
    }

    @Override
    public void submitPip(final PictureInPictureRenderState renderState)
    {
        this.guiRenderState.addPicturesInPictureState(renderState);
    }

    @Override
    public ScreenRectangle currentScissorArea()
    {
        return this.scissorStack.peek();
    }
}
