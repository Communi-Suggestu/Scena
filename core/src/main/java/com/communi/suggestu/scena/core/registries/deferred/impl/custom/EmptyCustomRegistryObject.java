package com.communi.suggestu.scena.core.registries.deferred.impl.custom;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EmptyCustomRegistryObject<T> implements IRegistryObject<T>
{
    private final ResourceLocation entryName;

    public EmptyCustomRegistryObject(final ResourceLocation entryName) {
        this.entryName = entryName;
    }

    @Override
    public @NotNull T get()
    {
        throw new IllegalStateException("Empty registry object");
    }

    @Override
    public ResourceLocation getId()
    {
        return entryName;
    }

    @Override
    public Stream<T> stream()
    {
        return Stream.of();
    }

    @Override
    public boolean isPresent()
    {
        return false;
    }

    @Override
    public void ifPresent(final Consumer<? super T> consumer)
    {

    }

    @Override
    public IRegistryObject<T> filter(final Predicate<? super T> predicate)
    {
        return this;
    }

    @Override
    public <U> Optional<U> map(final Function<? super T, ? extends U> mapper)
    {
        return Optional.empty();
    }

    @Override
    public <U> Optional<U> flatMap(final Function<? super T, Optional<U>> mapper)
    {
        return Optional.empty();
    }

    @Override
    public <U> Supplier<U> lazyMap(final Function<? super T, ? extends U> mapper)
    {
        return () -> null;
    }

    @Override
    public T orElse(final T other)
    {
        return other;
    }

    @Override
    public T orElseGet(final Supplier<? extends T> other)
    {
        return other.get();
    }

    @Override
    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X
    {
        throw exceptionSupplier.get();
    }
}
