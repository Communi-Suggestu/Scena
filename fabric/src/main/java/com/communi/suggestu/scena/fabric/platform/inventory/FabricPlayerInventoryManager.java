package com.communi.suggestu.scena.fabric.platform.inventory;

import com.communi.suggestu.scena.core.entity.IPlayerInventoryManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class FabricPlayerInventoryManager implements IPlayerInventoryManager
{
    private static final FabricPlayerInventoryManager INSTANCE = new FabricPlayerInventoryManager();

    public static FabricPlayerInventoryManager getInstance()
    {
        return INSTANCE;
    }

    private FabricPlayerInventoryManager()
    {
    }

    @Override
    public void giveToPlayer(final Player player, final ItemStack stack)
    {
        player.getInventory().placeItemBackInInventory(stack);
    }
}
