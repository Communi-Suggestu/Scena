package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;

/**
 * An event which is fired after the world has been rendered.
 * <p>
 *     Can not be used to perform any rendering, and is only used to get notified of frame
 *     generation information.
 * </p>
 */
public interface IPostRenderLevelEvent extends IEvent {

    /**
     * Handles the event.
     */
    void handle();
}
