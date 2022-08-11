package com.communi.suggestu.scena.core.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public record FluidInformation(Fluid fluid, long amount, CompoundTag data)
{
    public FluidInformation(Fluid fluid) {
        this(fluid, 1, new CompoundTag());
    }

    public FluidInformation(Fluid fluid, long amount) {
        this(fluid, amount, new CompoundTag());
    }

    public FluidInformation withSource() {
        if (fluid instanceof FlowingFluid flowingFluid) {
            return new FluidInformation(flowingFluid.getSource(), amount, data);
        }

        return this;
    }
}
