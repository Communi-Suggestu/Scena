package com.communi.suggestu.scena.fabric.platform.network;

import com.communi.suggestu.scena.core.dist.Dist;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.network.INetworkChannel;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class FabricNetworkChannel implements INetworkChannel
{

    private final ResourceLocation                             name;
    private final Map<Integer, NetworkMessageSpecification<?>> messageSpecifications = Maps.newHashMap();

    public FabricNetworkChannel(final ResourceLocation name)
    {
        this.name = name;

        FabricNetworkChannelServerMessageProcessor.registerServerChannel(name, messageSpecifications::get);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FabricNetworkChannelClientMessageProcessor.registerClientChannel(name, messageSpecifications::get));
    }

    @SuppressWarnings("unchecked")
    public <T> void handleMessageSerialization(
      final T message,
      final FriendlyByteBuf friendlyByteBuf
    )
    {
        final NetworkMessageSpecification<T> spec = (NetworkMessageSpecification<T>) getSpec(message.getClass());
        friendlyByteBuf.writeVarInt(spec.id);
        spec.serializer.accept(message, friendlyByteBuf);
    }

    @Override
    public <T> void register(
      final int id,
      final Class<T> msgClass,
      final BiConsumer<T, FriendlyByteBuf> serializer,
      final Function<FriendlyByteBuf, T> creator,
      final MessageExecutionHandler<T> executionHandler)
    {
        final NetworkMessageSpecification<T> spec = new NetworkMessageSpecification<>(id, msgClass, serializer, creator, executionHandler);
        this.messageSpecifications.put(
          spec.id,
          spec
        );
    }

    @Override
    public void sendToServer(final Object msg)
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            if (Minecraft.getInstance().getConnection() == null)
                return;

            final FriendlyByteBuf buf = PacketByteBufs.create();

            handleMessageSerialization(
              msg,
              buf
            );

            ClientPlayNetworking.send(name, buf);
        });
    }

    @Override
    public void sendToPlayer(final Object msg, final ServerPlayer player)
    {
        final FriendlyByteBuf buf = PacketByteBufs.create();

        handleMessageSerialization(
          msg,
          buf
        );

        ServerPlayNetworking.send(player, name, buf);
    }

    @SuppressWarnings("unchecked")
    private <T> NetworkMessageSpecification<T> getSpec(final Class<T> networkMessageClass)
    {
        return (NetworkMessageSpecification<T>) messageSpecifications.values().stream().filter(
            spec -> spec.msgClass.equals(networkMessageClass)
          )
          .findFirst()
          .orElseThrow();
    }

    static final class NetworkMessageSpecification<T>
    {
        private final int                            id;
        private final Class<T>                       msgClass;
        private final BiConsumer<T, FriendlyByteBuf> serializer;
        private final Function<FriendlyByteBuf, T>   creator;
        private final MessageExecutionHandler<T>     executionHandler;

        private NetworkMessageSpecification(
          final int id,
          final Class<T> msgClass,
          final BiConsumer<T, FriendlyByteBuf> serializer,
          final Function<FriendlyByteBuf, T> creator, final MessageExecutionHandler<T> executionHandler)
        {
            this.id = id;
            this.msgClass = msgClass;
            this.serializer = serializer;
            this.creator = creator;
            this.executionHandler = executionHandler;
        }

        public int getId()
        {
            return id;
        }

        public Class<T> getMsgClass()
        {
            return msgClass;
        }

        public BiConsumer<T, FriendlyByteBuf> getSerializer()
        {
            return serializer;
        }

        public Function<FriendlyByteBuf, T> getCreator()
        {
            return creator;
        }

        public MessageExecutionHandler<T> getExecutionHandler()
        {
            return executionHandler;
        }
    }
}
