package com.communi.suggestu.scena.forge.platform.client.screens;

import com.communi.suggestu.scena.core.client.screens.IScreenManager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ForgeScreenManager implements IScreenManager {
    private static final ForgeScreenManager INSTANCE = new ForgeScreenManager();

    public static ForgeScreenManager getInstance() {
        return INSTANCE;
    }

    private ForgeScreenManager() {
    }


    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor) {
        Mod.EventBusSubscriber.Bus.MOD.bus().get().addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, clientSetupEvent -> clientSetupEvent.enqueueWork(() -> {
            MenuScreens.register(type, constructor);
        }));
    }
}
