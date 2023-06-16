package com.communi.suggestu.scena.fabric.platform.item;

import com.communi.suggestu.scena.core.item.IItemComparisonHelper;
import net.minecraft.world.item.ItemStack;

public final class FabricItemComparisonHelper implements IItemComparisonHelper
{
    private static final FabricItemComparisonHelper INSTANCE = new FabricItemComparisonHelper();

    public static FabricItemComparisonHelper getInstance()
    {
        return INSTANCE;
    }

    private FabricItemComparisonHelper()
    {
    }

    @Override
    public boolean canItemStacksStack(final ItemStack left, final ItemStack right)
    {
        return ItemStack.matches(left, right);
    }
}
