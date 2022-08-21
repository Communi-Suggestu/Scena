package com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

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
    public @NotNull Collection<RenderType> getRenderTypesFor(final BakedModel model, final BlockState state, final RandomSource rand, final IBlockModelData data)
    {
        return Collections.singletonList(ItemBlockRenderTypes.getChunkRenderType(state));
    }
}
