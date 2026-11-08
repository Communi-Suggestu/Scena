package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.resources.Identifier;

/**
 * Event fired when the level render state is extracted. This event allows for the modification of the level render state before it is used for rendering.
 * <p>
 *     If you want to store additional data in the level render state, you can use the provided {@link IRenderStateStore} to store your data with a unique {@link Identifier}.
 * </p>
 */
public interface IExtractLevelRenderStateEvent extends IEvent {

    /**
     * Invoked when the level render state is extracted. This method allows for the modification of the level render state before it is used for rendering.
     *
     * @param currentState The current level render state.
     * @param level The client level.
     * @param camera The camera used for rendering.
     * @param deltaTracker The delta tracker used for rendering.
     * @param store The store used to store additional data in the level render state.
     */
    void handle(
            LevelRenderState currentState,
            ClientLevel level,
            Camera camera,
            DeltaTracker deltaTracker,
            IRenderStateStore store);

    /**
     * Interface for storing additional data in the level render state. Implementations of this interface should provide a way to store and retrieve data associated with a unique {@link Identifier}.
     */
    interface IRenderStateStore {

        /**
         * Stores a value associated with the given identifier in the level render state.
         *
         * @param state The identifier for the value to be stored.
         * @param value The value to be stored.
         * @param <T> The type of the value to be stored.
         */
        <T> void write(Identifier state, T value);
    }
}
