package com.communi.suggestu.scena.core.client.rendering;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DataAwareBlockStateModel
{
    /**
     * Collects all the data used by the model to
     * {@linkplain #collectParts(BlockAndTintGetter, BlockPos, BlockState, RandomSource, List) produce renderable geometry}.
     * The returned object encapsulates which parts of the world state the model depends on.
     *
     * <p>This allows the geometry produced previously by a model to be reused, provided that the key matches.
     * <b>The key can be used to compare the geometry of different models.</b>
     * If this model forwards to a single other model, it can directly return its geometry key.
     * Otherwise, it should use a custom type specific to the model.
     *
     * <p>The passed in {@code level}, {@code pos} and {@code random} parameters should not be
     * put directly into the returned key. Only the relevant information should be extracted
     * so that the same key is yielded as often as possible.
     *
     * <p>The default implementation returns {@code null}, meaning the model does not implement this method.
     * A model that wishes to override this method must therefore not return null,
     * even if the model does not use any passed in state.
     *
     * @return an object collecting all the data that influences the geometry of this model;
     *         can be any object as long as it implements {@link Object#hashCode()} and {@link Object#equals(Object)} correctly.
     */
    @Nullable
    Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random);

    /**
     * Collects the parts of the model that should be rendered.
     *
     * @param level  a level to query block entity data or other world state
     * @param pos    the position of the block being rendered
     * @param state  the state of the block being rendered
     * @param random a random source for random model variations
     * @param parts  the list that should receive all parts to be rendered
     */
    void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts);

    /**
     * Returns the particle icon.
     */
    TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state);
}
