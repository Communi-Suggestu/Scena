package com.communi.suggestu.scena.core.event;

/**
 * An event fired when the common configuration has been loaded.
 */
public interface ICommonConfigurationLoaded extends IEvent {

    /**
     * The event entry point.
     */
    void handle();
}
