package com.communi.suggestu.scena.fabric.platform.fluid;

import com.communi.suggestu.scena.core.dist.Dist;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.FluidRegistration;
import com.communi.suggestu.scena.core.fluid.FluidWithHandler;
import com.communi.suggestu.scena.core.fluid.IFluidManager;
import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class FabricFluidManager implements IFluidManager
{
    private static final FabricFluidManager INSTANCE = new FabricFluidManager();

    public static FabricFluidManager getInstance()
    {
        return INSTANCE;
    }

    private FabricFluidManager()
    {
    }

    @Override
    public FluidRegistration registerFluidAndVariant(final ResourceLocation name, final Supplier<FluidWithHandler> fluid, final Supplier<IFluidVariantHandler> variantHandler)
    {
        final IRegistrar<Fluid> fluidRegistrar = IRegistrar.create(Registry.FLUID_REGISTRY, name.getNamespace());
        final IRegistryObject<Fluid> fluidRegistration = fluidRegistrar.register(name.getPath(), fluid);

        final IFluidVariantHandler handler = variantHandler.get();
        FluidVariantAttributes.register(fluidRegistration.get(), new FabricFluidVariantAttributeHandlerDelegate(handler));

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), fluidRegistration.get());

            ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register((atlasTexture, registry) -> {
                registry.register(handler.getStillTexture(new FluidInformation(fluidRegistration.get())).orElseThrow());
                registry.register(handler.getFlowingTexture(new FluidInformation(fluidRegistration.get())).orElseThrow());
            });

            FluidVariantRendering.register(fluidRegistration.get(), new FabricFluidVariantRenderHandlerDelegate(handler));
        });

        return new FluidRegistration(fluidRegistration, () -> handler);
    }

    @Override
    public Optional<IFluidVariantHandler> getVariantHandlerFor(final Fluid fluid)
    {
        return Optional.of(new FabricFluidVariantHandlerDelegate(FluidVariantAttributes.getHandlerOrDefault(fluid)));
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public Optional<FluidInformation> get(final ItemStack stack)
    {
        try(Transaction ignored = Transaction.openOuter()) {
            final Storage<FluidVariant> target = FluidStorage.ITEM.find(stack, ContainerItemContext.withInitial(stack));
            if (target == null)
                return Optional.empty();

            final Iterable<StorageView<FluidVariant>> fluids = target;

            if (!fluids.iterator().hasNext())
                return Optional.empty();

            final StorageView<FluidVariant> view = fluids.iterator().next();

            return Optional.of(
              new FluidInformation(
                view.getResource().getFluid(),
                view.getAmount(),
                view.getResource().copyNbt()
              )
            );
        }
    }

    @Override
    public ItemStack extractFrom(final ItemStack stack, final long amount)
    {
        try(final Transaction context = Transaction.openOuter()) {
            final Optional<FluidInformation> contained =
              get(stack);

            return contained.map(fluid -> {
                final FluidVariant variant = makeVariant(fluid);
                final ContainerItemContext containerContext = ContainerItemContext.withInitial(stack);

                Objects.requireNonNull(FluidStorage.ITEM.find(stack, containerContext))
                  .extract(variant, amount, context);

                final StorageView<ItemVariant> itemVariant = containerContext.getMainSlot().iterator().next();
                return itemVariant.getResource().toStack((int) itemVariant.getAmount());
            }).orElse(ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack insertInto(final ItemStack stack, final FluidInformation fluidInformation)
    {
        try(final Transaction context = Transaction.openOuter()) {
            final Optional<FluidInformation> contained =
              get(stack);

            return contained.map(fluid -> {
                final FluidVariant variant = makeVariant(fluid);
                final ContainerItemContext containerContext = ContainerItemContext.withInitial(stack);

                Objects.requireNonNull(FluidStorage.ITEM.find(stack, containerContext))
                  .insert(variant, fluidInformation.amount(), context);

                final StorageView<ItemVariant> itemVariant = containerContext.getMainSlot().iterator().next();
                return itemVariant.getResource().toStack((int) itemVariant.getAmount());
            }).orElse(ItemStack.EMPTY);
        }
    }

    @Override
    public Component getDisplayName(final Fluid fluid)
    {
        return DistExecutor.unsafeRunForDist(
          () -> () -> FluidVariantAttributes.getName(makeVariant(new FluidInformation(fluid))),
          () -> () -> fluid.defaultFluidState().createLegacyBlock().getBlock().getName()
        );
    }

    public static FluidVariant makeVariant(final FluidInformation fluid) {
        if (!fluid.fluid().isSource(fluid.fluid().defaultFluidState()) && fluid.fluid() != Fluids.EMPTY) {
            //We have a flowing fluid.
            //Let's make a none flowing variant of it.
            return makeVariant(fluid.withSource());
        }

        if (fluid.data() == null)
            return FluidVariant.of(fluid.fluid());

        return FluidVariant.of(fluid.fluid(), fluid.data());
    }

    public static FluidInformation makeInformation(final FluidVariant fluid, final long count) {
        if (!fluid.getFluid().isSource(fluid.getFluid().defaultFluidState()) && fluid.getFluid() != Fluids.EMPTY) {
            //We have a flowing fluid.
            //Let's make a none flowing variant of it.
            if (fluid.getFluid() instanceof FlowingFluid flowingFluid) {
                return makeInformation(FluidVariant.of(flowingFluid.getSource(), fluid.copyNbt()), count);
            }
        }

        if (fluid.copyNbt() == null)
            return new FluidInformation(fluid.getFluid(), count);

        return new FluidInformation(fluid.getFluid(), count, fluid.copyNbt());
    }

    public static FluidInformation makeInformation(final FluidVariant fluid) {
        return makeInformation(fluid, 1);
    }
}
