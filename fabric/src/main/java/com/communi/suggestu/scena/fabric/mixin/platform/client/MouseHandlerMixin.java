package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin
{

    @Inject(
        method = "onScroll",
        cancellable = true,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/ScrollWheelHandler;onMouseScroll(DD)Lorg/joml/Vector2i;"
        )
    )
    public void onScrollEventHook(
        final long windowPointer, final double xOffset, final double yOffset, final CallbackInfo ci, @Local(ordinal = 4) double d2) {
        if (FabricClientEvents.SCROLL.invoker().handle(d2))
        {
            ci.cancel();
        }
    }
}
