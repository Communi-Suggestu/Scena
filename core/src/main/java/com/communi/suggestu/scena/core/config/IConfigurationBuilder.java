package com.communi.suggestu.scena.core.config;


import org.joml.Vector4f;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a builder for the current platform.
 */
public interface IConfigurationBuilder
{
    /**
     * Defines a new boolean property with the given key and default value.
     *
     * @param key The key to use.
     * @param defaultValue The default value.
     * @return The value provider.
     */
    Supplier<Boolean> defineBoolean(String key, boolean defaultValue);

    /**
     * Defines a new list property with the given key and default value.
     *
     * @param key The key to use.
     * @param defaultValue The default value.
     * @param containedType The type of the values in the list.
     * @param <T> The type contained in the list.
     * @return The value provider.
     */
    <T> Supplier<List<? extends T>> defineList(String key, List<T> defaultValue, final Class<T> containedType);

    /**
     * Defines a new Vector4f property with the given key and default value.
     *
     * @param key The key to use.
     * @param defaultValue The default value.
     * @return The value provider.
     */
    Supplier<Vector4f> defineVector4f(String key, Vector4f defaultValue);

    /**
     * Defines a new string property with the given key and default value.
     *
     * @param key The key to use.
     * @param defaultValue The default value.
     * @return The value provider.
     */
    Supplier<String> defineString(String key, String defaultValue);

    /**
     * Defines a new long property with the given key and default value.
     *
     * @param key The key to use.
     * @param defaultValue The default value.
     * @param minValue The minimal value.
     * @param maxValue The maximal value.
     * @return The value provider.
     */
    Supplier<Long> defineLong(String key, long defaultValue, long minValue, long maxValue);

    /**
     * Defines a new integer property with the given key and default value.
     *
     * @param key The key to use.
     * @param defaultValue The default value.
     * @param minValue The minimal value.
     * @param maxValue The maximal value.
     * @return The value provider.
     */
    Supplier<Integer> defineInteger(String key, int defaultValue, int minValue, int maxValue);

    /**
     * Defines a new double property with the given key and default value.
     *
     * @param key The key to use.
     * @param defaultValue The default value.
     * @param minValue The minimal value.
     * @param maxValue The maximal value.
     * @return The value provider.
     */
    Supplier<Double> defineDouble(String key, double defaultValue, double minValue, double maxValue);
    
    /**
     * Defines a new enum based property with the given key and default value.
     *
     * @param key The key to use.
     * @param defaultValue The default value.
     * @param <T> The type of the enu,
     * @return The value provider.
     */
    <T extends Enum<T>> Supplier<T> defineEnum(String key, T defaultValue);

    /**
     * Finalizes the builder and sets up the configuration.
     */
    void setup();

}
