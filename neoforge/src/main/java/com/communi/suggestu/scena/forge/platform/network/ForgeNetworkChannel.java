package com.communi.suggestu.scena.forge.platform.network;

import com.communi.suggestu.scena.core.network.INetworkChannel;
import com.communi.suggestu.scena.core.network.PayloadDirection;
import com.communi.suggestu.scena.core.network.PayloadPhase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.extensions.IServerCommonPacketListenerExtension;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.util.HashSet;
import java.util.Set;

public class ForgeNetworkChannel implements INetworkChannel {

    private final String version;
    private final Set<Registration<?, ?>> registrations = new HashSet<>();

    public ForgeNetworkChannel(String version) {
        this.version = version;
    }

    public String version() {
        return version;
    }

    public Set<Registration<?, ?>> registrations() {
        return registrations;
    }

    @Override
    public <T extends CustomPacketPayload, B extends FriendlyByteBuf> void register(
            CustomPacketPayload.Type<T> type,
            StreamCodec<B, T> codec,
            MessageExecutionHandler<T> handler,
            PayloadPhase<B> phase,
            PayloadDirection direction) {
        registrations.add(new Registration<>(type, codec, handler, phase, direction));
    }

    @Override
    public void sendToServer(CustomPacketPayload msg) {
        ClientPacketDistributor.sendToServer(msg);
    }

    @Override
    public void sendToPlayer(CustomPacketPayload msg, ServerPacketListener listener) {
        if (!canSendTo(msg, listener)) {
            return;
        }

        final IServerCommonPacketListenerExtension extension = (IServerCommonPacketListenerExtension) listener;
        extension.send(msg);
    }

    @Override
    public boolean canSendTo(final CustomPacketPayload msg, final ServerPacketListener listener) {
        if (!(listener instanceof IServerCommonPacketListenerExtension extension)) {
            return false;
        }

        return NetworkRegistry.hasChannel(extension, msg.type().id());
    }

    public record Registration<T extends CustomPacketPayload, B extends FriendlyByteBuf>(
            CustomPacketPayload.Type<T> type,
            StreamCodec<B, T> codec,
            MessageExecutionHandler<T> handler,
            PayloadPhase<B> phase,
            PayloadDirection direction
    ) {
    }
}
