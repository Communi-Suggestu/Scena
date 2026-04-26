package com.communi.suggestu.scena.fabric.platform.client.rendering;

import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.item.ItemTintSources;
import org.apache.commons.lang3.NotImplementedException;

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
        configurator.accept(BlockColorRegistry::register);
    }

    @Override
    public void setupDynamicBlockColors(final Consumer<IDynamicBlockColorSetter> setter)
    {
        throw new NotImplementedException("This feature is currently not supported on Fabric");
    }

    @Override
    public void setupItemColors(final Consumer<IItemColorSetter> configurator)
    {
        configurator.accept(ItemTintSources.ID_MAPPER::put);
    }

}
