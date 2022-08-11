package com.communi.suggestu.scena.fabric.platform.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.IntFunction;

public class FabricNetworkChannelServerMessageProcessor
{

    private FabricNetworkChannelServerMessageProcessor()
    {
        throw new IllegalStateException("Can not instantiate an instance of: FabricNetworkChannelMessageProcessor. This is a utility class");
    }

    public static void registerServerChannel(final ResourceLocation name, final IntFunction<FabricNetworkChannel.NetworkMessageSpecification<?>> specificationCreator) {
        ServerPlayNetworking.registerGlobalReceiver(name, (minecraftServer, serverPlayer, serverGamePacketListener, friendlyByteBuf, packetSender) -> FabricNetworkChannelServerMessageProcessor.handleReceivedMessageOnServerSide(minecraftServer, serverPlayer, friendlyByteBuf, specificationCreator));
    }

    public static void handleReceivedMessageOnServerSide(
      final MinecraftServer minecraftServer,
      final ServerPlayer serverPlayer,
      final FriendlyByteBuf friendlyByteBuf,
      final IntFunction<FabricNetworkChannel.NetworkMessageSpecification<?>> specificationCreator)
    {
        final int messageId = friendlyByteBuf.readVarInt();
        final FabricNetworkChannel.NetworkMessageSpecification<?> spec = specificationCreator.apply(messageId);
        handleMessageReceive(
          spec,
          friendlyByteBuf,
          serverPlayer,
          minecraftServer::execute
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
        specification.getExecutionHandler().execute(message, true, player, executor);
    }
}
