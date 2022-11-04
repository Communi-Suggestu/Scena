package com.communi.suggestu.scena.forge.platform.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class EventBusEventEntryPoint<T extends IEvent, F extends Event> implements IEventEntryPoint<T> {

    public static <Z extends IEvent, Y extends Event> IEventEntryPoint<Z> forge(final BiConsumer<Y, Z> invoker) {
        return new EventBusEventEntryPoint<>(Mod.EventBusSubscriber.Bus.FORGE.bus().get(), invoker);
    }

    public static <Z extends IEvent, Y extends Event> IEventEntryPoint<Z> mod(final BiConsumer<Y, Z> invoker) {
        return new EventBusEventEntryPoint<>(Mod.EventBusSubscriber.Bus.MOD.bus().get(), invoker);
    }

    private final IEventBus eventBus;
    private final BiConsumer<F, T> invoker;

    public EventBusEventEntryPoint(IEventBus eventBus, BiConsumer<F, T> invoker) {
        this.eventBus = eventBus;
        this.invoker = invoker;
    }

    @Override
    public void register(T handler) {
        eventBus.addListener(new Consumer<F>() {
            @Override
            public void accept(F event) {
                invoker.accept(event, handler);
            }
        });
    }
}
