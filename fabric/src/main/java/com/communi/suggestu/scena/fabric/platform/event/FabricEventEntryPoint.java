package com.communi.suggestu.scena.fabric.platform.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import net.fabricmc.fabric.api.event.Event;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class FabricEventEntryPoint<T extends IEvent, G> implements IEventEntryPoint<T> {

    public static <T extends IEvent, G> FabricEventEntryPoint<T, G> create(Event<G> fabricEvent, Function<T, G> invoker) {
        return new FabricEventEntryPoint<>(fabricEvent, invoker);
    }

    private final Event<G> fabricEvent;

    private final Function<T, G> invoker;

    private FabricEventEntryPoint(Event<G> fabricEvent, Function<T, G> invoker) {
        this.fabricEvent = fabricEvent;
        this.invoker = invoker;
    }

    @Override
    public void register(T handler) {
        fabricEvent.register(invoker.apply(handler));
    }
}
