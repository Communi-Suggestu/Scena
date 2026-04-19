package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;

import java.util.function.Function;

/**
 * Event used to register new Picture in Picture renderers for GUIs
 */
@FunctionalInterface
public interface IRegisterPIPRenderersEvent extends IEvent
{

    /**
     * Used to register new pip render states and renderers to the given registrar.
     *
     * @param registrar The registrar.
     */
    void handle(Registrar registrar);

    interface Registrar {
        /**
         * Register a custom {@link PictureInPictureRenderer} factory.
         *
         * @param stateClass The type of state that the renderers constructed by the given factory can handle.
         * @param factory    A function to construct a PiP renderer
         */
        <T extends PictureInPictureRenderState> void register(Class<T> stateClass,
            Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<T>> factory);
    }
}
