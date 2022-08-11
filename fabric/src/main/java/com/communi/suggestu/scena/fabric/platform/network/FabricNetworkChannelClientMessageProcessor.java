package com.communi.suggestu.scena.fabric.platform.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.IntFunction;

public class FabricNetworkChannelClientMessageProcessor
{

    private FabricNetworkChannelClientMessageProcessor()
    {
        throw new IllegalStateException("Can not instantiate an instance of: FabricNetworkChannelMessageProcessor. This is a utility class");
    }

    public static void registerClientChannel(final ResourceLocation name, final IntFunction<FabricNetworkChannel.NetworkMessageSpecification<?>> specificationCreator) {
        ClientPlayNetworking.registerGlobalReceiver(name,
          (minecraft, clientPacketListener, friendlyByteBuf, packetSender) -> FabricNetworkChannelClientMessageProcessor.handleReceivedMessageOnClientSide(friendlyByteBuf, specificationCreator));
    }

    public static void handleReceivedMessageOnClientSide(final FriendlyByteBuf friendlyByteBuf, final IntFunction<FabricNetworkChannel.NetworkMessageSpecification<?>> specificationCreator)
    {
        final int messageId = friendlyByteBuf.readVarInt();
        final FabricNetworkChannel.NetworkMessageSpecification<?> spec = specificationCreator.apply(messageId);
        handleMessageReceive(
          spec,
          friendlyByteBuf,
          Minecraft.getInstance().player,
          Minecraft.getInstance()::execute
        );
    }

    private static <T> void handleMessageReceive(
      final FabricNetworkChannel.NetworkMessageSpecification<T> specification,
      final FriendlyByteBuf friendlyByteBuf,
      final Player player,
      final Consumer<Runnable> executor
    )
    {
        final T message = specification.getCreator().apply(friendlyByteBuf);
        specification.getExecutionHandler().execute(message, false, player, executor);
    }
}
