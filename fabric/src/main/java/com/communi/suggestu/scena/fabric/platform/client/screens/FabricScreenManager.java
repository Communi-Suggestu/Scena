package com.communi.suggestu.scena.fabric.platform.client.screens;

import com.communi.suggestu.scena.core.client.screens.IScreenManager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;

public final class FabricScreenManager implements IScreenManager {
    private static final FabricScreenManager INSTANCE = new FabricScreenManager();

    public static FabricScreenManager getInstance() {
        return INSTANCE;
    }

    private FabricScreenManager() {
    }

    @Override
    public void registerMenus(Consumer<IMenuRegistrar> registrarConsumer) {
        registrarConsumer.accept(new IMenuRegistrar() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, ScreenConstructor<M, U> constructor) {
                MenuScreens.register(type, constructor::create);
            }
        });
    }

}
