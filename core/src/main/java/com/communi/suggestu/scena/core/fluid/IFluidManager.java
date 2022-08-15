package com.communi.suggestu.scena.core.fluid;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The fluid manager of the platform.
 * Different platforms implement fluid management systems.
 */
public interface IFluidManager
{
    /**
     * Gives access to the fluid manager of the current platform.
     *
     * @return The fluid manager of the current platform.
     */
    static IFluidManager getInstance() {
        return IScenaPlatform.getInstance().getFluidManager();
    }

    /**
     * Registers a new variant handler and the passed in fluid.
     *
     * @param name The name of the fluid.
     * @param fluid The fluid to register the handler for.
     * @param variantHandler The builder that will be used to create the handler.
     * @return A supplier which will return the registered variant handler.
     */
    FluidRegistration registerFluidAndVariant(final ResourceLocation name, final Supplier<FluidWithHandler> fluid, final Supplier<IFluidVariantHandler> variantHandler);

    /**
     * Returns the fluid variant handler for the given fluid.
     * This might be empty or filled with a default handler depending on the platform.
     *
     * @param fluid The fluid to get the handler for.
     * @return The fluid variant handler for the given fluid.
     */
    Optional<IFluidVariantHandler> getVariantHandlerFor(final Fluid fluid);

    /**
     * Returns the fluid variant handler for the given fluid information.
     * This might be empty or filled with a default handler depending on the platform.
     *
     * @param fluid The fluid information to get the handler for.
     * @return The fluid variant handler for the given fluid information.
     */
    default Optional<IFluidVariantHandler> getVariantHandlerFor(FluidInformation fluid) {
        return getVariantHandlerFor(fluid.fluid());
    }

    /**
     * Returns the fluid, and the amount of fluid stored in the given itemstack.
     *
     * @param stack The item stack to get the fluid from.
     * @return An optional possibly containing the fluid and the amount of fluid in the stack.
     */
    Optional<FluidInformation> get(final ItemStack stack);

    /**
     * Extracts all the fluid from the itemstack.
     *
     * @param stack The itemstack to extract all fluids from.
     */
    default ItemStack extractFrom(final ItemStack stack) {
        return extractFrom(stack, Long.MAX_VALUE);
    }

    /**
     * Extracts the given amount of fluids from the given stack.
     *
     * @param stack The stack to extract from.
     * @param amount The amount to extract.
     * @return The resulting itemstack from the extraction.
     */
    ItemStack extractFrom(final ItemStack stack, final long amount);

    /**
     * Invoked to insert a given amount of fluid into the given stack.
     *
     * @param stack The stack to insert into.
     * @param fluidInformation The fluid to insert.
     * @return The resulting stack of the insertion.
     */
    ItemStack insertInto(ItemStack stack, FluidInformation fluidInformation);

    /**
     * The amount of a fluid in a single bucket on a given platform.
     * In general this is 1000 mB, which is equal to one block.
     *
     * @return The amount of fluid in one bucket.
     */
    default long getBucketAmount() {
        return 1000;
    }

    /**
     * Gets the display name of the fluid.
     *
     * @param fluid The fluid to get the display name from.
     * @return The display name of the fluid.
     */
    Component getDisplayName(Fluid fluid);

}
