package com.communi.suggestu.scena.forge.platform.client.event;

import com.communi.suggestu.scena.core.client.event.*;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.core.event.IGatherTooltipEvent;
import com.communi.suggestu.scena.forge.platform.event.EventBusEventEntryPoint;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.gui.overlay.GuiOverlayManager;
import net.neoforged.neoforge.event.TickEvent;
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
        return EventBusEventEntryPoint.forge(TickEvent.ClientTickEvent.class, (event, handler) -> {
            if (event.phase != TickEvent.Phase.START)
                return;

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
        return EventBusEventEntryPoint.forge(RenderGuiOverlayEvent.Post.class, (event, handler) -> {
            if (event.getOverlay() != GuiOverlayManager.getOverlays().get(GuiOverlayManager.getOverlays().size() - 1))
                return;

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
        return EventBusEventEntryPoint.forge(ItemTooltipEvent.class, (event, handler) -> handler.handle(event.getItemStack(), event.getToolTip()));
    }
}
