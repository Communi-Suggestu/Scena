package com.communi.suggestu.scena.forge.platform.client.rendering;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.forge.platform.client.model.data.ForgeBlockModelDataPlatformDelegate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ForgeRenderTypeManager implements IRenderTypeManager
{
    private static final RandomSource RANDOM_SOURCE = new LegacyRandomSource(0);
    private static final ForgeRenderTypeManager INSTANCE = new ForgeRenderTypeManager();

    public static ForgeRenderTypeManager getInstance()
    {
        return INSTANCE;
    }

    private ForgeRenderTypeManager()
    {
    }

    @Override
    public boolean canRenderInType(final BlockState blockState, final RenderType renderType)
    {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState).getRenderTypes(
                blockState,
                RANDOM_SOURCE,
                ModelData.EMPTY
        ).contains(renderType);
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final RenderType renderType)
    {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final BakedModel model, final BlockState state, final RandomSource rand, final IBlockModelData data)
    {
        if (!(data instanceof ForgeBlockModelDataPlatformDelegate delegate))
        {
            throw new IllegalArgumentException("data must be an instance of ForgeBlockModelData");
        }

        return model.getRenderTypes(state, rand, delegate.getDelegate()).asList();
    }

}
