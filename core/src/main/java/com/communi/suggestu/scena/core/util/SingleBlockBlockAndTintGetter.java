package com.communi.suggestu.scena.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SingleBlockBlockAndTintGetter extends SingleBlockBlockReader implements BlockAndTintGetter
{
    @Nullable
    private final BlockAndTintGetter source;

    protected SingleBlockBlockAndTintGetter(
        final BlockState blockState,
        final BlockPos pos,
        final @Nullable BlockAndTintGetter source,
        final @Nullable BlockEntity blockEntity)
    {
        super(blockState, pos, source, blockEntity);
        this.source = source;
    }

    @NotNull
    @Override
    public LevelLightEngine getLightEngine()
    {
        if (this.source == null)
            throw new IllegalStateException("No reader available.");

        return this.source.getLightEngine();
    }

    @Override
    public int getBlockTint(final @NotNull BlockPos blockPos, final @NotNull ColorResolver colorResolver)
    {
        if (this.source == null)
            return -1;

        return this.source.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public CardinalLighting cardinalLighting()
    {
        if (this.source == null)
            return CardinalLighting.DEFAULT;

        return this.source.cardinalLighting();
    }

    public static class Builder
    {
        private           BlockState         blockState;
        private           BlockPos           pos;
        private @Nullable BlockAndTintGetter              source;
        private           Supplier<@Nullable BlockEntity> blockEntityBuilder;

        public Builder withBlockState(final BlockState blockState)
        {
            this.blockState = blockState;
            return this;
        }

        public Builder withPos(final BlockPos pos)
        {
            this.pos = pos;
            return this;
        }

        public Builder withSource(final @Nullable BlockAndTintGetter source)
        {
            this.source = source;
            return this;
        }

        public Builder withSource(final @Nullable ClientLevel source)
        {
            this.source = source;
            return this;
        }

        public Builder withSource(final @Nullable LevelAccessor level)
        {
            if (level == null) {
                this.source = null;
                return this;
            }

            this.source = new BlockAndTintGetter() {
                @Override
                public CardinalLighting cardinalLighting()
                {
                    return level.dimensionType().cardinalLightType().get();
                }

                @Override
                public int getBlockTint(final BlockPos pos, final ColorResolver colorResolver)
                {
                    int dist = Minecraft.getInstance().options.biomeBlendRadius().get();
                    if (dist == 0) {
                        return colorResolver.getColor(level.getBiome(pos).value(), pos.getX(), pos.getZ());
                    } else {
                        int count = (dist * 2 + 1) * (dist * 2 + 1);
                        int totalRed = 0;
                        int totalGreen = 0;
                        int totalBlue = 0;
                        Cursor3D cursor = new Cursor3D(pos.getX() - dist, pos.getY(), pos.getZ() - dist, pos.getX() + dist, pos.getY(), pos.getZ() + dist);
                        BlockPos.MutableBlockPos nextPos = new BlockPos.MutableBlockPos();

                        while (cursor.advance()) {
                            nextPos.set(cursor.nextX(), cursor.nextY(), cursor.nextZ());
                            int color = colorResolver.getColor(level.getBiome(nextPos).value(), nextPos.getX(), nextPos.getZ());
                            totalRed += ARGB.red(color);
                            totalGreen += ARGB.green(color);
                            totalBlue += ARGB.blue(color);
                        }

                        return ARGB.color(totalRed / count, totalGreen / count, totalBlue / count);
                    }
                }

                @Override
                public LevelLightEngine getLightEngine()
                {
                    return level.getLightEngine();
                }

                @Override
                public @org.jspecify.annotations.Nullable BlockEntity getBlockEntity(final BlockPos pos)
                {
                    return level.getBlockEntity(pos);
                }

                @Override
                public BlockState getBlockState(final BlockPos pos)
                {
                    return level.getBlockState(pos);
                }

                @Override
                public FluidState getFluidState(final BlockPos pos)
                {
                    return level.getFluidState(pos);
                }

                @Override
                public int getHeight()
                {
                    return level.getHeight();
                }

                @Override
                public int getMinY()
                {
                    return level.getMinY();
                }
            };

            return this;
        }

        public Builder withBlockEntity(final Supplier<@Nullable BlockEntity> blockEntityBuilder)
        {
            this.blockEntityBuilder = blockEntityBuilder;
            return this;
        }

        public SingleBlockBlockAndTintGetter createSingleBlockBlockAndTintGetter()
        {
            if (blockState == null)
                throw new IllegalStateException("A blockstate is required for a single block block and tint getter!");

            final BlockEntity blockEntity = blockEntityBuilder != null ? blockEntityBuilder.get() : null;
            return new SingleBlockBlockAndTintGetter(blockState, pos, source, blockEntity);
        }
    }
}
