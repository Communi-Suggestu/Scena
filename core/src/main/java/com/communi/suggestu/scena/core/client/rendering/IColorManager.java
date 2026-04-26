package com.communi.suggestu.scena.core.client.rendering;

import com.communi.suggestu.scena.core.client.IClientManager;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

/**
 * Manages access to block and item colors for rendering.
 */
public interface IColorManager
{

    static IColorManager getInstance()
    {
        return IClientManager.getInstance().getColorManager();
    }

    /**
     * Sets up block colors for the current platform.
     * A dynamic callback should be passed in, into this method, which gets invoked to set up the block
     * colors on an appropriate time.
     *
     * @param configurator The configurator for the block colors.
     */
    void setupBlockColors(
        Consumer<IBlockColorSetter> configurator
    );

    /**
     * Sets up dynamic block colors for the current platform.
     * A dynamic callback should be passed in, into this method, which gets invoked to set up the block
     * colors on an appropriate time.
     *
     * @param setter The configurator for the block colors.
     */
    void setupDynamicBlockColors(
        Consumer<IDynamicBlockColorSetter> setter
    );

    /**
     * Sets up item colors for the current platform.
     * A dynamic callback should be passed in, into this method, which gets invoked to set up the item
     * colors on an appropriate time.
     *
     * @param configurator The configurator for the item colors.
     */
    void setupItemColors(
        Consumer<IItemColorSetter> configurator
    );

    /**
     * Callback interface for the block color configuration.
     */
    @FunctionalInterface
    interface IBlockColorSetter
    {
        /**
         * Sets the color manager for the given blocks.
         *
         * @param colorManager The color manager to use for the given block.
         * @param blocks       The blocks which use the given color manager.
         */
        void register(final List<BlockTintSource> colorManager, final Block... blocks);
    }

    /**
     * Callback interface for the item color configuration.
     */
    @FunctionalInterface
    interface IItemColorSetter
    {

        /**
         * Registers the item tint source provider.
         */
        void register(final Identifier colorProviderName, MapCodec<? extends ItemTintSource> builder);
    }

    /**
     * Callback to collect dynamic tints.
     */
    @FunctionalInterface
    interface IDynamicColorProvider
    {

        /**
         * Collects the dynamic colors.
         *
         * @param state      The state
         * @param level      The level
         * @param pos        The position
         * @param tintValues The tint value results list.
         */
        void collect(BlockState state, BlockAndTintGetter level, BlockPos pos, IntList tintValues);
    }

    @FunctionalInterface
    interface IDynamicBlockColorSetter
    {
        /**
         * Sets the dynamic color manager for the given blocks.
         *
         * @param colorManager The color manager to use for the given block.
         * @param blocks       The blocks which use the given color manager.
         */
        void register(final IDynamicColorProvider colorManager, final Block... blocks);
    }
}
