package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.resources.Identifier;

/**
 * An event fired when custom geometry is about to be submitted to the renderer.
 */
public interface ISubmitCustomGeometryEvent extends IEvent {

    /**
     * Invoked when custom geometry is about to be submitted to the renderer.
     *
     * @param nodeCollector The node collector used to submit the custom geometry.
     * @param poseStack The pose stack used for rendering.
     * @param renderState The current level render state.
     * @param store The store used to read additional data from the level render state.
     */
    void handle(
        SubmitNodeCollector nodeCollector,
        PoseStack poseStack,
        LevelRenderState renderState,
        IRenderStateStore store
    );

    /**
     * Interface for reading additional data from the level render state. Implementations of this interface should provide a way to read data associated with a unique {@link Identifier}.
     * <p>
     *     To extract and store additional data in the level render state, you can use the {@link IExtractLevelRenderStateEvent} event. This allows you to store and retrieve data associated with a unique {@link Identifier}.
     * </p>
     */
    interface IRenderStateStore {

        /**
         * Reads a value associated with the given identifier from the level render state.
         *
         * @param <T>        The type of the value to be read.
         * @param identifier The identifier for the value to be read.
         * @return The value associated with the given identifier, or null if no value is found.
         */
        <T> T read(Identifier identifier);
    }
}
