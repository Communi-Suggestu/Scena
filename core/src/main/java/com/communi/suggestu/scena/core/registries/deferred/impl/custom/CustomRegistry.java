package com.communi.suggestu.scena.core.registries.deferred.impl.custom;

import com.communi.suggestu.scena.core.registries.ICustomRegistry;
import com.communi.suggestu.scena.core.registries.ICustomRegistryEntry;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class CustomRegistry<T extends ICustomRegistryEntry> implements ICustomRegistry<T> {
    private final BiMap<Identifier, T> registerMap = HashBiMap.create();

    @Override
    public Codec<T> byNameCodec() {
        return Identifier.CODEC.comapFlatMap(resourceLocation -> {
                    final Optional<T> optionalEntry = get(resourceLocation);
                    return optionalEntry.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown registry name: " + resourceLocation));
                },
                ICustomRegistryEntry::getRegistryName);
    }

    @Override
    public StreamCodec<ByteBuf, T> byNameStreamCodec() {
        return Identifier.STREAM_CODEC.map(
                this::getOrThrow,
                ICustomRegistryEntry::getRegistryName
        );
    }

    @Override
    public Collection<T> getValues() {
        synchronized (registerMap) {
            return registerMap.values();
        }
    }

    @Override
    public Set<Identifier> getNames() {
        synchronized (registerMap) {
            return registerMap.keySet();
        }
    }

    @Override
    public Optional<T> get(final Identifier name) {
        synchronized (registerMap) {
            return Optional.ofNullable(registerMap.get(name));
        }
    }

    @Override
    public void forEach(final Consumer<T> consumer) {
        synchronized (registerMap) {
            registerMap.values().forEach(consumer);
        }
    }

    public void register(final T value) {
        synchronized (registerMap) {
            registerMap.put(value.getRegistryName(), value);
        }
    }

    public static final class Builder<E extends ICustomRegistryEntry> implements ICustomRegistry.Builder<E> {

        @Override
        public ICustomRegistry<E> build() {
            return new CustomRegistry<>();
        }
    }
}

