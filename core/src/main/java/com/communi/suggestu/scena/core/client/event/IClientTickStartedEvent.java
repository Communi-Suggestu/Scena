package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;

/**
 * An event fired when a client ticks.
 */
@FunctionalInterface
public interface IClientTickStartedEvent extends IEvent {

    /**
     * Invoked when a client ticks.
     */
    void handle();
}
