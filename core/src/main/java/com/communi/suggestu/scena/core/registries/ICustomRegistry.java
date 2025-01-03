package com.communi.suggestu.scena.core.registries;

import com.communi.suggestu.scena.core.registries.deferred.IRegistrarManager;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents a registry that can be used to register expandable systems to.
 * @param <T> The type of the element in the registry.
 */
public interface ICustomRegistry<T extends ICustomRegistryEntry>
{

    /**
     * The codec used to serialize and deserialize the registry entries.
     * @return The codec.
     */
    Codec<T> byNameCodec();

    /**
     * The codec used to serialize and deserialize the registry entries to a stream.
     *
     * @return The codec.
     */
    StreamCodec<ByteBuf, T> byNameStreamCodec();

    /**
     * Gives access to all values in the registry.
     * @return The registries values.
     */
    Collection<T> getValues();

    /**
     * Gives access to all names stored in the current registry.
     * @return All names as "ids" of the objects stored in the registry.
     */
    Set<ResourceLocation> getNames();

    /**
     * Gives access to the value with the given name in the registry.
     * An empty optional is returned if no object is registered with the given name.
     *
     * @param name The name to lookup.
     * @return An optional with the lookup result, empty if the name is not used by any object in the registry.
     */
    Optional<T> get(final ResourceLocation name);

    /**
     * Gives access to the value with the given name in the registry.
     * An exception is thrown if no object is registered with the given name.
     *
     * @param name The name to lookup.
     * @return The lookup result.
     */
    default T getOrThrow(final ResourceLocation name) {
        return get(name).orElseThrow(() -> new IllegalStateException("Unknown registry name: " + name));
    }

    /**
     * Callback executor for each of the values in the registry.
     *
     * @param consumer The callback to execute.
     */
    void forEach(final Consumer<T> consumer);

    /**
     * Builder specifications for creating new chisels and bits registries.
     *
     * @param <T> The type contained in the registry.
     */
    interface Builder<T extends ICustomRegistryEntry> {

        /**
         * Creates a new registry builder for the given registry type.
         *
         * @param <T> The type contained in the registry.
         * @return The registry builder.
         */
        static <T extends ICustomRegistryEntry> Builder<T> simple()
        {
            return IRegistrarManager.getInstance().simpleBuilderFor();
        }

        /**
         * Creates a new registry from the current specification.
         *
         * @return The new registry.
         */
        ICustomRegistry<T> build();
    }
}
