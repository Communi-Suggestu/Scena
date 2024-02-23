package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerEventMixin
{
    @Inject(
      method = "onScroll",
      cancellable = true,
      at = @At(
        value = "FIELD",
        target = "Lnet/minecraft/client/MouseHandler;accumulatedScrollY:D",
        ordinal = 7,
        shift = At.Shift.AFTER
      )
    )
    private void onScroll(final long x, final double y, final double delta, final CallbackInfo callbackInfo)
    {
        double offset = delta;
        if (Minecraft.ON_OSX && delta == 0)
        {
            offset = delta;
        }
        double scrollDelta = (Minecraft.getInstance().options.discreteMouseScroll().get() ? Math.signum(offset) : offset) * Minecraft.getInstance().options.mouseWheelSensitivity().get();

        if (FabricClientEvents.SCROLL.invoker().handle(scrollDelta))
        {
            callbackInfo.cancel();
        }
    }
}
