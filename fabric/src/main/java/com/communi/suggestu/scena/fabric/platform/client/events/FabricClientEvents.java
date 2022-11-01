package com.communi.suggestu.scena.fabric.platform.client.events;

import com.communi.suggestu.scena.core.client.event.*;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.fabric.platform.event.FabricEventEntryPoint;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Function;

public final class FabricClientEvents implements IClientEvents {
    private static final FabricClientEvents INSTANCE = new FabricClientEvents();

    public static FabricClientEvents getInstance() {
        return INSTANCE;
    }

    public static final Event<IScrollEvent> SCROLL = EventFactory.createArrayBacked(IScrollEvent.class, callbacks -> (final double scrollDelta) -> {
        boolean handled = false;
        for (IScrollEvent callback : callbacks) {
            if (callback.handle(scrollDelta)) {
                handled = true;
            }

            if (handled)
                return true;
        }

        return false;
    });

    private FabricClientEvents() {
    }

    @Override
    public IEventEntryPoint<IClientTickStartedEvent> getClientTickStartedEvent() {
        return FabricEventEntryPoint.create(ClientTickEvents.START_CLIENT_TICK, handler -> client -> handler.handle());
    }

    @Override
    public IEventEntryPoint<IDrawHighlightEvent> getDrawHighlightEvent() {
        return FabricEventEntryPoint.create(WorldRenderEvents.BEFORE_BLOCK_OUTLINE, handler -> (context, hitResult) -> handler.handle());
    }

    @Override
    public IEventEntryPoint<IHudRenderEvent> getHUDRenderEvent() {
        return FabricEventEntryPoint.create(HudRenderCallback.EVENT, handler -> (matrixStack, tickDelta) -> handler.handle(matrixStack));
    }

    @Override
    public IEventEntryPoint<IScrollEvent> getScrollEvent() {
        return FabricEventEntryPoint.create(SCROLL, Function.identity());
    }

}
