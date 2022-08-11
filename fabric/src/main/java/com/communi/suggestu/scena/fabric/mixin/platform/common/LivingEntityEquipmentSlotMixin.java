package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.core.item.IWearableItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEquipmentSlotMixin
{

    @Inject(
      method = "getEquipmentSlotForItem",
      at = @At("HEAD"),
      cancellable = true
    )
    private static void onGetEquipmentSlotForItem(final ItemStack stack, final CallbackInfoReturnable<EquipmentSlot> cir) {
        if (stack.getItem() instanceof IWearableItem wearableItem) {
            cir.setReturnValue(wearableItem.getSlot());
        }
    }
}
