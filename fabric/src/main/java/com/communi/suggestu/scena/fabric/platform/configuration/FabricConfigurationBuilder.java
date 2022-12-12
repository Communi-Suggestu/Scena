package com.communi.suggestu.scena.fabric.platform.configuration;

import com.communi.suggestu.scena.core.config.IConfigurationBuilder;
import com.google.common.collect.Lists;
import com.google.gson.*;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FabricConfigurationBuilder implements IConfigurationBuilder
{

    private static final Gson GSON = new GsonBuilder()
                                       .create();

    private final FabricConfigurationSource source;
    private final Consumer<FabricConfigurationSpec> specConsumer;

    private final List<FabricConfigurationValue<?>> configuredValues = Lists.newArrayList();

    public FabricConfigurationBuilder(
      final FabricConfigurationSource source,
      final Consumer<FabricConfigurationSpec> specConsumer) {
        this.source = source;
        this.specConsumer = specConsumer;
    }

    @Override
    public Supplier<Boolean> defineBoolean(final String key, final boolean defaultValue)
    {
        final FabricConfigurationValue<Boolean> value = new FabricConfigurationValue<>(
          source,
          key,
          JsonElement::getAsBoolean,
          JsonPrimitive::new,
          defaultValue
        );

        configuredValues.add(value);

        return value;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(final String key, final List<T> defaultValue, final Class<T> containedType)
    {
        final FabricConfigurationValue<List<? extends T>> value = new FabricConfigurationValue<>(
          source,
          key,
          jsonElement -> {
              if (!jsonElement.isJsonArray())
                  throw new JsonParseException("List: " + key + " is not an array");

              final JsonArray jsonArray = jsonElement.getAsJsonArray();
              final List<T> list = new ArrayList<>();

              for (final JsonElement element : jsonArray) {
                  list.add(GSON.fromJson(element, containedType));
              }

              return list;
          },
          GSON::toJsonTree,
          defaultValue
        );

        configuredValues.add(value);

        return value;
    }

    @Override
    public Supplier<Vector4f> defineVector4f(final String key, final Vector4f defaultValue)
    {
        final FabricConfigurationValue<Vector4f> value = new FabricConfigurationValue<>(
                source,
                key,
                jsonElement -> {
                    if (!jsonElement.isJsonArray()) {
                        if (!jsonElement.isJsonObject()) {
                            throw new JsonParseException("Vector4f: " + key + " is not an array or an object with x,y,z,w as parameters.");
                        }

                        final JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.keySet().size() != 4) {
                            throw new JsonParseException("Vector4f: " + key + " is not an array, or an object with x,y,z,w as parameters.");
                        }
                        if (!jsonObject.has("x") || !jsonObject.has("y") || !jsonObject.has("z") || !jsonObject.has("w")) {
                            throw new JsonParseException("Vector4f: " + key + " is not an array, or an object with x,y,z,w as parameters.");
                        }

                        jsonElement = new JsonArray();
                        ((JsonArray) jsonElement).add(jsonObject.get("x"));
                        ((JsonArray) jsonElement).add(jsonObject.get("y"));
                        ((JsonArray) jsonElement).add(jsonObject.get("z"));
                        ((JsonArray) jsonElement).add(jsonObject.get("w"));
                    }

                    final JsonArray jsonArray = jsonElement.getAsJsonArray();
                    final List<Float> list = new ArrayList<>();

                    for (final JsonElement element : jsonArray) {
                        list.add(GSON.fromJson(element, Float.class));
                    }

                    if (list.size() != 3 && list.size() != 4)
                        throw new JsonParseException("Vector4f: element count is " + list.size() + ", but must be 3 or 4");

                    if (list.size() == 3)
                        list.add(1f);

                    return new Vector4f(list.get(0), list.get(1), list.get(2), list.get(3));
                },
                GSON::toJsonTree,
                defaultValue
        );

        configuredValues.add(value);

        return value;
    }

    @Override
    public Supplier<String> defineString(final String key, final String defaultValue)
    {
        final FabricConfigurationValue<String> value = new FabricConfigurationValue<>(
          source,
          key,
          JsonElement::getAsString,
          JsonPrimitive::new,
          defaultValue
        );

        configuredValues.add(value);

        return value;
    }

    @Override
    public Supplier<Long> defineLong(final String key, final long defaultValue, final long minValue, final long maxValue)
    {
        final FabricConfigurationValue<Long> value = new FabricVerifyableConfigurationValue<>(
          source,
          key,
          JsonElement::getAsLong,
          JsonPrimitive::new,
          defaultValue,
          val -> Math.max(minValue, Math.min(maxValue, val))
        );

        configuredValues.add(value);

        return value;
    }

    @Override
    public Supplier<Integer> defineInteger(final String key, final int defaultValue, final int minValue, final int maxValue)
    {
        final FabricConfigurationValue<Integer> value = new FabricVerifyableConfigurationValue<>(
          source,
          key,
          JsonElement::getAsInt,
          JsonPrimitive::new,
          defaultValue,
          val -> Math.max(minValue, Math.min(maxValue, val))
        );

        configuredValues.add(value);

        return value;
    }

    @Override
    public Supplier<Double> defineDouble(final String key, final double defaultValue, final double minValue, final double maxValue)
    {
        final FabricConfigurationValue<Double> value = new FabricVerifyableConfigurationValue<>(
          source,
          key,
          JsonElement::getAsDouble,
          JsonPrimitive::new,
          defaultValue,
          val -> Math.max(minValue, Math.min(maxValue, val))
        );

        configuredValues.add(value);

        return value;
    }

    @Override
    public <T extends Enum<T>> Supplier<T> defineEnum(final String key, final T defaultValue)
    {
        final FabricConfigurationValue<T> value = new FabricConfigurationValue<>(
          source,
          key,
          jsonElement -> {
              if (!jsonElement.isJsonPrimitive())
                  throw new JsonParseException("Enum: " + key + " is not a primitive");

              final JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
              if (!primitive.isString() && !primitive.isNumber())
                  throw new JsonParseException("Enum: " + key + " is not a string or number");

              final T[] values = defaultValue.getDeclaringClass().getEnumConstants();
              if (primitive.isString()) {
                  final String val = primitive.getAsString();

                  if (Arrays.stream(values).noneMatch(v -> v.name().equals(val)))
                      throw new JsonParseException("Enum: " + key + " is not a valid value");

                  return Enum.valueOf(defaultValue.getDeclaringClass(), val);
              }

              if (primitive.isNumber()) {
                  final int val = primitive.getAsInt();
                  if  (val < 0 || val >= values.length)
                      throw new JsonParseException("Enum: " + key + " is not a valid value");

                  return values[val];
              }

              throw new IllegalStateException("Reached unreachable code");
          },
          (enumValue) -> new JsonPrimitive(enumValue.name()),
          defaultValue
        );

        configuredValues.add(value);

        return value;
    }

    @Override
    public void setup()
    {
        final FabricConfigurationSpec spec = new FabricConfigurationSpec(
          source,
          configuredValues
        );

        specConsumer.accept(spec);
    }
}
