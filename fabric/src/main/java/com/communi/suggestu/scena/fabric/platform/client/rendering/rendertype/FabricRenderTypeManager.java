package com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

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
}
