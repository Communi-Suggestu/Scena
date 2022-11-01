package com.communi.suggestu.scena.core.event;

/**
 * Defines a manager for a specific event.
 * @param <T> The event type.
 */
public interface IEventEntryPoint<T extends IEvent> {

    /**
     * Register a handler for that event.
     *
     * @param handler The handler.
     */
    void register(T handler);
}
