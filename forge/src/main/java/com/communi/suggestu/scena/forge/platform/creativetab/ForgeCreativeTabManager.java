package com.communi.suggestu.scena.forge.platform.creativetab;

import com.communi.suggestu.scena.core.creativetab.ICreativeTabManager;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.IntFunction;

public final class ForgeCreativeTabManager implements ICreativeTabManager
{
    private static final ForgeCreativeTabManager INSTANCE = new ForgeCreativeTabManager();

    public static ForgeCreativeTabManager getInstance()
    {
        return INSTANCE;
    }

    private ForgeCreativeTabManager()
    {
    }

    @Override
    public CreativeModeTab register(final IntFunction<CreativeModeTab> builder)
    {
        return builder.apply(CreativeModeTab.TABS.length);
    }
}
