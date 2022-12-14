package com.communi.suggestu.scena.forge.platform.creativetab;

import com.communi.suggestu.scena.core.creativetab.ICreativeTabManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID)
public final class ForgeCreativeTabManager implements ICreativeTabManager {
    private static final ForgeCreativeTabManager INSTANCE = new ForgeCreativeTabManager();
    private final Map<ResourceLocation, CreativeTabConstructionRegistration> creativeTabs = Maps.newConcurrentMap();
    private final Map<ResourceLocation, CreativeModeTab> registeredTabs = Maps.newConcurrentMap();
    private final AtomicBoolean creativeTabsRegistered = new AtomicBoolean(false);
    private final Collection<CreativeTabModificationRegistration> modificationRegistrations = Collections.synchronizedCollection(Lists.newArrayList());
    private final AtomicBoolean creativeTabsModified = new AtomicBoolean(false);


    private ForgeCreativeTabManager() {
    }

    public static ForgeCreativeTabManager getInstance() {
        return INSTANCE;
    }

    @Override
    public Supplier<CreativeModeTab> register(@NotNull Consumer<CreativeModeTab.Builder> configurator, @NotNull ResourceLocation name, @NotNull List<Object> afters, @NotNull List<Object> befores) {
        if (creativeTabsRegistered.get()) {
            throw new IllegalStateException("Cannot register creative tab after creative tabs have been registered");
        }

        creativeTabs.put(name, new CreativeTabConstructionRegistration(configurator, afters, befores));

        return () -> {
            if (!registeredTabs.containsKey(name)) {
                throw new IllegalStateException("Tried to access creative tab before it was registered!");
            }

            return registeredTabs.get(name);
        };
    }

    @Override
    public void modifyTab(final Supplier<CreativeModeTab> tabSupplier, DisplayItemsAdapter displayItemsAdapter) {
        if (creativeTabsModified.get()) {
            throw new IllegalStateException("Cannot modify creative tab after creative tabs have been modified");
        }

        modificationRegistrations.add(new CreativeTabModificationRegistration(tabSupplier, displayItemsAdapter));
    }

    @SubscribeEvent
    public static void onCreativeModeTab(CreativeModeTabEvent.Register event) {
        getInstance().creativeTabsRegistered.set(true);

        final List<ResourceLocation> unregistered = Lists.newArrayList(getInstance().creativeTabs.keySet());

        while (!unregistered.isEmpty()) {
            final ResourceLocation name = unregistered.remove(0);
            final CreativeTabConstructionRegistration registration = getInstance().creativeTabs.get(name);

            if (registration == null) {
                throw new IllegalStateException("Creative tab " + name + " was not registered!");
            }

            final CreativeModeTab tab = event.registerCreativeModeTab(
                    name,
                    registration.befores(),
                    registration.afters(),
                    registration.configurator()
            );

            getInstance().registeredTabs.put(name, tab);
        }
    }

    @SubscribeEvent
    public static void onCreativeModeTab(CreativeModeTabEvent.BuildContents event) {
        getInstance().creativeTabsModified.set(true);

        for (final CreativeTabModificationRegistration registration : getInstance().modificationRegistrations) {
            final CreativeModeTab tab = registration.tabSupplier().get();

            if (event.getTab() == tab) {
                final var adapter = registration.adapterConsumer();
                adapter.accept(event.getFlags(), new Adapter(event.getEntries()), event.hasPermissions());
            }
        }
    }

    record CreativeTabConstructionRegistration(@NotNull Consumer<CreativeModeTab.Builder> configurator,
                                               @NotNull List<Object> afters, @NotNull List<Object> befores) {
    }

    record CreativeTabModificationRegistration(@NotNull Supplier<CreativeModeTab> tabSupplier,
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
