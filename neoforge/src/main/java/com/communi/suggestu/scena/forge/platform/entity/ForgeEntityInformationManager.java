package com.communi.suggestu.scena.forge.platform.entity;

import com.communi.suggestu.scena.core.entity.IEntityInformationManager;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

public class ForgeEntityInformationManager implements IEntityInformationManager {
    private static final ForgeEntityInformationManager INSTANCE = new ForgeEntityInformationManager();

    private ForgeEntityInformationManager() {
    }

    public static ForgeEntityInformationManager getInstance() {
        return INSTANCE;
    }

    @Override
    public double getBlockReachDistance(final Player player) {
        final AttributeInstance reachAttribute = player.getAttribute(NeoForgeMod.BLOCK_REACH.value());
        return reachAttribute == null ? 5d : reachAttribute.getValue();
    }

    @Override
    public double getEntityReachDistance(Player player) {
        final AttributeInstance reachAttribute = player.getAttribute(NeoForgeMod.ENTITY_REACH.value());
        return reachAttribute == null ? 5d : reachAttribute.getValue();
    }
}
