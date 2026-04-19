package com.communi.suggestu.scena.fabric.platform.creativetab;

import com.communi.suggestu.scena.core.creativetab.ICreativeTabManager;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class FabricCreativeTabManager implements ICreativeTabManager {
    private static final FabricCreativeTabManager INSTANCE = new FabricCreativeTabManager();

    private FabricCreativeTabManager() {
    }

    public static FabricCreativeTabManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void modifyTab(final ResourceKey<CreativeModeTab> key, DisplayItemsAdapter adapterConsumer) {

        CreativeModeTabEvents.modifyOutputEvent(key).register(entries -> adapterConsumer.accept(entries.getEnabledFeatures(), new Adapter(entries), entries.shouldShowOpRestrictedItems()));
    }

    public static final class Adapter implements ICreativeTabManager.CreativeModeTabPopulator {
        private final FabricCreativeModeTabOutput delegate;

        public Adapter(FabricCreativeModeTabOutput delegate) {
            this.delegate = delegate;
        }

        @Override
        public void prepend(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            delegate.prepend(stack, visibility);
        }

        @Override
        public void addAfter(ItemStack stack, CreativeModeTab.TabVisibility visibility, ItemStack after) {
            delegate.insertAfter(after, List.of(stack), visibility);
        }

        @Override
        public void addBefore(ItemStack stack, CreativeModeTab.TabVisibility visibility, ItemStack before) {
            delegate.insertBefore(before, List.of(stack), visibility);
        }
    }
}
