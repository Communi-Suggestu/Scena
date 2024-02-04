package com.communi.suggestu.scena.forge.platform.item;

import com.communi.suggestu.scena.core.item.IDyeItemHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;

import java.util.Optional;

public class DyeItemHelper implements IDyeItemHelper
{
    private static final DyeItemHelper INSTANCE = new DyeItemHelper();

    public static DyeItemHelper getInstance()
    {
        return INSTANCE;
    }

    private DyeItemHelper()
    {
    }


    @Override
    public Optional<DyeColor> getColorFromItem(final ItemStack stack)
    {
        if (stack.is(Tags.Items.DYES_WHITE))
            return Optional.of(DyeColor.WHITE);
        if (stack.is(Tags.Items.DYES_ORANGE))
            return Optional.of(DyeColor.ORANGE);
        if (stack.is(Tags.Items.DYES_MAGENTA))
            return Optional.of(DyeColor.MAGENTA);
        if (stack.is(Tags.Items.DYES_LIGHT_BLUE))
            return Optional.of(DyeColor.LIGHT_BLUE);
        if (stack.is(Tags.Items.DYES_YELLOW))
            return Optional.of(DyeColor.YELLOW);
        if (stack.is(Tags.Items.DYES_LIME))
            return Optional.of(DyeColor.LIME);
        if (stack.is(Tags.Items.DYES_PINK))
            return Optional.of(DyeColor.PINK);
        if (stack.is(Tags.Items.DYES_GRAY))
            return Optional.of(DyeColor.GRAY);
        if (stack.is(Tags.Items.DYES_LIGHT_GRAY))
            return Optional.of(DyeColor.LIGHT_GRAY);
        if (stack.is(Tags.Items.DYES_CYAN))
            return Optional.of(DyeColor.CYAN);
        if (stack.is(Tags.Items.DYES_PURPLE))
            return Optional.of(DyeColor.PURPLE);
        if (stack.is(Tags.Items.DYES_BLUE))
            return Optional.of(DyeColor.BLUE);
        if (stack.is(Tags.Items.DYES_BROWN))
            return Optional.of(DyeColor.BROWN);
        if (stack.is(Tags.Items.DYES_GREEN))
            return Optional.of(DyeColor.GREEN);
        if (stack.is(Tags.Items.DYES_RED))
            return Optional.of(DyeColor.RED);
        if (stack.is(Tags.Items.DYES_BLACK))
            return Optional.of(DyeColor.BLACK);
        
        return Optional.empty();
    }
}
