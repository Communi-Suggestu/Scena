package com.communi.suggestu.scena.forge.platform.client.event;

import com.communi.suggestu.scena.core.client.event.*;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.forge.platform.event.EventBusEventEntryPoint;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.event.TickEvent;

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

            handler.handle(event.getPoseStack());
        });
    }

    @Override
    public IEventEntryPoint<IScrollEvent> getScrollEvent() {
        return EventBusEventEntryPoint.forge(InputEvent.MouseScrollingEvent.class, (event, handler) -> {
            event.setCanceled(handler.handle(event.getScrollDelta()));
        });
    }

    @Override
    public IEventEntryPoint<IPostRenderWorldEvent> getPostRenderWorldEvent() {
        return EventBusEventEntryPoint.forge(ForgeScenaRenderWorldLastEvent.class, (event, handler) -> {
            handler.handle(event.getLevelRenderer(), event.getPoseStack(), event.getPartialTicks());
        });
    }
}
