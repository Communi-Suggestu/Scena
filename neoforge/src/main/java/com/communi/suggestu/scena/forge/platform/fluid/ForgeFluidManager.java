package com.communi.suggestu.scena.forge.platform.fluid;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.communi.suggestu.scena.core.dist.Dist;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.fluid.*;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import com.communi.suggestu.scena.forge.platform.ForgeScenaPlatform;
import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeFluidManager implements IFluidManager {

    private static final ForgeFluidManager INSTANCE = new ForgeFluidManager();

    public static ForgeFluidManager getInstance() {
        return INSTANCE;
    }

    private ForgeFluidManager() {
    }

    @Override
    public FluidRegistration registerFluidAndVariant(final Identifier name, final Supplier<FluidWithHandler> fluid, final Supplier<IFluidVariantHandler> variantHandler) {
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
        final var fluidStack = FluidUtil.getFirstStackContained(stack);
        if (fluidStack.isEmpty())
            return Optional.empty();

        return Optional.of(
            new FluidInformation(
                fluidStack.getFluid(),
                fluidStack.getAmount(),
                fluidStack.getComponentsPatch()
            )
        );
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount) {
        final ResourceHandler<FluidResource> access = ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (access == null)
            return stack;

        try(Transaction tx = Transaction.open(null)) {
            final var fluidStack = FluidUtil.getFirstStackContained(stack);
            final var resource = FluidResource.of(fluidStack);
            if (resource.isEmpty())
                return stack;

            access.extract(resource, (int) Math.min(fluidStack.getAmount(), amount), tx);
            tx.commit();
        }

        return stack;
    }

    @Override
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation) {
        final ResourceHandler<FluidResource> access = ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (access == null)
            return stack;

        try(Transaction tx = Transaction.open(null)) {
            final var resource = FluidResource.of(fluidInformation.fluid(), fluidInformation.data());
            if (resource.isEmpty())
                return stack;

            access.insert(resource, (int) fluidInformation.amount(), tx);
            tx.commit();
        }

        return stack;
    }

    @Override
    public Component getDisplayName(final Fluid fluid) {
        return fluid.getFluidType().getDescription();
    }

    @NotNull
    public static FluidStack buildFluidStack(final FluidInformation fluid) {
        if (fluid.data() == null)
            return new FluidStack(fluid.fluid(), (int) fluid.amount());

        return new FluidStack(
                fluid.fluid(),
                (int) fluid.amount(),
                fluid.data());
    }

    @NotNull
    public static FluidInformation buildFluidInformation(final FluidStack fluid) {
        if (fluid.getComponentsPatch().isEmpty())
            return new FluidInformation(fluid.getFluid(), fluid.getAmount());

        return new FluidInformation(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch());
    }

}
