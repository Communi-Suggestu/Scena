package com.communi.suggestu.scena.core.client.rendering.type;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Manager for handling the different render types which are available to
 * fluids, items and blocks.
 */
public interface IRenderTypeManager
{

    /**
     * The current render type manager.
     * Deals with the render types which are available on different platforms.
     *
     * @return The render type manager.
     */
    static IRenderTypeManager getInstance() {
        return IRenderingManager.getInstance().getRenderTypeManager();
    }

    /**
     * Indicates if the blockstate needs to be rendered in the render type.
     *
     * @param blockState The block state in question.
     * @param renderType The render type.
     * @return True when rendering in the given render type is required, false when not.
     */
    boolean canRenderInType(final BlockState blockState, final RenderType renderType);

    /**
     * Indicates if the fluidState needs to be rendered in the render type.
     *
     * @param fluidState The fluid state in question.
     * @param renderType The render type.
     * @return True when rendering in the given render type is required, false when not.
     */
    boolean canRenderInType(final FluidState fluidState, final RenderType renderType);

    /**
     * Registers a callback which can be used to register render types for blocks.
     * @param consumer The configurator.
     */
    void registerBlockFallbackRenderTypes(final Consumer<IFallbackBlockRenderTypeRegistrar> consumer);

    /**
     * Retrieves the {@linkplain RenderType render types} for the given block state, data and model.
     *
     * @param model The model to get the types for.
     * @param state The block state to get the types for.
     * @param rand The random source to use.
     * @param data The data to use.
     * @return The render types for the given block state, data and model.
     */
    @NotNull
    Collection<RenderType> getRenderTypesFor(BakedModel model, BlockState state, RandomSource rand, IBlockModelData data);

    /**
     * Retrieves the {@linkplain RenderType render types} for the given model, itemstack and if we are running in fabulous or not.
     *
     * @param model The model to get the types for.
     * @param stack The stack to get the types for.
     * @param isFabulous True when fabulous is enabled, false when not.
     * @return The render types for the given block state, data and model.
     */
    @NotNull
    Collection<RenderType> getRenderTypesFor(BakedModel model, ItemStack stack, boolean isFabulous);


    /**
     * A registrar for fallback render types for blocks, in case the platform does not support model based render types.
     */
    interface IFallbackBlockRenderTypeRegistrar
    {
        /**
         * Registers a fallback render type for the given block.
         *
         * @param block The block to register the fallback render type for.
         * @param renderType The render type to use as a fallback.
         */
        void register(final Block block, final RenderType renderType);
    }
}
