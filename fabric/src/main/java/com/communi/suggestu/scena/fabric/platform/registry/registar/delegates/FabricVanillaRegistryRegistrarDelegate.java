package com.communi.suggestu.scena.fabric.platform.registry.registar.delegates;

import com.communi.suggestu.scena.core.registries.SimpleCustomRegistryEntry;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class FabricVanillaRegistryRegistrarDelegate<R> implements IRegistrar<R>
{

    public final String modId;
    public final WritableRegistry<R> vanillaRegistry;

    public FabricVanillaRegistryRegistrarDelegate(final String modId, final Registry<R> vanillaRegistry) {
        this.modId = modId;

        if (!(vanillaRegistry instanceof WritableRegistry<R> writableRegistry)) {
            throw new IllegalArgumentException("Registry must be writable!");
        }
        this.vanillaRegistry = writableRegistry;
    }

    @Override
    public <I extends R> IRegistryObject<R, I> register(final String name, final Supplier<? extends I> factory)
    {
        final Identifier id = Identifier.fromNamespaceAndPath(modId, name);
        final I entry = factory.get();
        if (entry instanceof SimpleCustomRegistryEntry<?> registryEntry)
            registryEntry.setRegistryName(id);

        Registry.register(this.vanillaRegistry, id, entry);

        return new FabricVanillaRegistryRegistryObjectDelegate<I, R>(
                id,
                entry,
                ResourceKey.create(this.vanillaRegistry.key(), id)
        );
    }
}
