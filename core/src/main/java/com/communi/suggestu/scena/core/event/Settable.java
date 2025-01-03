package com.communi.suggestu.scena.core.event;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A settable value.
 * <p>
 *     Used when event properties are modifiable by the event handler.
 *
 * @param <T> the type of the value
 */
public class Settable<T> {

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    /**
     * Creates a new settable value.
     *
     * @param getter the getter
     * @param setter the setter
     */
    public Settable(Supplier<T> getter, Consumer<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public T get() {
        return getter.get();
    }

    /**
     * Sets the value.
     *
     * @param value the value
     */
    public void set(T value) {
        setter.accept(value);
    }

    public static final class Fixed<T> extends Settable<T> {
        private T value;

        public Fixed(T value) {
            super(null, null);
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void set(T value) {
            this.value = value;
        }
    }
}
