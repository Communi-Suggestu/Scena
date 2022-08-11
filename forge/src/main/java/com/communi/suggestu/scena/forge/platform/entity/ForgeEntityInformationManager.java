package com.communi.suggestu.scena.forge.platform.entity;

import com.communi.suggestu.scena.core.entity.IEntityInformationManager;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

public class ForgeEntityInformationManager implements IEntityInformationManager
{
    private static final ForgeEntityInformationManager INSTANCE = new ForgeEntityInformationManager();

    public static ForgeEntityInformationManager getInstance()
    {
        return INSTANCE;
    }


    private ForgeEntityInformationManager()
    {
    }


    @Override
    public double getReachDistance(final Player player)
    {
        final AttributeInstance reachAttribute = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
        return reachAttribute == null ? 5d : reachAttribute.getValue();
    }
}
