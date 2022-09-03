package com.communi.suggestu.scena.core.client.models.baked;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface IDataAwareBakedModel extends BakedModel {

    /**
     * Retrieves the models quads for the given data.
     *
     * @param state The block state.
     * @param side The side of the model.
     * @param rand The random source.
     * @param extraData The data to use.
     * @param renderType The render type to render in, might be null if unknown.
     * @return A list of quads for the given data.
     */
    @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IBlockModelData extraData, @Nullable RenderType renderType);

    /**
     * The render types of the model.
     *
     * @param state The block state of the model.
     * @param rand The random source.
     * @param data The data to use.
     * @return The render types of the model.
     */
    @NotNull Collection<RenderType> getSupportedRenderTypes(final BlockState state, final RandomSource rand, final IBlockModelData data);

    /**
     * The render types of the model when it is rendered as an item.
     *
     * @param stack The stack of the model.
     * @param fabulous If the rendering is happening in fabulous or not.
     * @return The render types of the model.
     */
    @NotNull Collection<RenderType> getSupportedRenderTypes(final ItemStack stack, final boolean fabulous);
}
