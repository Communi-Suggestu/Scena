package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;

/**
 * Marker event that indicates when it is safe to register common client GPU resources like TextureMaps.
 */
@FunctionalInterface
public interface IResourceRegistrationEvent extends IEvent {

    /**
     * Invoked when the GPU resources are safe to register.
     */
    void handle();
}
