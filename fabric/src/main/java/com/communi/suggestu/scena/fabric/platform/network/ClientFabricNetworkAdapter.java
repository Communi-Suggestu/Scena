package com.communi.suggestu.scena.fabric.platform.network;

import com.communi.suggestu.scena.core.dist.Dist;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.network.INetworkChannel;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientFabricNetworkAdapter {
    static <T extends CustomPacketPayload> void registerGlobalConfigurationHandler(CustomPacketPayload.Type<T> type, INetworkChannel.MessageExecutionHandler<T> handler) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientConfigurationNetworking.registerGlobalReceiver(type, (payload, context) -> {
                handler.execute(payload, false, null, context.client()::execute);
            });
        });
    }

    static <T extends CustomPacketPayload> void registerGlobalPlayHandler(CustomPacketPayload.Type<T> type, INetworkChannel.MessageExecutionHandler<T> handler) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
                handler.execute(payload, false, context.player(), context.client()::execute);
            });
        });
    }
}
