package com.communi.suggestu.scena.core.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ScenaContainerMenu extends AbstractContainerMenu {

    private final QuickMoveHandler quickMoveHandler = new QuickMoveHandler(this);

    protected ScenaContainerMenu(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
        registerQuickMoveRules();
    }

    public QuickMoveHandler getQuickMoveHandler() {
        return quickMoveHandler;
    }

    protected abstract void registerQuickMoveRules();

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return quickMoveHandler.quickMoveStack(pPlayer, pIndex);
    }

    public Slot getMenuSlot(int index) {
        return this.slots.get(index);
    }

    public boolean moveMenuItemStackTo(ItemStack slotStack, int startIdx, int endIdx, boolean reversed) {
        return super.moveItemStackTo(slotStack, startIdx, endIdx, reversed);
    }
}
