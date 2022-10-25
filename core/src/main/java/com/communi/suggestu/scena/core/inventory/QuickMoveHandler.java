package com.communi.suggestu.scena.core.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * The QuickMoveHandler is a registration helper for setting up {@link AbstractContainerMenu#quickMoveStack(Player, int)}
 * Adapted from: <a href="https://github.com/Shadows-of-Fire/Placebo/blob/1.19/src/main/java/shadows/placebo/container/QuickMoveHandler.java">...</a>
 * Idea and rule implementation is the same, but adapted to use a MenuScreen reference directly.
 */
public final class QuickMoveHandler {

    private final ScenaContainerMenu scenaContainerMenu;
    private final List<QuickMoveRule> rules = new ArrayList<>();

    public QuickMoveHandler(ScenaContainerMenu scenaContainerMenu) {
        this.scenaContainerMenu = scenaContainerMenu;
    }


    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack slotStackCopy = ItemStack.EMPTY;
        Slot slot = scenaContainerMenu.getMenuSlot(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            slotStackCopy = slotStack.copy();
            for (QuickMoveRule rule : this.rules) {
                if (rule.req.test(slotStack, index)) {
                    if (!scenaContainerMenu.moveMenuItemStackTo(slotStack, rule.startIdx, rule.endIdx, rule.reversed)) {
                        slot.setChanged();
                        return ItemStack.EMPTY;
                    }
                }
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return slotStackCopy;
    }

    public QuickMoveHandler registerRule(BiPredicate<ItemStack, Integer> req, int startIdx, int endIdx, boolean reversed) {
        this.rules.add(new QuickMoveRule(req, startIdx, endIdx, reversed));
        return this;
    }

    public QuickMoveHandler registerRule(BiPredicate<ItemStack, Integer> req, int startIdx, int endIdx) {
        return this.registerRule(req, startIdx, endIdx, false);
    }

    private record QuickMoveRule(BiPredicate<ItemStack, Integer> req, int startIdx, int endIdx, boolean reversed) {}
}