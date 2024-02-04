package com.communi.suggestu.scena.forge.platform.creativetab;

import com.communi.suggestu.scena.core.creativetab.ICreativeTabManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.MutableHashedLinkedMap;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID)
public final class ForgeCreativeTabManager implements ICreativeTabManager {
    private static final ForgeCreativeTabManager INSTANCE = new ForgeCreativeTabManager();
    private final Collection<CreativeTabModificationRegistration> modificationRegistrations = Collections.synchronizedCollection(Lists.newArrayList());
    private final AtomicBoolean creativeTabsModified = new AtomicBoolean(false);


    private ForgeCreativeTabManager() {
    }

    public static ForgeCreativeTabManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void modifyTab(final ResourceKey<CreativeModeTab> key, DisplayItemsAdapter displayItemsAdapter) {
        if (creativeTabsModified.get()) {
            throw new IllegalStateException("Cannot modify creative tab after creative tabs have been modified");
        }

        modificationRegistrations.add(new CreativeTabModificationRegistration(key, displayItemsAdapter));
    }

    @SubscribeEvent
    public static void onCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        getInstance().creativeTabsModified.set(true);

        for (final CreativeTabModificationRegistration registration : getInstance().modificationRegistrations) {
            if (event.getTabKey() == registration.key()) {
                final var adapter = registration.adapterConsumer();
                adapter.accept(event.getFlags(), new Adapter(event.getEntries()), event.hasPermissions());
            }
        }
    }

    record CreativeTabConstructionRegistration(@NotNull Consumer<CreativeModeTab.Builder> configurator,
                                               @NotNull List<Object> afters, @NotNull List<Object> befores) {
    }

    record CreativeTabModificationRegistration(@NotNull ResourceKey<CreativeModeTab> key,
                                               @NotNull DisplayItemsAdapter adapterConsumer) {
    }

    public static final class Adapter implements ICreativeTabManager.CreativeModeTabPopulator {
        private final MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> delegate;

        public Adapter(MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void prepend(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
            delegate.put(stack, visibility);
        }

        @Override
        public void addAfter(ItemStack stack, CreativeModeTab.TabVisibility visibility, ItemStack after) {
            delegate.putAfter(after, stack, visibility);
        }

        @Override
        public void addBefore(ItemStack stack, CreativeModeTab.TabVisibility visibility, ItemStack before) {
            delegate.putBefore(before, stack, visibility);
        }
    }
}
