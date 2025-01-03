package com.communi.suggestu.scena.forge.platform.client.accessors;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * accessors used with {@link com.communi.suggestu.scena.core.dist.DistExecutor}
 */
public class ClientAccessors {

    /**
     * @return the client player
     */
    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }
}
