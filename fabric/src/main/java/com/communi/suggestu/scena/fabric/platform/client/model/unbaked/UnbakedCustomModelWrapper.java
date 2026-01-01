package com.communi.suggestu.scena.fabric.platform.client.model.unbaked;

import com.communi.suggestu.scena.core.client.rendering.DataAwareBlockStateModel;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public record UnbakedCustomModelWrapper<T extends BlockStateModel.Unbaked>(
    MapCodec<T> mapCodec,
    @Nullable
    T model
) implements CustomUnbakedBlockStateModel
{
    public UnbakedCustomModelWrapper(final MapCodec<T> mapCodec)
    {
        this(mapCodec, null);
    }

    @Override
    public @NotNull MapCodec<UnbakedCustomModelWrapper<?>> codec()
    {
        return mapCodec().xmap(
            unbaked -> new UnbakedCustomModelWrapper<>(mapCodec(), unbaked),
            customUnbakedBlockStateModel -> model()
        );
    }

    @Override
    public @NotNull BlockStateModel bake(final @NotNull ModelBaker baker)
    {
        if (model() == null)
            throw new IllegalStateException("Data registration mode can not be used for baking!");

        return new DataAware(model().bake(baker));
    }

    @Override
    public void resolveDependencies(final @NotNull ResolvableModel.Resolver resolver)
    {
        if (model() == null)
            throw new IllegalStateException("Data registration mode can not be used for baking!");

        model().resolveDependencies(resolver);
    }

    public record DataAware(BlockStateModel inner) implements BlockStateModel {

        @Override
        public void emitQuads(
            final @NotNull QuadEmitter emitter,
            final @NotNull BlockAndTintGetter blockView,
            final @NotNull BlockPos pos,
            final @NotNull BlockState state,
            final @NotNull RandomSource random,
            final @NotNull Predicate<@Nullable Direction> cullTest)
        {
            final List<BlockModelPart> parts = new ArrayList<>();
            if (inner() instanceof DataAwareBlockStateModel dataAwareBlockStateModel) {
                dataAwareBlockStateModel.collectParts(
                    blockView,
                    pos,
                    state,
                    random,
                    parts
                );
            } else {
                collectParts(random, parts);
            }
            for (BlockModelPart part : parts)
            {
                part.emitQuads(emitter, cullTest);
            }
        }

        @Override
        public void collectParts(final @NotNull RandomSource random, final @NotNull List<BlockModelPart> output)
        {
            inner().collectParts(random, output);
        }

        @Override
        public @Nullable Object createGeometryKey(
            final @NotNull BlockAndTintGetter blockView,
            final @NotNull BlockPos pos,
            final @NotNull BlockState state,
            final @NotNull RandomSource random)
        {
            if (inner() instanceof DataAwareBlockStateModel dataAwareBlockStateModel) {
                return dataAwareBlockStateModel.createGeometryKey(
                    blockView, pos, state, random
                );
            }
            return BlockStateModel.super.createGeometryKey(blockView, pos, state, random);
        }

        @Override
        public @NotNull TextureAtlasSprite particleSprite(final @NotNull BlockAndTintGetter blockView, final @NotNull BlockPos pos, final @NotNull BlockState state)
        {
            if (inner() instanceof DataAwareBlockStateModel dataAwareBlockStateModel) {
                return dataAwareBlockStateModel.particleIcon(
                    blockView, pos, state
                );
            }
            return BlockStateModel.super.particleSprite(blockView, pos, state);
        }

        @Override
        public @NotNull TextureAtlasSprite particleIcon()
        {
            return inner().particleIcon();
        }
    }
}
