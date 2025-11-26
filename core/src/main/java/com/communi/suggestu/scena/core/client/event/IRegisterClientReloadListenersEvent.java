package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

/**
 * Event that registers a new reload listener on the client.
 */
public interface IRegisterClientReloadListenersEvent extends IEvent
{

    /**
     * Invoked when the event triggers to register listeners.
     *
     * @param registrar The registrar.
     */
    void handle(Registrar registrar);

    /**
     * The registry to register the listeners to.
     */
    public interface Registrar {

        /**
         * Registers a new listener with the given key.
         *
         * @param key The key
         * @param listener The listener
         */
        void addListener(Identifier key, PreparableReloadListener listener);
    }
}
