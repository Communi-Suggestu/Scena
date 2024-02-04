package com.communi.suggestu.scena.forge.platform.registry.registrar.delegates;

import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ForgeDeferredRegisterPlatformDelegate<T> implements IRegistrar<T>
{

    private final DeferredRegister<T> delegate;

    public ForgeDeferredRegisterPlatformDelegate(final DeferredRegister<T> delegate) {this.delegate = delegate;}

    @Override
    public <I extends T> IRegistryObject<I> register(final String name, final Supplier<? extends I> factory)
    {
        final DeferredHolder<T, I> holder = delegate.register(name, factory);
        return new ForgeRegistryObjectPlatformDelegate<>(
                holder.getId(),
                holder.asOptional()
                );
    }
}
