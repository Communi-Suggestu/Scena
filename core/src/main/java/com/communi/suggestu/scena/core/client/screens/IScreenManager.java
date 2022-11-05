package com.communi.suggestu.scena.core.client.screens;

import com.communi.suggestu.scena.core.client.IClientManager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Central manager for dealing with screens and menus.
 */
public interface IScreenManager {

    static IScreenManager getInstance() {
        return IClientManager.getInstance().getScreenManager();
    }

    /**
     * Registers a new screen for the given menu type.
     *
     * @param type The menu type.
     * @param constructor The screen constructor.
     * @param <M> The type of the menu.
     * @param <U> The type of the screen.
     */
    <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor);
}
