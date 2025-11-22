package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
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
        final boolean isScoping = FabricGameEvents.IS_PLAYER_SCOPING.invoker()
            .isScoping(player);
        if (isScoping) {
            cir.setReturnValue(true);
            return;
        }
    }
}
