package com.communi.suggestu.scena.core.event;

import org.apache.commons.lang3.NotImplementedException;

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

    /**
     * Indicates whether the current event is implemented on the current platform.
     * @return True when implemented, false when not.
     */
    default boolean isImplemented() {
        return !(this instanceof IEventEntryPoint.NotImplemented<T>);
    }

    /**
     * Underlying implementation for events which are not implemented on the current platform.
     * @param <T> The type of the event.
     */
    record NotImplemented<T extends IEvent>() implements IEventEntryPoint<T> {

        @Override
        public void register(final T handler)
        {
            throw new NotImplementedException("This event is not yet implemented on this platform. You can not register a handler to it!");
        }
    }

    /**
     * Creates a new event entry point which throws the given message on the first call to register.
     *
     * @param message The message to throw.
     * @return The entry point.
     * @param <G> The type of the event.
     */
    static <G extends IEvent> IEventEntryPoint<G> throwing(final String message) {
        return handler -> {
            throw new IllegalStateException(message);
        };
    }
}
