package com.communi.suggestu.scena.forge.platform.fluid;

import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.IFluidManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ForgeFluidManager implements IFluidManager
{
    private static final ForgeFluidManager INSTANCE = new ForgeFluidManager();

    public static ForgeFluidManager getInstance()
    {
        return INSTANCE;
    }


    private ForgeFluidManager()
    {
    }


    @Override
    public Optional<FluidInformation> get(final ItemStack stack)
    {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                 .map(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE))
                 .map(fluidStack -> new FluidInformation(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.isEmpty() ? new CompoundTag() : fluidStack.getOrCreateTag()));
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount)
    {
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
          .ifPresent(handler -> handler.drain((int) amount, IFluidHandler.FluidAction.EXECUTE));

        return stack;
    }

    @Override
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation)
    {
        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
          .ifPresent(handler -> handler.fill(buildFluidStack(fluidInformation), IFluidHandler.FluidAction.EXECUTE));

        return stack;
    }

    @Override
    public Component getDisplayName(final Fluid fluid)
    {
        return fluid.getFluidType().getDescription();
    }

    @NotNull
    public FluidStack buildFluidStack(final FluidInformation fluid)
    {
        if (fluid.data() == null)
            return new FluidStack(fluid.fluid(), (int) fluid.amount());

        return new FluidStack(fluid.fluid(), (int) fluid.amount(), fluid.data());
    }
}
