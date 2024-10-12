package com.communi.suggestu.scena.forge.platform.client.event;

import com.communi.suggestu.scena.core.client.event.*;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.core.event.IGatherTooltipEvent;
import com.communi.suggestu.scena.forge.platform.event.EventBusEventEntryPoint;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class ForgeClientEvents implements IClientEvents {
    private static final ForgeClientEvents INSTANCE = new ForgeClientEvents();

    public static ForgeClientEvents getInstance() {
        return INSTANCE;
    }

    private ForgeClientEvents() {
    }

    @Override
    public IEventEntryPoint<IClientTickStartedEvent> getClientTickStartedEvent() {
        return EventBusEventEntryPoint.forge(ClientTickEvent.Pre.class, (event, handler) -> {
            handler.handle();
        });
    }

    @Override
    public IEventEntryPoint<IDrawHighlightEvent> getDrawHighlightEvent() {
        return EventBusEventEntryPoint.forge(RenderHighlightEvent.Block.class, (event, handler) -> {
            event.setCanceled(handler.handle());
        });
    }

    @Override
    public IEventEntryPoint<IHudRenderEvent> getHUDRenderEvent() {
        return EventBusEventEntryPoint.forge(RenderGuiEvent.Post.class, (event, handler) -> {
            handler.handle(event.getGuiGraphics());
        });
    }

    @Override
    public IEventEntryPoint<IScrollEvent> getScrollEvent() {
        return EventBusEventEntryPoint.forge(InputEvent.MouseScrollingEvent.class, (event, handler) -> {
            event.setCanceled(handler.handle(event.getScrollDeltaY()));
        });
    }

    @Override
    public IEventEntryPoint<IPostRenderWorldEvent> getPostRenderWorldEvent() {
        return EventBusEventEntryPoint.forge(ForgeScenaRenderWorldLastEvent.class, (event, handler) -> {
            handler.handle(event.getLevelRenderer(), event.getPoseStack(), event.getPartialTicks());
        });
    }

    @Override
    public IEventEntryPoint<IResourceRegistrationEvent> getResourceRegistrationEvent() {
        return EventBusEventEntryPoint.mod(RegisterColorHandlersEvent.Block.class, (event, handler) -> {
            handler.handle();
        });
    }

    @Override
    public IEventEntryPoint<IGatherTooltipEvent> getGatherTooltipEvent() {
        return EventBusEventEntryPoint.forge(ItemTooltipEvent.class, (event, handler) -> handler.handle(
                event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip()
        ));
    }
}
