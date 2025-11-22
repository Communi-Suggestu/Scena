package com.communi.suggestu.scena.forge.mixin.platform.common;

import com.communi.suggestu.scena.forge.platform.event.ForgeGameEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerScopingMixin
{
    @Inject(
        method = "isScoping",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void scena$isScopingHandler(final CallbackInfoReturnable<Boolean> cir) {
        final Player player = (Player) ((Object) this);
        final ForgeGameEvents.ScenaInternalEvent.IsScoping event = ForgeGameEvents.ScenaInternalEvent.IsScoping.post(player);
        if (event.isScoping()) {
            cir.setReturnValue(true);
        }
    }
}
