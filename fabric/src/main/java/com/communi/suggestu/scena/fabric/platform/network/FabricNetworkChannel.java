package com.communi.suggestu.scena.fabric.platform.network;

import com.communi.suggestu.scena.core.network.INetworkChannel;
import com.communi.suggestu.scena.core.network.PayloadDirection;
import com.communi.suggestu.scena.core.network.PayloadPhase;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class FabricNetworkChannel implements INetworkChannel {

    private final Set<CustomPacketPayload.Type<?>> CONFIG_TYPES = new HashSet<>();
    private final Set<CustomPacketPayload.Type<?>> PLAY_TYPES = new HashSet<>();

    public FabricNetworkChannel() {
    }


    @Override
    public <T extends CustomPacketPayload, B extends FriendlyByteBuf> void register(CustomPacketPayload.Type<T> type, StreamCodec<B, T> codec, MessageExecutionHandler<T> handler, PayloadPhase<B> phase, PayloadDirection direction) {
        getTypeRegistry(phase, direction).forEach(registry -> registry.register(type, codec));
        registryReceiver(type, handler, phase, direction);

        if (phase == PayloadPhase.CONFIG) {
            CONFIG_TYPES.add(type);
        } else if (phase == PayloadPhase.PLAY) {
            PLAY_TYPES.add(type);
        }
    }

    @SuppressWarnings("unchecked")
    private static <B extends FriendlyByteBuf> Iterable<PayloadTypeRegistry<B>> getTypeRegistry(PayloadPhase<B> phase, PayloadDirection payloadDirection) {
        if (phase == PayloadPhase.CONFIG) {
            if (payloadDirection == PayloadDirection.SERVERBOUND) {
                return List.of((PayloadTypeRegistry<B>) PayloadTypeRegistry.configurationC2S());
            } else if (payloadDirection == PayloadDirection.CLIENTBOUND) {
                return List.of((PayloadTypeRegistry<B>) PayloadTypeRegistry.configurationS2C());
            } else if (payloadDirection == PayloadDirection.BOTH) {
                return List.of((PayloadTypeRegistry<B>) PayloadTypeRegistry.configurationC2S(), (PayloadTypeRegistry<B>) PayloadTypeRegistry.configurationS2C());
            }
        } else if (phase == PayloadPhase.PLAY) {
            if (payloadDirection == PayloadDirection.SERVERBOUND) {
                return List.of((PayloadTypeRegistry<B>) PayloadTypeRegistry.playC2S());
            } else if (payloadDirection == PayloadDirection.CLIENTBOUND) {
                return List.of((PayloadTypeRegistry<B>) PayloadTypeRegistry.playS2C());
            } else if (payloadDirection == PayloadDirection.BOTH) {
                return List.of((PayloadTypeRegistry<B>) PayloadTypeRegistry.playC2S(), (PayloadTypeRegistry<B>) PayloadTypeRegistry.playS2C());
            }
        }

        throw new IllegalStateException("Unknown phase or direction");
    }

    private static <T extends CustomPacketPayload> void registryReceiver(CustomPacketPayload.Type<T> type, MessageExecutionHandler<T> handler, PayloadPhase<?> phase, PayloadDirection direction) {
        if (phase == PayloadPhase.PLAY) {
            if (direction == PayloadDirection.CLIENTBOUND || direction == PayloadDirection.BOTH) {
                ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.execute(payload, false, context.player(), context.client()::execute));
            }

            if (direction == PayloadDirection.SERVERBOUND || direction == PayloadDirection.BOTH) {
                ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.execute(payload, true, context.player(), context.server()::execute));
            }
        }

        if (phase == PayloadPhase.CONFIG) {
            if (direction == PayloadDirection.CLIENTBOUND || direction == PayloadDirection.BOTH) {
                ClientConfigurationNetworking.registerGlobalReceiver(type, (payload, context) -> handler.execute(payload, false, null, context.client()::execute));
            }

            if (direction == PayloadDirection.SERVERBOUND || direction == PayloadDirection.BOTH) {
                ServerConfigurationNetworking.registerGlobalReceiver(type, (payload, context) -> handler.execute(payload, true, null, context.server()::execute));
            }
        }

    }

    @Override
    public void sendToServer(CustomPacketPayload msg) {
        if (CONFIG_TYPES.contains(msg.type())) {
            ClientConfigurationNetworking.send(msg);
        } else if (PLAY_TYPES.contains(msg.type())) {
            ClientPlayNetworking.send(msg);
        }

        throw new IllegalStateException("Payload type is not registered.");
    }

    @Override
    public void sendToPlayer(CustomPacketPayload msg, ServerPacketListener listener) {
        if (CONFIG_TYPES.contains(msg.type()) && listener instanceof ServerConfigurationPacketListenerImpl packetListener) {
            ServerConfigurationNetworking.send(packetListener, msg);
        } else if (PLAY_TYPES.contains(msg.type()) && listener instanceof ServerGamePacketListenerImpl packetListener) {
            ServerPlayNetworking.send(packetListener.getPlayer(), msg);
        }

        throw new IllegalStateException("Payload type is not registered or wrong listener type.");
    }

    static final class NetworkMessageSpecification<T> {
        private final int id;
        private final Class<T> msgClass;
        private final BiConsumer<T, FriendlyByteBuf> serializer;
        private final Function<FriendlyByteBuf, T> creator;
        private final MessageExecutionHandler<T> executionHandler;

        private NetworkMessageSpecification(final int id, final Class<T> msgClass, final BiConsumer<T, FriendlyByteBuf> serializer, final Function<FriendlyByteBuf, T> creator, final MessageExecutionHandler<T> executionHandler) {
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
