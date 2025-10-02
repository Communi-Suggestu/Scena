package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public abstract class BlockColorsResourceRegistrationEventMixin
{

    @Inject(
      method = "createDefault",
      at = @At("RETURN")
    )
    private static void onConstruction(final CallbackInfoReturnable<BlockColors> cir) {
        FabricClientEvents.RESOURCE_REGISTRATION.invoker().handle();
    }
}
