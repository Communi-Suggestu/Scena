package com.communi.suggestu.scena.forge.platform.registry.registrar.delegates;

import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ForgeDeferredRegisterPlatformDelegate<T> implements IRegistrar<T>
{

    private final DeferredRegister<T> delegate;

    public ForgeDeferredRegisterPlatformDelegate(final DeferredRegister<T> delegate) {this.delegate = delegate;}

    @Override
    public <I extends T> IRegistryObject<I> register(final String name, final Supplier<? extends I> factory)
    {
        return new ForgeRegistryObjectPlatformDelegate<>(
          delegate.register(name, factory)
        );
    }
}
