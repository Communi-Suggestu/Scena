package com.communi.suggestu.scena.fabric.platform.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.communi.suggestu.scena.core.event.IEventEntryPoint;

public final class FabricMultiEventEntryPoint<T extends IEvent> implements IEventEntryPoint<T> {

    @SafeVarargs
    public static <T extends IEvent> FabricMultiEventEntryPoint<T> create(IEventEntryPoint<T>... entryPoints) {
        return new FabricMultiEventEntryPoint<T>(entryPoints);
    }

    private final IEventEntryPoint<T>[] entryPoints;

    private FabricMultiEventEntryPoint(IEventEntryPoint<T>[] entryPoints) {
        this.entryPoints = entryPoints;
    }

    @Override
    public void register(T handler) {
        for (IEventEntryPoint<T> entryPoint : entryPoints) {
            entryPoint.register(handler);
        }
    }
}
