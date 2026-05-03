package com.communi.suggestu.scena.fabric.platform.client.rendering;

import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockTintsFactory;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
        setter.accept((colorManager, blocks) -> BlockColorRegistry.register(colorManager::collect, blocks));
    }

    @Override
    public void setupItemColors(final Consumer<IItemColorSetter> configurator)
    {
        configurator.accept(ItemTintSources.ID_MAPPER::put);
    }

}
