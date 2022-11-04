package com.communi.suggestu.scena.forge.platform.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class EventBusEventEntryPoint<T extends IEvent, F extends Event> implements IEventEntryPoint<T> {

    public static <Z extends IEvent, Y extends Event> IEventEntryPoint<Z> forge(final Class<Y> eventClass, final BiConsumer<Y, Z> invoker) {
        return new EventBusEventEntryPoint<>(Mod.EventBusSubscriber.Bus.FORGE.bus().get(), eventClass, invoker);
    }

    public static <Z extends IEvent, Y extends Event> IEventEntryPoint<Z> mod(final Class<Y> eventClass, final BiConsumer<Y, Z> invoker) {
        return new EventBusEventEntryPoint<>(Mod.EventBusSubscriber.Bus.MOD.bus().get(), eventClass, invoker);
    }

    private final IEventBus eventBus;
    private final Class<F> eventClass;
    private final BiConsumer<F, T> invoker;

    public EventBusEventEntryPoint(IEventBus eventBus, Class<F> eventClass, BiConsumer<F, T> invoker) {
        this.eventBus = eventBus;
        this.eventClass = eventClass;
        this.invoker = invoker;
    }

    @Override
    public void register(T handler) {
        eventBus.addListener(EventPriority.NORMAL, false, eventClass, event -> invoker.accept(event, handler));
    }
}
