package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import com.mojang.blaze3d.platform.MacosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.ScrollWheelHandler;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScrollWheelHandler.class)
public abstract class ScrollWheelHandlerEventMixin
{
    @Inject(
      method = "onMouseScroll",
      cancellable = true,
      at = @At(
        value = "FIELD",
        target = "Lnet/minecraft/client/ScrollWheelHandler;accumulatedScrollY:D",
        ordinal = 7,
        shift = At.Shift.AFTER
      )
    )
    private void onScroll(final double xOffset, final double delta, final CallbackInfoReturnable<Vector2i> cir)
    {
        double offset = delta;
        if (MacosUtil.IS_MACOS && delta == 0)
        {
            offset = delta;
        }
        double scrollDelta = (Minecraft.getInstance().options.discreteMouseScroll().get() ? Math.signum(offset) : offset) * Minecraft.getInstance().options.mouseWheelSensitivity().get();

        if (FabricClientEvents.SCROLL.invoker().handle(scrollDelta))
        {
            cir.cancel();
        }
    }
}
