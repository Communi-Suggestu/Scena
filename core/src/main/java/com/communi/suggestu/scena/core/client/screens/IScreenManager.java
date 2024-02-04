package com.communi.suggestu.scena.core.client.screens;

import com.communi.suggestu.scena.core.client.IClientManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;

/**
 * Central manager for dealing with screens and menus.
 */
public interface IScreenManager {

    static IScreenManager getInstance() {
        return IClientManager.getInstance().getScreenManager();
    }

    void registerMenus(final Consumer<IMenuRegistrar> registrarConsumer);

    interface IMenuRegistrar {

        /**
         * Registers a new screen for the given menu type.
         *
         * @param type The menu type.
         * @param constructor The screen constructor.
         * @param <M> The type of the menu.
         * @param <U> The type of the screen.
         */
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, ScreenConstructor<M, U> constructor);
    }

    interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T pMenu, Inventory pInventory, Component pTitle);
    }
}
