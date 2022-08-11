package com.communi.suggestu.scena.core.registries.deferred;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface IRegistryObject<T> extends Supplier<T>
{
    /**
     * Directly retrieves the wrapped Registry Object. This value will automatically be updated when the backing registry is updated.
     * Will throw NPE if the value is null, use isPresent to check first. Or use any of the other guarded functions.
     */
    @Override
    @NotNull
    T get();

    ResourceLocation getId();

    /**
     * Streams the content of the registry object if it is available.
     * 
     * @return The stream of the registry object.
     */
    Stream<T> stream();

    /**
     * Return {@code true} if there is a mod object present, otherwise {@code false}.
     *
     * @return {@code true} if there is a mod object present, otherwise {@code false}
     */
    boolean isPresent();

    /**
     * If a mod object is present, invoke the specified consumer with the object,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a mod object is present
     * @throws NullPointerException if mod object is present and {@code consumer} is
     * null
     */
    void ifPresent(Consumer<? super T> consumer);

    /**
     * If a mod object is present, and the mod object matches the given predicate,
     * return an {@code IChiselsAndBitsRegistryObject} describing the value, otherwise return an
     * empty {@code IChiselsAndBitsRegistryObject}.
     *
     * @param predicate a predicate to apply to the mod object, if present
     * @return an {@code IChiselsAndBitsRegistryObject} describing the value of this {@code IChiselsAndBitsRegistryObject}
     * if a mod object is present and the mod object matches the given predicate,
     * otherwise an empty {@code IChiselsAndBitsRegistryObject}
     * @throws NullPointerException if the predicate is null
     */
    IRegistryObject<T> filter(Predicate<? super T> predicate);

    /**
     * If a mod object is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code Optional} describing the
     * result.  Otherwise return an empty {@code Optional}.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper a mapping function to apply to the mod object, if present
     * @return an {@code Optional} describing the result of applying a mapping
     * function to the mod object of this {@code IChiselsAndBitsRegistryObject}, if a mod object is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null
     */
    <U> Optional<U> map(Function<? super T, ? extends U> mapper);

    /**
     * If a value is present, apply the provided {@code Optional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code Optional}.  This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already an {@code Optional},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code Optional}.
     *
     * @param <U> The type parameter to the {@code Optional} returned by
     * @param mapper a mapping function to apply to the mod object, if present
     *           the mapping function
     * @return the result of applying an {@code Optional}-bearing mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null or returns
     * a null result
     */
    <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper);

    /**
     * If a mod object is present, lazily apply the provided mapping function to it,
     * returning a supplier for the transformed result. If this object is empty, or the
     * mapping function returns {@code null}, the supplier will return {@code null}.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper A mapping function to apply to the mod object, if present
     * @return A {@code Supplier} lazily providing the result of applying a mapping
     * function to the mod object of this {@code IChiselsAndBitsRegistryObject}, if a mod object is present,
     * otherwise a supplier returning {@code null}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    <U> Supplier<U> lazyMap(Function<? super T, ? extends U> mapper);

    /**
     * Return the mod object if present, otherwise return {@code other}.
     *
     * @param other the mod object to be returned if there is no mod object present, may
     * be null
     * @return the mod object, if present, otherwise {@code other}
     */
    T orElse(T other);

    /**
     * Return the mod object if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no mod object
     * is present
     * @return the mod object if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if mod object is not present and {@code other} is
     * null
     */
    T orElseGet(Supplier<? extends T> other);

    /**
     * Return the contained mod object, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     * be thrown
     * @return the present mod object
     * @throws X if there is no mod object present
     * @throws NullPointerException if no mod object is present and
     * {@code exceptionSupplier} is null
     */
    <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;
}
