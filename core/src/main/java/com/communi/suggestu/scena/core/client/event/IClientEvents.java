package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;

public interface IClientEvents {

    static IClientEvents getInstance() {
        return IScenaPlatform.getInstance().getClientManager().getClientEvents();
    }

    /**
     * The entry point for client tick events
     *
     * @return The entry point for client tick events
     */
    IEventEntryPoint<IClientTickStartedEvent> getClientTickStartedEvent();

    /**
     * The entry point for the draw highlight event.
     *
     * @return The entry point for the draw highlight event.
     */
    IEventEntryPoint<IDrawHighlightEvent> getDrawHighlightEvent();

    /**
     * The entry point for the HUD render event.
     *
     * @return The entry point for the HUD render event.
     */
    IEventEntryPoint<IHudRenderEvent> getHUDRenderEvent();

    /**
     * The entry point for the scroll event.
     *
     * @return The entry point for the scroll event.
     */
    IEventEntryPoint<IScrollEvent> getScrollEvent();

    /**
     * The entry point for the post render world event.
     *
     * @return The entry point for the post render world event.
     */
    IEventEntryPoint<IPostRenderWorldEvent> getPostRenderWorldEvent();
}
