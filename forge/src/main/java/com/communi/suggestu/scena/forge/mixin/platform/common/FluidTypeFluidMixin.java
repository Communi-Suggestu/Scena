package com.communi.suggestu.scena.forge.mixin.platform.common;

import com.communi.suggestu.scena.core.fluid.FluidWithHandler;
import com.communi.suggestu.scena.forge.platform.fluid.ForgeFluidTypeDelegate;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Fluid.class)
public abstract class FluidTypeFluidMixin
{

    @SuppressWarnings("ConstantConditions")
    @Unique
    public Fluid getThis() {
        return (Fluid) (Object) this;
    }

    @Inject(
            method = "getFluidType",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void onGetFluidType(final CallbackInfoReturnable<FluidType> cir) {
        if (getThis() instanceof FluidWithHandler withHandler) {
            cir.setReturnValue(new ForgeFluidTypeDelegate(withHandler.getVariantHandler()));
        }
    }
}
