package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;

/**
 * An event fired when a highlight around a box is about to be drawn.
 */
@FunctionalInterface
public interface IDrawHighlightEvent extends IEvent {

    /**
     * Invoked when a highlight around a box is about to be drawn.
     *
     * @return True when then handler has handled the drawing himself and vanilla highlight drawing should be cancelled.
     */
    boolean handle();
}
