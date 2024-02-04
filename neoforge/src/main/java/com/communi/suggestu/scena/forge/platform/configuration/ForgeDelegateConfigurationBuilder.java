package com.communi.suggestu.scena.forge.platform.configuration;

import com.communi.suggestu.scena.core.config.IConfigurationBuilder;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.communi.suggestu.scena.forge.utils.LanguageHandler;
import net.minecraft.locale.Language;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ForgeDelegateConfigurationBuilder implements IConfigurationBuilder
{

    private final Consumer<ModConfigSpec> specConsumer;
    private final Consumer<String> keyConsumer;
    private final ModConfigSpec.Builder builder;

    private final Set<String> keys = Sets.newConcurrentHashSet();

    public ForgeDelegateConfigurationBuilder(final Consumer<ModConfigSpec> specConsumer, final Consumer<String> keyConsumer) {
        this.specConsumer = specConsumer;
        this.keyConsumer = keyConsumer;
        builder = new ModConfigSpec.Builder();
    }

    @Override
    public Supplier<Boolean> defineBoolean(final String key, final boolean defaultValue)
    {
        setCommentAndTranslation(key);
        return builder.define(key, defaultValue)::get;
    }

    @Override
    public <T> Supplier<List<? extends T>> defineList(final String key, final List<T> defaultValue, final Class<T> containedType)
    {
        setCommentAndTranslation(key);
        if (containedType != Float.class)
            return builder.defineList(key, defaultValue, t -> true)::get;

        List<Double> defaultValueAsDoubles = defaultValue.stream().map(f -> ((Float) f).doubleValue()).toList();
        ModConfigSpec.ConfigValue<List<? extends Double>> doubleListValue = builder.defineList(key, defaultValueAsDoubles, t -> t instanceof Double);
        return () -> doubleListValue.get().stream().map(d -> containedType.cast(d.floatValue())).toList();
    }

    @Override
    public Supplier<Vector4f> defineVector4f(final String key, final Vector4f defaultValue)
    {
        setCommentAndTranslation(key);
        final ArrayList<Double> defaultValueList = Lists.newArrayList(
                (double) defaultValue.x(),
                (double) defaultValue.y(),
                (double) defaultValue.z(),
                (double) defaultValue.w());
        final ModConfigSpec.ConfigValue<List<? extends Double>> doubleListValue = builder.defineList(key, defaultValueList, t -> t instanceof Double);
        return () -> {
            final List<? extends Double> list = doubleListValue.get();
            return new Vector4f(
                    list.get(0).floatValue(),
                    list.get(1).floatValue(),
                    list.get(2).floatValue(),
                    list.get(3).floatValue());
        };
    }

    @Override
    public Supplier<String> defineString(final String key, final String defaultValue)
    {
        setCommentAndTranslation(key);
        return builder.define(key, defaultValue)::get;
    }

    @Override
    public Supplier<Long> defineLong(final String key, final long defaultValue, final long minValue, final long maxValue)
    {
        setCommentAndTranslation(key);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public Supplier<Integer> defineInteger(final String key, final int defaultValue, final int minValue, final int maxValue)
    {
        setCommentAndTranslation(key);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public Supplier<Double> defineDouble(final String key, final double defaultValue, final double minValue, final double maxValue)
    {
        setCommentAndTranslation(key);
        return builder.defineInRange(key, defaultValue, minValue, maxValue)::get;
    }

    @Override
    public <T extends Enum<T>> Supplier<T> defineEnum(final String key, final T defaultValue)
    {
        setCommentAndTranslation(key);
        return builder.defineEnum(key, defaultValue)::get;
    }

    @Override
    public void setup()
    {
        keys.forEach(keyConsumer);
        specConsumer.accept(builder.build());
    }

    private void setCommentAndTranslation(final String unprocessedKey)
    {
        final String key = String.format("mod.%s.config.%s.comment", Constants.MOD_ID, unprocessedKey);
        keys.add(key);
        builder.comment(translateToLocal(key));
    }

    public static String translateToLocal(
      final String key )
    {
        return DistExecutor.unsafeRunForDist(
          () -> () -> {
              final String translated = Language.getInstance().getOrDefault(key);
              if (translated.equals(key))
                  return LanguageHandler.translateKey(key);

              return translated;
          },
          () -> () -> LanguageHandler.translateKey(key)
        );
    }
}
