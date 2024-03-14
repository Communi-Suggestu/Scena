package com.communi.suggestu.scena.forge.platform.fluid;

import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.FluidRegistration;
import com.communi.suggestu.scena.core.fluid.FluidWithHandler;
import com.communi.suggestu.scena.core.fluid.IFluidManager;
import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import com.google.common.base.Suppliers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class ForgeFluidManager implements IFluidManager {

    private static final ForgeFluidManager INSTANCE = new ForgeFluidManager();

    public static ForgeFluidManager getInstance() {
        return INSTANCE;
    }

    private ForgeFluidManager() {
    }

    @Override
    public FluidRegistration registerFluidAndVariant(final ResourceLocation name, final Supplier<FluidWithHandler> fluid, final Supplier<IFluidVariantHandler> variantHandler) {
        final IFluidVariantHandler handler = variantHandler.get();
        final IRegistrar<FluidType> fluidTypeRegistrar = IRegistrar.create(NeoForgeRegistries.FLUID_TYPES.key(), name.getNamespace());
        final IRegistryObject<FluidType> fluidTypeRegistration = fluidTypeRegistrar.register(name.getPath(), () -> new ForgeFluidTypeDelegate(handler));

        final IRegistrar<Fluid> fluidRegistrar = IRegistrar.create(Registries.FLUID, name.getNamespace());
        final IRegistryObject<Fluid> fluidRegistration = fluidRegistrar.register(name.getPath(), fluid);

        return new FluidRegistration(fluidRegistration, Suppliers.memoize(() -> new ForgeFluidVariantHandlerDelegate(fluidTypeRegistration.get())));
    }

    @Override
    public Optional<IFluidVariantHandler> getVariantHandlerFor(final Fluid fluid) {
        return Optional.of(new ForgeFluidVariantHandlerDelegate(fluid.getFluidType()));
    }

    @Override
    public Optional<FluidInformation> get(final ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM))
                .map(fluidHandler -> fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE))
                .map(fluidStack -> new FluidInformation(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.isEmpty() ? new CompoundTag() : fluidStack.getOrCreateTag()));
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount) {
        final Optional<IFluidHandlerItem> handler = Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM));
        handler.ifPresent(h -> h.drain((int) amount, IFluidHandler.FluidAction.EXECUTE));

        return handler.map(IFluidHandlerItem::getContainer).orElse(stack);
    }

    @Override
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation) {
        final Optional<IFluidHandlerItem> handler = Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM));
        handler.ifPresent(h -> h.fill(buildFluidStack(fluidInformation), IFluidHandler.FluidAction.EXECUTE));
        return handler.map(IFluidHandlerItem::getContainer).orElse(stack);
    }

    @Override
    public Component getDisplayName(final Fluid fluid) {
        return fluid.getFluidType().getDescription();
    }

    @NotNull
    public static FluidStack buildFluidStack(final FluidInformation fluid) {
        if (fluid.data() == null)
            return new FluidStack(fluid.fluid(), (int) fluid.amount());

        return new FluidStack(fluid.fluid(), (int) fluid.amount(), fluid.data());
    }

    @NotNull
    public static FluidInformation buildFluidInformation(final FluidStack fluid) {
        if (fluid.getTag() == null)
            return new FluidInformation(fluid.getFluid(), (int) fluid.getAmount());

        return new FluidInformation(fluid.getFluid(), (int) fluid.getAmount(), fluid.getOrCreateTag());
    }

}
