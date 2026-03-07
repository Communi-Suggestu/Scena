package com.communi.suggestu.scena.forge.platform.client.rendering;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ForgeRenderTypeManager implements IRenderTypeManager
{
    private static final RandomSource       RANDOM   = Util.make(RandomSource.createNewThreadLocalInstance(), (random) -> random.setSeed(42L));
    private static final ForgeRenderTypeManager INSTANCE = new ForgeRenderTypeManager();

    public static ForgeRenderTypeManager getInstance()
    {
        return INSTANCE;
    }

    private ForgeRenderTypeManager()
    {
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canRenderInType(final BlockState blockState, final ChunkSectionLayer renderType)
    {
        var partsList = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(blockState)
            .collectParts(RANDOM);

        for (final BlockModelPart blockModelPart : partsList)
        {
            for (final Direction direction : Direction.values())
            {
                for (final BakedQuad quad : blockModelPart.getQuads(direction))
                {
                    if (quad.spriteInfo().layer() == renderType)
                        return true;
                }
            }

            for (final BakedQuad quad : blockModelPart.getQuads(null))
            {
                if (quad.spriteInfo().layer() == renderType)
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final ChunkSectionLayer renderType)
    {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    @Override
    public @NotNull Collection<ChunkSectionLayer> getRenderTypesFor(
        final BlockAndTintGetter blockAndTintGetter,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final BlockPos position,
        final BlockState state)
    {
        final var model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
        RANDOM.setSeed(state.getSeed(position));
        var parts = model.collectParts(blockAndTintGetter, position, state, RANDOM);
        var layers = EnumSet.noneOf(ChunkSectionLayer.class);

        for (final BlockModelPart part : parts)
        {
            for (final Direction direction : Direction.values())
            {
                for (final BakedQuad quad : part.getQuads(direction))
                {
                    layers.add(quad.spriteInfo().layer());
                }
            }

            for (final BakedQuad quad : part.getQuads(null))
            {
                layers.add(quad.spriteInfo().layer());
            }
        }

        return layers;
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final ItemModel model, final ItemStack stack, final boolean isFabulous)
    {
        var state = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().appendItemLayers(
            state,
            stack,
            ItemDisplayContext.NONE,
            null,
            null,
            0
        );

        var types = new HashSet<RenderType>();
        for (final ItemStackRenderState.LayerRenderState layer : state.layers)
        {
            for (final BakedQuad bakedQuad : layer.prepareQuadList())
            {
                types.add(bakedQuad.spriteInfo().itemRenderType());
            }
        }

        return types;
    }
}
