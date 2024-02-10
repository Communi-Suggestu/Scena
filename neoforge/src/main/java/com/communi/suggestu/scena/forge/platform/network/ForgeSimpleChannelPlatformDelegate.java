package com.communi.suggestu.scena.forge.platform.network;

import com.communi.suggestu.scena.core.network.INetworkChannel;
import com.google.common.collect.Maps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ForgeSimpleChannelPlatformDelegate implements INetworkChannel, IPayloadHandler<ForgeSimpleChannelPlatformDelegate.Payload> {
    private final ResourceLocation name;
    private final String version;
    private final Map<Integer, NetworkMessageSpecification<?>> messageSpecifications = Maps.newHashMap();

    public ForgeSimpleChannelPlatformDelegate(ResourceLocation name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public <T> void handleMessageSerialization(
            final T message,
            final FriendlyByteBuf friendlyByteBuf
    ) {
        final NetworkMessageSpecification<T> spec = (NetworkMessageSpecification<T>) getSpec(message.getClass());
        friendlyByteBuf.writeVarInt(spec.id);
        spec.serializer.accept(message, friendlyByteBuf);
    }

    public <T> T handleMessageDeserialization(
            final int id,
            final FriendlyByteBuf friendlyByteBuf
    ) {
        final NetworkMessageSpecification<T> spec = (NetworkMessageSpecification<T>) messageSpecifications.get(id);
        return spec.creator.apply(friendlyByteBuf);
    }

    public <T> void handleMessageExecution(
            final int id,
            final T message,
            IPayloadContext context
    ) {
        final NetworkMessageSpecification<T> spec = (NetworkMessageSpecification<T>) messageSpecifications.get(id);
        spec.executionHandler.execute(
                message,
                context.flow().isServerbound(),
                context.player().orElse(null),
                runnable -> context.workHandler().execute(runnable)
        );
    }

    @Override
    public <T> void register(
            final int id,
            final Class<T> msgClass,
            final BiConsumer<T, FriendlyByteBuf> serializer,
            final Function<FriendlyByteBuf, T> creator,
            final MessageExecutionHandler<T> executionHandler) {
        final NetworkMessageSpecification<T> spec = new NetworkMessageSpecification<>(id, msgClass, serializer, creator, executionHandler);
        this.messageSpecifications.put(
                spec.id,
                spec
        );
    }

    @SuppressWarnings("unchecked")
    private <T> NetworkMessageSpecification<T> getSpec(final Class<T> networkMessageClass) {
        return (NetworkMessageSpecification<T>) messageSpecifications.values().stream().filter(
                        spec -> spec.msgClass.equals(networkMessageClass)
                )
                .findFirst()
                .orElseThrow();
    }

    @Override
    public void sendToServer(final Object msg) {
        PacketDistributor.SERVER.noArg().send(new Payload(msg));
    }

    @Override
    public void sendToPlayer(final Object msg, final ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(new Payload(msg));
    }

    @Override
    public void handle(@NotNull Payload payload, @NotNull IPayloadContext context) {
        handleMessageExecution(payload.id, payload.msg, context);
    }

    public Payload read(final FriendlyByteBuf buf) {
        return new Payload(buf);
    }

    public final class Payload implements CustomPacketPayload {

        private int id;
        private Object msg;

        public Payload(Object msg) {
            this.msg = msg;
        }

        public Payload(FriendlyByteBuf buf) {
            this.id = buf.readVarInt();
            this.msg = handleMessageDeserialization(id, buf);
        }

        @Override
        public void write(@NotNull FriendlyByteBuf pBuffer) {
            pBuffer.writeVarInt(id);
            handleMessageSerialization(msg, pBuffer);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return name;
        }
    }

    static final class NetworkMessageSpecification<T> {
        private final int id;
        private final Class<T> msgClass;
        private final BiConsumer<T, FriendlyByteBuf> serializer;
        private final Function<FriendlyByteBuf, T> creator;
        private final MessageExecutionHandler<T> executionHandler;

        private NetworkMessageSpecification(
                final int id,
                final Class<T> msgClass,
                final BiConsumer<T, FriendlyByteBuf> serializer,
                final Function<FriendlyByteBuf, T> creator, final MessageExecutionHandler<T> executionHandler) {
            this.id = id;
            this.msgClass = msgClass;
            this.serializer = serializer;
            this.creator = creator;
            this.executionHandler = executionHandler;
        }

        public int getId() {
            return id;
        }

        public Class<T> getMsgClass() {
            return msgClass;
        }

        public BiConsumer<T, FriendlyByteBuf> getSerializer() {
            return serializer;
        }

        public Function<FriendlyByteBuf, T> getCreator() {
            return creator;
        }

        public MessageExecutionHandler<T> getExecutionHandler() {
            return executionHandler;
        }
    }
}
