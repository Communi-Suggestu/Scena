package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityEventMixin
{
    @Shadow public abstract ItemStack getItem();

    @Unique
    private ItemEntity getThis() {
        return (ItemEntity) (Object) this;
    }

    @Inject(
            method = "playerTouch",
            cancellable = true,
            at = @At(
                    value = "HEAD"
            )
    )
    private void onScroll(final Player player, final CallbackInfo ci)
    {
        if (FabricGameEvents.ENTITY_ITEM_PICKUP.invoker().handle(getThis(), player)) {
            ci.cancel();
        }
    }
}
