package com.communi.suggestu.scena.forge.platform.network;

import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.network.INetworkChannel;
import com.communi.suggestu.scena.core.network.INetworkChannelManager;
import com.communi.suggestu.scena.core.network.PayloadDirection;
import com.communi.suggestu.scena.core.network.PayloadPhase;
import com.communi.suggestu.scena.forge.accessors.CommonAccessors;
import com.communi.suggestu.scena.forge.platform.client.accessors.ClientAccessors;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class ForgeNetworkChannelManager implements INetworkChannelManager {
    private static final ForgeNetworkChannelManager INSTANCE = new ForgeNetworkChannelManager();

    public static ForgeNetworkChannelManager getInstance() {
        return INSTANCE;
    }

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final ConcurrentLinkedDeque<ForgeNetworkChannel> channels = new ConcurrentLinkedDeque<>();

    private ForgeNetworkChannelManager() {
    }

    @Override
    public INetworkChannel create(String version, Consumer<INetworkChannel> configurator) {
        if (initialized.get()) {
            throw new IllegalStateException("Cannot create a new network channel after initialization");
        }

        final ForgeNetworkChannel channel = new ForgeNetworkChannel(version);
        configurator.accept(channel);
        channels.add(channel);
        return channel;
    }


    @SubscribeEvent
    public static void onRegisterNetworkChannels(final RegisterPayloadHandlersEvent event) {
        getInstance().initialized.set(true);
        getInstance().channels.forEach((channel) -> {
            final PayloadRegistrar registrar = event.registrar(channel.version())
                    .executesOn(HandlerThread.NETWORK);

            channel.registrations().forEach(registration -> registerNetworkChannel(registration, registrar));
        });
    }

    @SubscribeEvent
    public static void onRegisterNetworkChannels(final RegisterClientPayloadHandlersEvent event) {
        getInstance().initialized.set(true);
        getInstance().channels.forEach((channel) -> {
            channel.registrations().forEach(registration -> {
                registerNetworkChannel(registration, event);
            });
        });
    }

    private static <T extends CustomPacketPayload, B extends FriendlyByteBuf> void registerNetworkChannel(
        final ForgeNetworkChannel.Registration<T, B> registration,
        final RegisterClientPayloadHandlersEvent event
    ) {
        if (registration.direction().requiresClientHandler()) {
            event.register(
                registration.type(),
                HandlerThread.NETWORK,
                createPayloadHandlerFor(registration)
            );
        }
    }

    private static <T extends CustomPacketPayload, B extends FriendlyByteBuf> @NotNull IPayloadHandler<T> createPayloadHandlerFor(final ForgeNetworkChannel.Registration<T, B> registration)
    {
        return (payload, context) -> {
            Player player;
            try
            {
                player = context.player();
            }
            catch (Exception e)
            {
                //noinspection Convert2MethodRef
                player = DistExecutor.unsafeRunForDist(
                    () -> () -> ClientAccessors.getPlayer(),
                    () -> () -> CommonAccessors.getNull()
                );
            }

            registration.handler().execute(payload,
                context.flow() == PacketFlow.SERVERBOUND,
                player,
                context::enqueueWork);
        };
    }

    private static <T extends CustomPacketPayload, B extends FriendlyByteBuf> void registerNetworkChannel(
            final ForgeNetworkChannel.Registration<T, B> registration,
            final PayloadRegistrar registrar
    ) {
        final CustomPacketPayload.Type<T> type = registration.type();
        final StreamCodec<B, T> codec = registration.codec();
        final PayloadPhase<B> phase = registration.phase();
        final PayloadDirection direction = registration.direction();

        final Registrator<T, B> registrator = getRegistrator(registrar, phase, direction);
        registrator.register(type, codec, createPayloadHandlerFor(registration));
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload, B extends FriendlyByteBuf> Registrator<T, B> getRegistrator(
            final PayloadRegistrar registrar,
            final PayloadPhase<B> phase,
            final PayloadDirection direction
    ) {
        if (phase == PayloadPhase.PLAY) {
            if (direction == PayloadDirection.BOTH) {
                return (type, reader, handler) -> registrar.playBidirectional(type, (StreamCodec<RegistryFriendlyByteBuf, T>) reader, handler);
            }
            if (direction == PayloadDirection.CLIENTBOUND) {
                return (type, reader, handler) -> registrar.playToClient(type, (StreamCodec<RegistryFriendlyByteBuf, T>) reader, handler);
            }
            if (direction == PayloadDirection.SERVERBOUND) {
                return (type, reader, handler) -> registrar.playToServer(type, (StreamCodec<RegistryFriendlyByteBuf, T>) reader, handler);
            }

            throw new IllegalArgumentException("Unknown direction: " + direction);
        }

        if (phase == PayloadPhase.CONFIG) {
            if (direction == PayloadDirection.BOTH) {
                return (type, reader, handler) -> registrar.configurationBidirectional(type, (StreamCodec<FriendlyByteBuf, T>) reader, handler);
            }
            if (direction == PayloadDirection.CLIENTBOUND) {
                return (type, reader, handler) -> registrar.configurationToClient(type, (StreamCodec<FriendlyByteBuf, T>) reader, handler);
            }
            if (direction == PayloadDirection.SERVERBOUND) {
                return (type, reader, handler) -> registrar.configurationToServer(type, (StreamCodec<FriendlyByteBuf, T>) reader, handler);
            }

            throw new IllegalArgumentException("Unknown direction: " + direction);
        }

        throw new IllegalArgumentException("Unknown phase: " + phase);
    }

    public interface Registrator<T extends CustomPacketPayload, B extends FriendlyByteBuf> {
        void register(CustomPacketPayload.Type<T> type, StreamCodec<B, T> reader, IPayloadHandler<T> handler);
    }
}
