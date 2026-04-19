package com.communi.suggestu.scena.core.util;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SingleBlockBlockAndLightGetter extends SingleBlockBlockReader implements BlockAndLightGetter
{
    @Nullable
    private final BlockAndLightGetter source;

    protected SingleBlockBlockAndLightGetter(
        final BlockState blockState,
        final BlockPos pos,
        final @Nullable BlockAndLightGetter source,
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

        public SingleBlockBlockAndLightGetter createSingleBlockBlockAndTintGetter()
        {
            if (blockState == null)
                throw new IllegalStateException("A blockstate is required for a single block block and tint getter!");

            final BlockEntity blockEntity = blockEntityBuilder != null ? blockEntityBuilder.get() : null;
            return new SingleBlockBlockAndLightGetter(blockState, pos, source, blockEntity);
        }
    }
}
