package com.communi.suggestu.scena.forge.platform.client.screens;

import com.communi.suggestu.scena.core.client.screens.IScreenManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeScreenManager implements IScreenManager {
    private static final ForgeScreenManager INSTANCE = new ForgeScreenManager();

    public static ForgeScreenManager getInstance() {
        return INSTANCE;
    }


    private final Collection<Consumer<IScreenManager.IMenuRegistrar>> menuRegistrars = Collections.synchronizedCollection(Lists.newArrayList());
    private final AtomicBoolean registeredMenuRegistrars = new AtomicBoolean(false);

    private ForgeScreenManager() {
    }

    @Override
    public void registerMenus(Consumer<IMenuRegistrar> registrarConsumer) {
        if (registeredMenuRegistrars.get()) {
            throw new IllegalStateException("Cannot register menu registrars after client setup");
        }

        menuRegistrars.add(registrarConsumer);
    }

    @SubscribeEvent
    public void onFMLClientSetup(FMLClientSetupEvent event) {
        registeredMenuRegistrars.set(true);

        menuRegistrars.forEach(registrar -> registrar.accept(new IMenuRegistrar() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, ScreenConstructor<M, U> constructor) {
                MenuScreens.register(type, constructor::create);
            }
        }));
    }
}
