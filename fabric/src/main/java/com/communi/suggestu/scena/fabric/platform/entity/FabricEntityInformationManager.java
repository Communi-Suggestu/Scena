package com.communi.suggestu.scena.fabric.platform.entity;

import com.communi.suggestu.scena.core.entity.IEntityInformationManager;
import net.minecraft.world.entity.player.Player;

public class FabricEntityInformationManager implements IEntityInformationManager
{
    private static final FabricEntityInformationManager INSTANCE = new FabricEntityInformationManager();

    public static FabricEntityInformationManager getInstance()
    {
        return INSTANCE;
    }

    @Override
    public double getReachDistance(final Player player)
    {
        return 5d;
    }

    private FabricEntityInformationManager()
    {
    }
}
