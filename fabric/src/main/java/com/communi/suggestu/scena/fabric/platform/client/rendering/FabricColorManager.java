package com.communi.suggestu.scena.fabric.platform.client.rendering;

import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public final class FabricColorManager implements IColorManager
{
    private static final FabricColorManager INSTANCE = new FabricColorManager();

    public static FabricColorManager getInstance()
    {
        return INSTANCE;
    }

    private FabricColorManager()
    {
    }

    @Override
    public void setupBlockColors(final Consumer<IBlockColorSetter> configurator)
    {
        final IBlockColorSetter setter = ColorProviderRegistry.BLOCK::register;

        configurator.accept(setter);
    }

    @Override
    public void setupItemColors(final Consumer<IItemColorSetter> configurator)
    {
        configurator.accept(ItemTintSources.ID_MAPPER::put);
    }

}
