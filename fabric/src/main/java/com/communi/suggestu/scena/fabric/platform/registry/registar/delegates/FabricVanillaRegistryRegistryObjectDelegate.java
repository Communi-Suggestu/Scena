package com.communi.suggestu.scena.fabric.platform.registry.registar.delegates;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FabricVanillaRegistryRegistryObjectDelegate<I extends R, R> implements IRegistryObject<R, I>
{
    private final Identifier id;
    private final I          value;
    private final ResourceKey<R> key;

    public FabricVanillaRegistryRegistryObjectDelegate(final Identifier id, final I value, ResourceKey<R> key) {
        this.id = id;
        this.value = value;
        this.key = key;
    }

    @Override
    public @NotNull I get()
    {
        return value;
    }

    @Override
    public ResourceKey<R> key() {
        return this.key;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public Stream<R> stream()
    {
        return Stream.of(value);
    }

    @Override
    public boolean isPresent()
    {
        return true;
    }

    @Override
    public void ifPresent(final Consumer<? super I> consumer)
    {
        consumer.accept(value);
    }

    @Override
    public IRegistryObject<R, I> filter(final Predicate<? super I> predicate)
    {
        if (predicate.test(value))
            return this;

        return Empty.empty();
    }

    @Override
    public <U> Optional<U> map(final Function<? super I, ? extends U> mapper)
    {
        return Optional.of(value).map(mapper);
    }

    @Override
    public <U> Optional<U> flatMap(final Function<? super I, Optional<U>> mapper)
    {
        return Optional.of(value).flatMap(mapper);
    }

    @Override
    public <U> Supplier<U> lazyMap(final Function<? super I, ? extends U> mapper)
    {
        return () -> map(mapper).orElse(null);
    }

    @Override
    public I orElse(final I other)
    {
        return value;
    }

    @Override
    public I orElseGet(final Supplier<? extends I> other)
    {
        return value;
    }

    @Override
    public <X extends Throwable> I orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X
    {
        return value;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class Empty<I extends R, R> implements IRegistryObject<R, I> {

        private static final Empty INSTANCE = new Empty();

        public static <T extends R, R> Empty<T, R> empty() {
            return INSTANCE;
        }

        @Override
        public @NotNull I get()
        {
            return null;
        }

        @Override
        public ResourceKey<R> key() {
            return null;
        }

        @Override
        public Identifier getId()
        {
            return null;
        }

        @Override
        public Stream<R> stream()
        {
            return Stream.empty();
        }

        @Override
        public boolean isPresent()
        {
            return false;
        }

        @Override
        public void ifPresent(final Consumer<? super I> consumer)
        {

        }

        @Override
        public IRegistryObject<R, I> filter(final Predicate<? super I> predicate)
        {
            return this;
        }

        @Override
        public <U> Optional<U> map(final Function<? super I, ? extends U> mapper)
        {
            return Optional.empty();
        }

        @Override
        public <U> Optional<U> flatMap(final Function<? super I, Optional<U>> mapper)
        {
            return Optional.empty();
        }

        @Override
        public <U> Supplier<U> lazyMap(final Function<? super I, ? extends U> mapper)
        {
            return () -> null;
        }

        @Override
        public I orElse(final I other)
        {
            return other;
        }

        @Override
        public I orElseGet(final Supplier<? extends I> other)
        {
            return other.get();
        }

        @Override
        public <X extends Throwable> I orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X
        {
            throw exceptionSupplier.get();
        }
    }
}
