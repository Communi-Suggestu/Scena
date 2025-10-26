package com.communi.suggestu.scena.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
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

    @Override
    public float getShade(@NotNull final Direction p_230487_1_, final boolean p_230487_2_)
    {
        if (this.source == null)
            return 0;

        return this.source.getShade(p_230487_1_, p_230487_2_);
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
