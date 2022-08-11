package com.communi.suggestu.scena.core.network;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.*;

public interface INetworkChannel
{

    /**
     * Register a new network channel.
     *
     * @param name The name of the channel
     * @param networkProtocolVersion The version of the protocol on this logical side of the channel.
     * @param clientAcceptedVersions The versions of the client protocol that are accepted.
     * @param serverAcceptedVersions The versions of the server protocol that are accepted.
     */
    static INetworkChannel create(final ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return INetworkChannelManager.getInstance().create(
          name,
          networkProtocolVersion,
          clientAcceptedVersions,
          serverAcceptedVersions
        );
    }

    /**
     * Registers a new message and its handler, so it can be transmitted over the channel.
     *
     * @param id The id of the message.
     * @param msgClass The message class.
     * @param serializer The serializer for the message.
     * @param creator The instantiation handler of the message.
     * @param executionHandler The handler which is executed when the message arrives.
     * @param <T> The type of the message.
     */
    <T> void register(
      final int id,
      final Class<T> msgClass,
      final BiConsumer<T, FriendlyByteBuf> serializer,
      final Function<FriendlyByteBuf, T> creator,
      final MessageExecutionHandler<T> executionHandler
    );

    /**
     * Sends the packet to the server.
     *
     * @param msg The message send.
     */
    void sendToServer(Object msg);

    /**
     * Sends the message to the player.
     *
     * @param msg The message to send.
     * @param player The player to send the message to.
     */
    void sendToPlayer(Object msg, ServerPlayer player);

    /**
     * Sends to everyone.
     *
     * @param msg message to send
     */
    default void sendToEveryone(final Object msg)
    {
        IScenaPlatform.getInstance().getCurrentServer().getPlayerList().getPlayers().forEach(player -> sendToPlayer(msg, player));
    }

    /**
     * Sends to everyone in given chunk.
     *
     * @param msg   message to send
     * @param chunk target chunk to look at
     */
    default void sendToTrackingChunk(final Object msg, final LevelChunk chunk)
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
         * @param player The player which send the data, null if not send by a player.
         * @param executor The executor to execute code on the game thread.
         */
        void execute(
          final T message,
          final boolean serverSide,
          final Player player,
          final Consumer<Runnable> executor
        );
    }
}
