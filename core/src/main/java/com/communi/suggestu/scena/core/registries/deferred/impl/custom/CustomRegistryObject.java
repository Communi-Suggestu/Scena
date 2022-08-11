package com.communi.suggestu.scena.core.registries.deferred.impl.custom;

import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistry;
import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistryEntry;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CustomRegistryObject<T extends R, R extends IChiselsAndBitsRegistryEntry> implements IRegistryObject<T>
{

    private final ResourceLocation entryName;
    private final Supplier<IChiselsAndBitsRegistry<R>> registry;

    public CustomRegistryObject(final ResourceLocation name, final Supplier<IChiselsAndBitsRegistry<R>> registry)
    {
        this.entryName = name;
        this.registry = registry;
    }

    private CustomRegistryObject(final T entry, Supplier<IChiselsAndBitsRegistry<R>> registry)
    {
        this.entryName = entry.getRegistryName();
        this.registry = registry;
    }

    @Override
    public @NotNull T get()
    {
        return getEntry().orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private Optional<T> getEntry()
    {
        return (Optional<T>) registry.get().get(entryName);
    }

    @Override
    public ResourceLocation getId()
    {
        return entryName;
    }

    @Override
    public Stream<T> stream()
    {
        return getEntry().stream();
    }

    @Override
    public boolean isPresent()
    {
        return getEntry().isPresent();
    }

    @Override
    public void ifPresent(final Consumer<? super T> consumer)
    {
        getEntry().ifPresent(consumer);
    }

    @Override
    public IRegistryObject<T> filter(final Predicate<? super T> predicate)
    {
        return getEntry()
                 .filter(predicate)
                 .map(entry -> new CustomRegistryObject<>(entry, registry))
                 .orElseGet(new EmptyCustomRegistryObject<>(entryName));
    }

    @Override
    public <U> Optional<U> map(final Function<? super T, ? extends U> mapper)
    {
        return getEntry().map(mapper);
    }

    @Override
    public <U> Optional<U> flatMap(final Function<? super T, Optional<U>> mapper)
    {
        return getEntry().flatMap(mapper);
    }

    @Override
    public <U> Supplier<U> lazyMap(final Function<? super T, ? extends U> mapper)
    {
        return () -> getEntry().map(mapper).orElse(null);
    }

    @Override
    public T orElse(final T other)
    {
        return getEntry().orElse(other);
    }

    @Override
    public T orElseGet(final Supplier<? extends T> other)
    {
        return getEntry().orElseGet(other);
    }

    @Override
    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X
    {
        return getEntry().orElseThrow(exceptionSupplier);
    }
}
