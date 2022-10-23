package com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype;

import com.communi.suggestu.scena.core.client.models.baked.IDataAwareBakedModel;
import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class FabricRenderTypeManager implements IRenderTypeManager
{
    private static final FabricRenderTypeManager INSTANCE = new FabricRenderTypeManager();

    public static FabricRenderTypeManager getInstance()
    {
        return INSTANCE;
    }

    private FabricRenderTypeManager()
    {
    }

    @Override
    public boolean canRenderInType(final BlockState blockState, final RenderType renderType)
    {
        return ItemBlockRenderTypes.getChunkRenderType(blockState) == renderType;
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final RenderType renderType)
    {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    @Override
    public void registerBlockFallbackRenderTypes(final Consumer<IFallbackBlockRenderTypeRegistrar> consumer)
    {
        consumer.accept(BlockRenderLayerMap.INSTANCE::putBlock);
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final BakedModel model, final BlockState state, final RandomSource rand, final IBlockModelData data)
    {
        return Collections.singletonList(ItemBlockRenderTypes.getChunkRenderType(state));
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(BakedModel model, ItemStack stack, boolean isFabulous) {
        if (stack.getItem() instanceof BlockItem blockItem)
        {
            final Collection<RenderType> renderTypes;
            if (model instanceof IDataAwareBakedModel dataAwareBakedModel) {
                renderTypes = dataAwareBakedModel.getSupportedRenderTypes(blockItem.getBlock().defaultBlockState(), RandomSource.create(42), IBlockModelData.empty());
            } else {
                renderTypes = getRenderTypesFor(model, blockItem.getBlock().defaultBlockState(), RandomSource.create(42), IBlockModelData.empty());
            }
            if (renderTypes.contains(RenderType.translucent())) {
                return Collections.singletonList(isFabulous || !Minecraft.useShaderTransparency() ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet());
            }
            return Collections.singletonList(Sheets.cutoutBlockSheet());
        }
        return Collections.singletonList(isFabulous ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet());
    }
}
