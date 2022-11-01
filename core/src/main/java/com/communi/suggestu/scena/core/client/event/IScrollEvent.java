package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;

/**
 * An event fired when the user scrolls with the mouse wheel.
 */
@FunctionalInterface
public interface IScrollEvent extends IEvent {

    /**
     * Invoked when the user scrolls with the mouse wheel.
     *
     * @param scrollDelta The amount scrolled.
     * @return True when the event has been handled and no vanilla logic should be processed.
     */
    boolean handle(double scrollDelta);
}
