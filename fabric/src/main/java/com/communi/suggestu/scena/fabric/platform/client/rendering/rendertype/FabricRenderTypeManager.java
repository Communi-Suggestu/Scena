package com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class FabricRenderTypeManager implements IRenderTypeManager
{
    private static final FabricRenderTypeManager INSTANCE = new FabricRenderTypeManager();

    public static FabricRenderTypeManager getInstance()
    {
        return INSTANCE;
    }

    @Override
    public boolean canRenderInType(final BlockState blockState, final ChunkSectionLayer renderType)
    {
        return ItemBlockRenderTypes.getChunkRenderType(blockState) == renderType;
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final ChunkSectionLayer renderType)
    {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    private FabricRenderTypeManager()
    {
    }

    @Override
    public void registerBlockFallbackRenderTypes(final Consumer<IFallbackBlockRenderTypeRegistrar> consumer)
    {
        consumer.accept(BlockRenderLayerMap::putBlock);
    }

    @Override
    public @NotNull Collection<ChunkSectionLayer> getRenderTypesFor(
        final BlockStateModel model,
        final BlockAndTintGetter blockAndTintGetter,
        final BlockPos position,
        final BlockState state,
        final RandomSource rand)
    {
        return List.of();
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final ItemModel model, final ItemStack stack, final boolean isFabulous)
    {
        return List.of(ItemBlockRenderTypes.getRenderType(stack));
    }

}
