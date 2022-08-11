package com.communi.suggestu.scena.core.client.rendering;

import com.communi.suggestu.scena.core.client.IClientManager;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * Manages access to block and item colors for rendering.
 */
public interface IColorManager
{

    static IColorManager getInstance() {
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
    interface IBlockColorSetter {

        /**
         * Sets the color manager for the given blocks.
         * @param colorManager The color manager to use for the given block.
         * @param blocks The blocks which use the given color manager.
         */
        void register(final BlockColor colorManager, final Block... blocks);
    }

    /**
     * Callback interface for the item color configuration.
     */
    @FunctionalInterface
    interface IItemColorSetter {

        /**
         * Sets the color manager for the given items.
         * @param colorManager The color manager to use for the given item.
         * @param items The items which use the given color manager.
         */
        void register(final ItemColor colorManager, final Item... items);
    }
}
