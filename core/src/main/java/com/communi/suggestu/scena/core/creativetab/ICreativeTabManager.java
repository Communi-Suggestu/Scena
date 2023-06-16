package com.communi.suggestu.scena.core.creativetab;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Manager which is used to handle the creation of new creative tabs.
 */
public interface ICreativeTabManager {

    /**
     * The current instance of on the current platform.
     *
     * @return The current manager.
     */
    static ICreativeTabManager getInstance() {
        return IScenaPlatform.getInstance().getCreativeTabManager();
    }

    /**
     * Can be invoked to modify an existing tab.
     *
     * @param key The key that identifies the tab.
     * @param adapterConsumer The adapter which can be used to modify the tab.
     */
    void modifyTab(final ResourceKey<CreativeModeTab> key, DisplayItemsAdapter adapterConsumer);

    /**
     * Defines an adapter which can be used to modify the contents of a creative tab.
     */
    @FunctionalInterface
    interface DisplayItemsAdapter {
        void accept(FeatureFlagSet enabledFlags, CreativeModeTabPopulator populator, boolean hasPermissions);
    }

    /**
     * Defines a populator which can modify the contents of a creative tab.
     */
    interface CreativeModeTabPopulator {
        /**
         * Adds the given item to the creative tab.
         *
         * @param stack       The item to add.
         * @param visibility The visibility of the item.
         */
        void prepend(ItemStack stack, CreativeModeTab.TabVisibility visibility);

        /**
         * Adds the given item to the creative tab.
         *
         * @param stack      The item to add.
         * @param visibility The visibility of the item.
         * @param after     The item to add the given item after.
         */
        void addAfter(ItemStack stack, CreativeModeTab.TabVisibility visibility, ItemStack after);

        /**
         * Adds the given item to the creative tab.
         *
         * @param stack      The item to add.
         * @param visibility The visibility of the item.
         * @param before    The item to add the given item before.
         */
        void addBefore(ItemStack stack, CreativeModeTab.TabVisibility visibility, ItemStack before);
    }
}
