package com.communi.suggestu.scena.fabric.platform.item;

import com.communi.suggestu.scena.core.item.IDyeItemHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class FabricDyeItemHelper implements IDyeItemHelper
{
    private static final FabricDyeItemHelper INSTANCE = new FabricDyeItemHelper();

    public static FabricDyeItemHelper getInstance()
    {
        return INSTANCE;
    }

    private FabricDyeItemHelper()
    {
    }

    @Override
    public Optional<DyeColor> getColorFromItem(final ItemStack stack)
    {
        return Optional.empty(); //Custom fabric dye colors is not supported yet, C&B will take care of vanilla.
    }
}
