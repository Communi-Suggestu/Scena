package com.communi.suggestu.scena.core.creativetab;

import com.communi.suggestu.scena.core.IScenaPlatform;
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
     * Register a new tab.
     *
     * @param configurator The tab configurator to register
     * @param name         The name of the tab.
     * @param afters       A list of tab names (String, ResourceLocations) or tabs that need to appear before this tab in the creative mode menu.
     * @param befores      A list of tab names (String, ResourceLocations) or tabs that need to appear after this tab in the creative mode menu.
     * @return The configured tab supplier, might not be immediately available.
     */
    Supplier<CreativeModeTab> register(@NotNull final Consumer<CreativeModeTab.Builder> configurator, @NotNull final ResourceLocation name, @NotNull final List<Object> afters, @NotNull final List<Object> befores);

    void modifyTab(final Supplier<CreativeModeTab> tabSupplier, DisplayItemsAdapter adapterConsumer);

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
