package com.communi.suggestu.scena.forge.platform.fluid;

import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.FluidRegistration;
import com.communi.suggestu.scena.core.fluid.FluidWithHandler;
import com.communi.suggestu.scena.core.fluid.IFluidManager;
import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

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
    public FluidRegistration registerFluidAndVariant(final ResourceLocation name, final Supplier<FluidWithHandler> fluid, final Supplier<IFluidVariantHandler> variantHandler)
    {
        final IFluidVariantHandler handler = variantHandler.get();
        final IRegistrar<FluidType> fluidTypeRegistrar = IRegistrar.create(ForgeRegistries.Keys.FLUID_TYPES, name.getNamespace());
        final IRegistryObject<FluidType> fluidTypeRegistration = fluidTypeRegistrar.register(name.getPath(), () -> new ForgeFluidTypeDelegate(handler));

        final IRegistrar<Fluid> fluidRegistrar = IRegistrar.create(ForgeRegistries.Keys.FLUIDS, name.getNamespace());
        final IRegistryObject<Fluid> fluidRegistration = fluidRegistrar.register(name.getPath(), fluid);

        return new FluidRegistration(fluidRegistration, Suppliers.memoize(() -> new ForgeFluidVariantHandlerDelegate(fluidTypeRegistration.get())));
    }

    @Override
    public Optional<IFluidVariantHandler> getVariantHandlerFor(final Fluid fluid)
    {
        return Optional.of(new ForgeFluidVariantHandlerDelegate(fluid.getFluidType()));
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
    public static FluidStack buildFluidStack(final FluidInformation fluid)
    {
        if (fluid.data() == null)
            return new FluidStack(fluid.fluid(), (int) fluid.amount());

        return new FluidStack(fluid.fluid(), (int) fluid.amount(), fluid.data());
    }

    @NotNull
    public static FluidInformation buildFluidInformation(final FluidStack fluid)
    {
        if (fluid.getTag() == null)
            return new FluidInformation(fluid.getFluid(), (int) fluid.getAmount());

        return new FluidInformation(fluid.getFluid(), (int) fluid.getAmount(), fluid.getOrCreateTag());
    }


}
