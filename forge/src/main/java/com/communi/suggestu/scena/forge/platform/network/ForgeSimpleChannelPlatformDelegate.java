package com.communi.suggestu.scena.forge.platform.network;

import com.communi.suggestu.scena.core.network.INetworkChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ForgeSimpleChannelPlatformDelegate implements INetworkChannel
{

    private final SimpleChannel simpleChannel;

    public ForgeSimpleChannelPlatformDelegate(final SimpleChannel simpleChannel) {this.simpleChannel = simpleChannel;}

    @Override
    public <T> void register(
      final int id,
      final Class<T> msgClass,
      final BiConsumer<T, FriendlyByteBuf> serializer,
      final Function<FriendlyByteBuf, T> creator,
      final MessageExecutionHandler<T> executionHandler)
    {
        simpleChannel.registerMessage(
          id,
          msgClass,
          serializer,
          creator,
          (message, contextSupplier) -> {
              final NetworkEvent.Context ctx = contextSupplier.get();
              final LogicalSide packetOrigin = ctx.getDirection().getOriginationSide();
              ctx.setPacketHandled(true);
              executionHandler.execute(message, packetOrigin.isClient(), packetOrigin.isClient() ? ctx.getSender() : null, ctx::enqueueWork);
          }
        );
    }

    @Override
    public void sendToServer(final Object msg)
    {
        simpleChannel.sendToServer(msg);
    }

    @Override
    public void sendToPlayer(final Object msg, final ServerPlayer player)
    {
        simpleChannel.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
