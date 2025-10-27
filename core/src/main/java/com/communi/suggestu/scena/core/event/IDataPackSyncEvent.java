package com.communi.suggestu.scena.core.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Defines events which are related to the synchronization of data packs.
 */
public interface IDataPackSyncEvent extends IEvent {

    /**
     * Invoked when the data pack is synchronized.
     *
     * @param playerList The player list.
     * @param players The players to synchronize with.
     * @param recipeTypeToSendHandler Consumer of recipe types to sync.
     */
    void onSync(PlayerList playerList, @Nullable Stream<ServerPlayer> players, Consumer<RecipeType<?>> recipeTypeToSendHandler);
}
