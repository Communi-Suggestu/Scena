package com.communi.suggestu.scena.core.network;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.*;

public interface INetworkChannel
{

    /**
     * Register a new network channel.
     */
    static INetworkChannel create() {
        return INetworkChannelManager.getInstance().create();
    }

    /**
     * Register a new network channel.
     *
     * @param type The type of the channel.
     * @param codec The codec to use for the channel.
     * @param handler The handler to use for the channel.
     * @param <T> The type of the channel.
     */
    default <T extends CustomPacketPayload> void register(
            final CustomPacketPayload.Type<T> type,
            final StreamCodec<RegistryFriendlyByteBuf, T> codec,
            final MessageExecutionHandler<T> handler
            ) {
        register(
                type,
                codec,
                handler,
                PayloadPhase.PLAY,
                PayloadDirection.BOTH
        );
    }

    /**
     * Register a new network channel.
     *
     * @param type The type of the channel.
     * @param codec The codec to use for the channel.
     * @param handler The handler to use for the channel.
     * @param phase The phase of the channel.
     * @param direction The direction of the channel.
     * @param <T> The type of the channel.
     * @param <B> The type of the buffer.
     */
    <T extends CustomPacketPayload, B extends FriendlyByteBuf> void register(
            final CustomPacketPayload.Type<T> type,
            final StreamCodec<B, T> codec,
            final MessageExecutionHandler<T> handler,
            final PayloadPhase<B> phase,
            final PayloadDirection direction
            );

    /**
     * Sends the packet to the server.
     *
     * @param msg The message send.
     */
    void sendToServer(CustomPacketPayload msg);

    /**
     * Sends the message to the player.
     *
     * @param msg The message to send.
     * @param player The player to send the message to.
     */
    default void sendToPlayer(CustomPacketPayload msg, ServerPlayer player) {
        sendToPlayer(msg, player.connection);
    }

    /**
     * Sends the message to the player.
     *
     * @param msg The message to send.
     * @param listener The listener to send the message to.
     */
    void sendToPlayer(CustomPacketPayload msg, ServerPacketListener listener);

    /**
     * Sends to everyone.
     *
     * @param msg message to send
     */
    default void sendToEveryone(final CustomPacketPayload msg)
    {
        IScenaPlatform.getInstance().getCurrentServer().getPlayerList().getPlayers().forEach(player -> sendToPlayer(msg, player));
    }

    /**
     * Sends to everyone in given chunk.
     *
     * @param msg   message to send
     * @param chunk target chunk to look at
     */
    default void sendToTrackingChunk(final CustomPacketPayload msg, final LevelChunk chunk)
    {
        ((ServerChunkCache)chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(serverPlayer -> sendToPlayer(msg, serverPlayer));
    }

    /**
     * Defines a handler that can be invoked when the message arrives at the receiving side.
     * @param <T> The type of the message.
     */
    interface MessageExecutionHandler<T> {

        /**
         * Invoked on a network when the message is received.
         *
         * @param message The message that has been received.
         * @param serverSide Indicates if the current executing side is the server.
         * @param player The player which send the data, null if not send by a player, maybe null during configuration phase
         * @param executor The executor to execute code on the game thread.
         */
        void execute(
          final T message,
          final boolean serverSide,
          @UnknownNullability final Player player,
          final Consumer<Runnable> executor
        );
    }
}
