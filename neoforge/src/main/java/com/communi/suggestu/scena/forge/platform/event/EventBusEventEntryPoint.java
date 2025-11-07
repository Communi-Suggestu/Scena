package com.communi.suggestu.scena.forge.platform.event;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.communi.suggestu.scena.core.event.IEvent;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;
import com.communi.suggestu.scena.forge.platform.ForgeScenaPlatform;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.BiConsumer;

public record EventBusEventEntryPoint<T extends IEvent, F extends Event>(
    IEventBus eventBus,
    Class<F> eventClass,
    BiConsumer<F, T> invoker,
    EventPriority priority) implements IEventEntryPoint<T>
{

    public static <Z extends IEvent, Y extends Event> IEventEntryPoint<Z> forge(final Class<Y> eventClass, final BiConsumer<Y, Z> invoker)
    {
        return new EventBusEventEntryPoint<>(NeoForge.EVENT_BUS, eventClass, invoker);
    }

    public static <Z extends IEvent, Y extends Event> IEventEntryPoint<Z> forge(final Class<Y> eventClass, final BiConsumer<Y, Z> invoker, final EventPriority priority)
    {
        return new EventBusEventEntryPoint<>(NeoForge.EVENT_BUS, eventClass, invoker, priority);
    }

    public static <Z extends IEvent, Y extends Event & IModBusEvent> IEventEntryPoint<Z> mod(final Class<Y> eventClass, final BiConsumer<Y, Z> invoker)
    {
        IScenaPlatform platform = IScenaPlatform.getInstance();
        if (!(platform instanceof ForgeScenaPlatform forgeScenaPlatform))
        {
            throw new IllegalStateException("Platform not initialized");
        }

        return new EventBusEventEntryPoint<>(forgeScenaPlatform.getModBus(), eventClass, invoker);
    }

    public EventBusEventEntryPoint(IEventBus eventBus, Class<F> eventClass, BiConsumer<F, T> invoker)
    {
        this(eventBus, eventClass, invoker, EventPriority.NORMAL);
    }

    @Override
    public void register(T handler)
    {
        eventBus.addListener(this.priority, false, eventClass, event -> invoker.accept(event, handler));
    }
}
