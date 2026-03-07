package com.communi.suggestu.scena.core.client.rendering.type;

import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.utils.RenderTypeUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Manager for handling the different render types which are available to
 * fluids, items and blocks.
 */
public interface IRenderTypeManager
{

    /**
     * The current render type manager.
     * Deals with the render types which are available on different platforms.
     *
     * @return The render type manager.
     */
    static IRenderTypeManager getInstance() {
        return IRenderingManager.getInstance().getRenderTypeManager();
    }

    /**
     * Indicates if the blockstate needs to be rendered in the render type.
     *
     * @param blockState The block state in question.
     * @param renderType The render type.
     * @return True when rendering in the given render type is required, false when not.
     */
    boolean canRenderInType(final BlockState blockState, final ChunkSectionLayer renderType);

    /**
     * Indicates if the fluidState needs to be rendered in the render type.
     *
     * @param fluidState The fluid state in question.
     * @param renderType The render type.
     * @return True when rendering in the given render type is required, false when not.
     */
    boolean canRenderInType(final FluidState fluidState, final ChunkSectionLayer renderType);

    /**
     * Retrieves the {@linkplain RenderType render types} for the given block state, data and model.
     *
     * @param blockAndTintGetter The level information
     * @param blockEntitySupplier The supplier of the block entity in the given position
     * @param position           The position to get the layers for.
     * @param state              The block state to get the types for.
     *
     * @return The render types for the given block state, data and model.
     */
    @NotNull
    Collection<ChunkSectionLayer> getRenderTypesFor(
        final BlockAndTintGetter blockAndTintGetter,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final BlockPos position,
        final BlockState state);

    /**
     * Retrieves the {@linkplain RenderType render types} for the given model, itemstack and if we are running in fabulous or not.
     *
     * @param model The model to get the types for.
     * @param stack The stack to get the types for.
     * @param isFabulous True when fabulous is enabled, false when not.
     * @return The render types for the given block state, data and model.
     */
    @NotNull
    Collection<RenderType> getRenderTypesFor(ItemModel model, ItemStack stack, boolean isFabulous);

    /**
     * Provides a {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY} for the given {@link DefaultVertexFormat#BLOCK} format.
     * Mimics the behavior of vanilla's {@link ItemBlockRenderTypes#getRenderType(BlockState)}.
     */
    default RenderType getEntityRenderType(ChunkSectionLayer chunkSectionLayer) {
        if (chunkSectionLayer != ChunkSectionLayer.TRANSLUCENT)
            return Sheets.cutoutBlockSheet();
        return Sheets.translucentItemSheet();
    }

    /**
     * Provides a {@link RenderType} fit for rendering moving blocks given the specified chunk render type.
     * Mimics the behavior of vanilla's {@link ItemBlockRenderTypes#getMovingBlockRenderType(BlockState)}.
     */
    default RenderType getMovingBlockRenderType(ChunkSectionLayer chunkSectionLayer) {
        return RenderTypeUtils.renderTypeFor(chunkSectionLayer);
    }

}
