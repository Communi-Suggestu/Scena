package com.communi.suggestu.scena.forge.platform.registry.registrar.delegates;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked"})
public class ForgeRegistryObjectPlatformDelegate<R, T extends R> implements IRegistryObject<T>
{

    private final ResourceLocation id;
    private final Supplier<Optional<T>> delegate;

    public ForgeRegistryObjectPlatformDelegate(ResourceLocation id, final Supplier<Optional<T>> delegate) {
        this.id = id;
        this.delegate = delegate;}

    @Override
    public @NotNull T get()
    {
        return delegate.get().orElseThrow();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public Stream<T> stream()
    {
        return Stream.of(get());
    }

    @Override
    public boolean isPresent()
    {
        return delegate.get().isPresent();
    }

    @Override
    public void ifPresent(final Consumer<? super T> consumer)
    {
        delegate.get().ifPresent((Consumer<Object>) o -> consumer.accept((T) o));
    }

    @Override
    public IRegistryObject<T> filter(final Predicate<? super T> predicate)
    {
        return new ForgeRegistryObjectPlatformDelegate<>(
                id,
                () -> delegate.get().filter((Predicate<Object>) o -> predicate.test((T) o)));
    }

    @Override
    public <U> Optional<U> map(final Function<? super T, ? extends U> mapper)
    {
        return delegate.get().map((Function<Object, U>) o -> mapper.apply((T) o));
    }

    @Override
    public <U> Optional<U> flatMap(final Function<? super T, Optional<U>> mapper)
    {
        return delegate.get().flatMap((Function<Object, Optional<U>>) o -> mapper.apply((T) o));
    }

    @Override
    public <U> Supplier<U> lazyMap(final Function<? super T, ? extends U> mapper)
    {
        return () -> mapper.apply(get());
    }

    @Override
    public T orElse(final T other)
    {
        return (T) delegate.get().orElse((T) other);
    }

    @Override
    public T orElseGet(final Supplier<? extends T> other)
    {
        return (T) delegate.get().orElseGet(other);
    }

    @Override
    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X
    {
        try
        {
            return (T) delegate.get().orElseThrow(exceptionSupplier);
        }
        catch (Throwable e)
        {
            throw exceptionSupplier.get();
        }
    }
}
