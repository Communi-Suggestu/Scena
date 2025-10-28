package com.communi.suggestu.scena.core.client.models;

import com.communi.suggestu.scena.core.client.models.processing.ModelQuadLayer;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The model manager of the platform.
 */
public interface IModelManager
{

    /**
     * The instance of the model manager for the current platform.
     *
     * @return The model manager.
     */
    static IModelManager getInstance() {
        return IRenderingManager.getInstance().getModelManager();
    }

    /**
     * Registers a new callback for item model property registration.
     *
     * @param callback The callback.
     */
    void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback);

    /**
     * Retrieves the particle texture for the block state in the given position.
     *
     * @param blockState The block state in question.
     * @param blockEntitySupplier The supplier that potentially creates the block entity for the block state to get the model for.
     * @param blockAndTintGetter The block and tint getter in which the block state is virtually placed.
     * @param pos The position on which the block state is virtually placed.
     *
     * @return The particle texture.
     */
    TextureAtlasSprite getParticleTexture(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos
    );

    /**
     * Determine the model cache for the {@link net.minecraft.client.renderer.block.model.BlockStateModel}
     * of a block state in the given position.
     *
     * @param blockState The block state in question.
     * @param blockEntitySupplier The supplier that potentially creates the block entity for the block state to get the model for.
     * @param blockAndTintGetter The block and tint getter in which the block state is virtually placed.
     * @param pos The position on which the block state is virtually placed.
     * @return The model cache key component of the model in that position.
     */
    @Nullable
    Object determineModelCacheKey(final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos);

    /**
     * Provides the ability to extract quad information of a {@link net.minecraft.client.renderer.block.model.BlockStateModel}
     *
     * @param blockState The block state to get the quad information of.
     * @param blockEntitySupplier The supplier that potentially creates the block entity for the block state to get the model for.
     * @param cullDirection The cull direction to get the quads for.
     * @param blockAndTintGetter The block and tint getter in which the block state is virtually placed.
     * @param pos The position on which the block state is virtually placed.
     * @param pipeline The pipeline head into which the quad data is pumped.
     */
    void extractQuads(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable Direction cullDirection,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos,
        final Consumer<ModelQuadLayer> pipeline);

    interface IItemModelPropertyRegistrar {
        /**
         * Register a new item model property to this registrar.
         *
         * @param name The name of the property.
         * @param property The function to get the property value.
         */
        void registerRangeProperty(@NotNull final ResourceLocation name, @NotNull final MapCodec<? extends RangeSelectItemModelProperty> property);

        /**
         * Register a new item model property to this registrar.
         *
         * @param name The name of the property.
         * @param property The function to get the property value.
         */
        void registerConditionalProperty(@NotNull final ResourceLocation name, @NotNull final MapCodec<? extends ConditionalItemModelProperty> property);
    }
}
