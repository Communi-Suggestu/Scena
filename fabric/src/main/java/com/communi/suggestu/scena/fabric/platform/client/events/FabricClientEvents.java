package com.communi.suggestu.scena.fabric.platform.client.events;

import com.communi.suggestu.scena.core.client.event.*;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.core.event.IGatherTooltipEvent;
import com.communi.suggestu.scena.core.event.Settable;
import com.communi.suggestu.scena.fabric.platform.event.FabricEventEntryPoint;
import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
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

    public static final Event<IResourceRegistrationEvent> RESOURCE_REGISTRATION = EventFactory.createArrayBacked(IResourceRegistrationEvent.class, callbacks -> () -> {
        for (IResourceRegistrationEvent callback : callbacks) {
            callback.handle();
        }
    });

    public static final Event<IGatherTooltipComponentsEvent> GATHER_TOOLTIP_COMPONENTS = EventFactory.createArrayBacked(IGatherTooltipComponentsEvent.class, callbacks ->
            (itemStack, screenWidth, screenHeight, maxWidth, tooltipElements) -> {
        for(IGatherTooltipComponentsEvent callback : callbacks) {
            if (!callback.gather(itemStack, screenWidth, screenHeight, maxWidth, tooltipElements)) {
                return false;
            }
        }

        return true;
    });

    private FabricClientEvents() {
    }

    @Override
    public IEventEntryPoint<IClientTickStartedEvent> getClientTickStartedEvent() {
        return FabricEventEntryPoint.create(ClientTickEvents.START_CLIENT_TICK, handler -> client -> handler.handle());
    }

    @Override
    public IEventEntryPoint<IDrawHighlightEvent> getDrawHighlightEvent() {
        return FabricEventEntryPoint.create(WorldRenderEvents.BEFORE_BLOCK_OUTLINE, handler -> (context, hitResult) -> !handler.handle());
    }

    @Override
    public IEventEntryPoint<IHudRenderEvent> getHUDRenderEvent() {
        return FabricEventEntryPoint.create(HudRenderCallback.EVENT, handler -> (matrixStack, tickDelta) -> handler.handle(matrixStack));
    }

    @Override
    public IEventEntryPoint<IScrollEvent> getScrollEvent() {
        return FabricEventEntryPoint.create(SCROLL, Function.identity());
    }

    @Override
    public IEventEntryPoint<IPostRenderWorldEvent> getPostRenderWorldEvent() {
        return FabricEventEntryPoint.create(WorldRenderEvents.AFTER_TRANSLUCENT, handler -> (context) -> handler.handle(context.worldRenderer(), context.matrixStack(), context.camera().getPartialTickTime()));
    }

    @Override
    public IEventEntryPoint<IResourceRegistrationEvent> getResourceRegistrationEvent() {
        return FabricEventEntryPoint.create(RESOURCE_REGISTRATION, Function.identity());
    }

    @Override
    public IEventEntryPoint<IGatherTooltipEvent> getGatherTooltipEvent() {
        return FabricEventEntryPoint.create(ItemTooltipCallback.EVENT, handler -> handler::handle);
    }

    @Override
    public IEventEntryPoint<IGatherTooltipComponentsEvent> getGatherTooltipComponentsEvent() {
        return FabricEventEntryPoint.create(GATHER_TOOLTIP_COMPONENTS, Function.identity());
    }
}
