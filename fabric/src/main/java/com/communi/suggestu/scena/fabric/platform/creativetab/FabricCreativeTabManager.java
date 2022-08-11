package com.communi.suggestu.scena.fabric.platform.creativetab;

import com.communi.suggestu.scena.core.creativetab.ICreativeTabManager;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.IntFunction;

public final class FabricCreativeTabManager implements ICreativeTabManager
{
    private static final FabricCreativeTabManager INSTANCE = new FabricCreativeTabManager();

    public static FabricCreativeTabManager getInstance()
    {
        return INSTANCE;
    }

    private FabricCreativeTabManager()
    {
    }

    @Override
    public CreativeModeTab register(final IntFunction<CreativeModeTab> builder)
    {
        ((ItemGroupExtensions)CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
        return builder.apply(CreativeModeTab.TABS.length - 1);
    }

}
