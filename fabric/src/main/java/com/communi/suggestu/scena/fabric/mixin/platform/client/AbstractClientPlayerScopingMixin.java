package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerScopingMixin
{

    @Inject(
        method = "getFieldOfViewModifier",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    public void getFieldOfViewModifierOnScoping(final boolean isFirstPerson, final float fovEffectScale, final CallbackInfoReturnable<Float> cir) {
        final Player player = (Player) ((Object) this);
        final boolean isScoping = FabricGameEvents.IS_PLAYER_SCOPING.invoker()
            .isScoping(player);
        if (isScoping) {
            cir.setReturnValue(0.1F);
        }
    }
}
