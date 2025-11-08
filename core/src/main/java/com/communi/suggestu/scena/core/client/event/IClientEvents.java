package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.core.event.IGatherTooltipEvent;

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

    /**
     * The entry point for the resource registration event.
     *
     * @return The entry point for the resource registration event.
     */
    IEventEntryPoint<IResourceRegistrationEvent> getResourceRegistrationEvent();

    /**
     * The entry point for when tooltips of an itemstack are gathered.
     *
     * @return The entry point for the tooltip gather event.
     */
    IEventEntryPoint<IGatherTooltipEvent> getGatherTooltipEvent();

    /**
     * The entry point for when tooltip components are gathered.
     *
     * @return The entry point for the tooltip components gather event.
     */
    IEventEntryPoint<IGatherTooltipComponentsEvent> getGatherTooltipComponentsEvent();

    /**
     * The entry point for the registration event of texture atlases.
     * Fired at the beginning of the construction of the atlas manager.
     *
     * @return The entry point.
     */
    IEventEntryPoint<IRegisterTextureAtlasesEvent> getRegisterTextureAtlasesEvent();

    /**
     * The entry point for the registration event of picture in picture renderers.
     *
     * @return The entry point.
     */
    IEventEntryPoint<IRegisterPIPRenderersEvent> getRegisterPIPRenderersEvent();

    /**
     * The entry point for the registration event of the block state model system.
     *
     * @return The entry point
     */
    IEventEntryPoint<IRegisterBlockStateModelEvent> getRegisterBlockStateModelEvent();

    /**
     * The entry point for the registration event of the item model system.
     *
     * @return The entry point
     */
    IEventEntryPoint<IRegisterItemModelEvent> getRegisterItemModelEvent();

    /**
     * The entry point for the registration event of resource reload listeners
     *
     * @return The entry point
     */
    IEventEntryPoint<IRegisterClientReloadListenersEvent> getRegisterClientResourceReloadListenersEvent();
}
