package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.fabric.platform.event.FabricGameEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(PlayerList.class)
public class PlayerListMixin
{

    @Unique
    private PlayerList getList()
    {
        return (PlayerList) (Object) this;
    }

    @Inject(
        method = "placeNewPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
            ordinal = 4
        )
    )
    public void onDataPackSync(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci)
    {
        FabricGameEvents.DATA_PACK_SYNC.invoker().onSync(
            getList(),
            Stream.of(player),
            null
        );
    }

    @Inject(
        method = "reloadResources",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
        )
    )
    public void onResourceSync(CallbackInfo ci)
    {
        FabricGameEvents.DATA_PACK_SYNC.invoker().onSync(
            getList(),
            getList().getPlayers().stream(),
            null
        );
    }
}
