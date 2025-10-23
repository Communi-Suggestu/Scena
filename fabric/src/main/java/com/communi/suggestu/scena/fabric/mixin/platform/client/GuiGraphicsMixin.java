package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.IExtendedGuiGraphics;
import com.communi.suggestu.scena.fabric.platform.client.rendering.IGuiGraphicsTooltipHandler;
import com.communi.suggestu.scena.fabric.platform.client.tooltip.TooltipUtils;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements IGuiGraphicsTooltipHandler, IExtendedGuiGraphics
{

    @Unique
    private ItemStack currentStack = ItemStack.EMPTY;

    @Shadow
    @Final
    public GuiRenderState guiRenderState;

    @Shadow
    @Final
    public GuiGraphics.ScissorStack scissorStack;

    @Inject(
            method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD")
    )
    public void storeItemStack(Font font, ItemStack stack, int mouseX, int mouseY, CallbackInfo ci) {
        this.currentStack = stack;
    }

    @Inject(
            method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("RETURN")
    )
    public void clearItemStack(Font font, ItemStack stack, int mouseX, int mouseY, CallbackInfo ci) {
        this.currentStack = ItemStack.EMPTY;
    }

    @WrapMethod(method = "setTooltipForNextFrameInternal")
    private void collectCustomComponentsInRenderTooltipInternal(
        final Font font,
        List<ClientTooltipComponent> components,
        final int x,
        final int y,
        final ClientTooltipPositioner positioner,
        final ResourceLocation background,
        final boolean focused,
        final Operation<Void> original) {
        if (!this.currentStack.isEmpty()) {
            components = new ArrayList<>(components);

            TooltipUtils.gatherTooltipComponents(
                    this.currentStack,
                    components
            );
        }

        original.call(font, components, x, y, positioner, background, focused);
    }

    @Override
    public void scena$setCurrentStack(ItemStack stack) {
        this.currentStack = stack;
    }

    @Override
    public void submitPip(final PictureInPictureRenderState renderState)
    {
        this.guiRenderState.submitPicturesInPictureState(renderState);
    }

    @Override
    public ScreenRectangle currentScissorArea()
    {
        return this.scissorStack.peek();
    }
}
