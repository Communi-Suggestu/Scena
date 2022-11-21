package com.communi.suggestu.scena.core.event;

import com.communi.suggestu.scena.core.IScenaPlatform;

/**
 * Defines events which are related to the running of the game.
 */
public interface IGameEvents {

    /**
     * The current game event entry points.
     *
     * @return The current game event entry points.
     */
    static IGameEvents getInstance() {
        return IScenaPlatform.getInstance().getGameEvents();
    }

    /**
     * The entry point for an entity pickup event.
     * @return The entry point for the entity pickup event.
     */
    IEventEntryPoint<IItemEntityPickupEvent> getItemEntityPickupEvent();

    /**
     * The entry point for when a player left-clicks a block.
     * 
     * @return The entry point for the player left-click block event.
     */
    IEventEntryPoint<IPlayerLeftClickBlockEvent> getPlayerLeftClickEvent();

    /**
     * The entry point for when a player right-clicks a block.
     *
     * @return The entry point for the player right-click block event.
     */
    IEventEntryPoint<IPlayerRightClickBlockEvent> getPlayerRightClickEvent();

    /**
     * The entry point for when a player joins a world.
     *
     * @return The entry point for the player joined world event.
     */
    IEventEntryPoint<IPlayerJoinedWorldEvent> getPlayerJoinedWorldEvent();

    /**
     * The entry point for when a player logs in.
     *
     * @return The entry point for the player logged in event.
     */
    IEventEntryPoint<IPlayerLoggedInEvent> getPlayerLoggedInEvent();

    /**
     * The entry point for when commands need to be registered.
     *
     * @return The entry point for the command registration event.
     */
    IEventEntryPoint<IRegisterCommandsEvent> getRegisterCommandsEvent();

    /**
     * The entry point for when a server is about to start.
     *
     * @return The entry point for the server about to start event.
     */
    IEventEntryPoint<IServerAboutToStartEvent> getServerAboutToStartEvent();

    /**
     * The entry point for when a chunk is loaded.
     *
     * @return The entry point for the chunk loaded event.
     */
    IEventEntryPoint<IChunkLoadEvent> getChunkLoadEvent();

    /**
     * The entry point for when the common configuration has been loaded.
     *
     * @return The entry point for the common configuration loaded event.
     */
    IEventEntryPoint<ICommonConfigurationLoaded> getCommonConfigurationLoadedEvent();
}
