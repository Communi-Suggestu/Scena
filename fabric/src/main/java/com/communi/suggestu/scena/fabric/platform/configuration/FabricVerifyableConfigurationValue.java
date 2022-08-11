package com.communi.suggestu.scena.fabric.platform.configuration;

import com.google.gson.JsonElement;

import java.util.function.Function;

public class FabricVerifyableConfigurationValue<T> extends FabricConfigurationValue<T>
{
    private final Function<T, T> verifier;

    public FabricVerifyableConfigurationValue(
      final FabricConfigurationSource source,
      final String key,
      final Function<JsonElement, T> adapter,
      final Function<T, JsonElement> writer,
      final T defaultValue,
      final Function<T, T> verifier)
    {
        super(source, key, adapter, writer, defaultValue);
        this.verifier = verifier;
    }

    @Override
    protected T verify(final T value)
    {
        return verifier.apply(value);
    }
}
