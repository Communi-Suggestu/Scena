package com.communi.suggestu.scena.core.entity;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Manages the interactions with a players inventory.
 */
public interface IPlayerInventoryManager
{

    /**
     * Gives access to the player inventory manager.
     *
     * @return The player inventory manager.
     */
    static IPlayerInventoryManager getInstance() {
        return IScenaPlatform.getInstance().getPlayerInventoryManager();
    }

    /**
     * Gives the item stack to the player.
     *
     * @param player The player in question.
     * @param stack The stack in question.
     */
    void giveToPlayer(final Player player, final ItemStack stack);
}
