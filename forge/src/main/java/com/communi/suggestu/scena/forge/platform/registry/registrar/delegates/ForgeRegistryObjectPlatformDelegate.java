package com.communi.suggestu.scena.forge.platform.registry.registrar.delegates;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ForgeRegistryObjectPlatformDelegate<T> implements IRegistryObject<T>
{

    private final RegistryObject<T> delegate;

    public ForgeRegistryObjectPlatformDelegate(final RegistryObject<T> delegate) {this.delegate = delegate;}

    @Override
    public @NotNull T get()
    {
        return (T) delegate.get();
    }

    @Override
    public ResourceLocation getId()
    {
        return delegate.getId();
    }

    @Override
    public Stream<T> stream()
    {
        return (Stream<T>) delegate.stream();
    }

    @Override
    public boolean isPresent()
    {
        return delegate.isPresent();
    }

    @Override
    public void ifPresent(final Consumer<? super T> consumer)
    {
        delegate.ifPresent((Consumer<Object>) o -> consumer.accept((T) o));
    }

    @Override
    public IRegistryObject<T> filter(final Predicate<? super T> predicate)
    {
        return new ForgeRegistryObjectPlatformDelegate<>(
          delegate.filter((Predicate<Object>) o -> predicate.test((T) o))
        );
    }

    @Override
    public <U> Optional<U> map(final Function<? super T, ? extends U> mapper)
    {
        return delegate.map((Function<Object, U>) o -> mapper.apply((T) o));
    }

    @Override
    public <U> Optional<U> flatMap(final Function<? super T, Optional<U>> mapper)
    {
        return delegate.flatMap((Function<Object, Optional<U>>) o -> mapper.apply((T) o));
    }

    @Override
    public <U> Supplier<U> lazyMap(final Function<? super T, ? extends U> mapper)
    {
        return delegate.lazyMap((Function<Object, U>) o -> mapper.apply((T) o));
    }

    @Override
    public T orElse(final T other)
    {
        return (T) delegate.orElse((T) other);
    }

    @Override
    public T orElseGet(final Supplier<? extends T> other)
    {
        return (T) delegate.orElseGet(other);
    }

    @Override
    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X
    {
        try
        {
            return (T) delegate.orElseThrow(exceptionSupplier);
        }
        catch (Throwable e)
        {
            throw exceptionSupplier.get();
        }
    }
}
