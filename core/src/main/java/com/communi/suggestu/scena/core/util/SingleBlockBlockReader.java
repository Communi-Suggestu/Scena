package com.communi.suggestu.scena.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SingleBlockBlockReader implements BlockGetter
{

    protected final BlockState blockState;
    protected final BlockPos pos;
    @Nullable
    protected final BlockGetter source;
    @Nullable
    protected final BlockEntity blockEntity;

    protected SingleBlockBlockReader(final BlockState blockState, final BlockPos pos, @Nullable final BlockGetter source, @Nullable final BlockEntity blockEntity)
    {
        this.blockState = blockState;
        this.pos = pos;
        this.source = source;
        this.blockEntity = blockEntity;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(@NotNull final BlockPos pos)
    {
        if (pos == this.pos)
        {
            return this.blockEntity;
        }

        return source == null ? null : source.getBlockEntity(pos);
    }

    @NotNull
    @Override
    public BlockState getBlockState(@NotNull final BlockPos pos)
    {
        if (pos == this.pos)
        {
            return blockState;
        }

        return source == null ? Blocks.AIR.defaultBlockState() : source.getBlockState(pos);
    }

    @NotNull
    @Override
    public FluidState getFluidState(@NotNull final BlockPos pos)
    {
        return getBlockState(pos).getFluidState();
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public int getMinY()
    {
        return 0;
    }

    public static class Builder {
        private           BlockState  blockState;
        private           BlockPos    pos = BlockPos.ZERO;
        private @Nullable BlockGetter           source;
        private Supplier<@Nullable BlockEntity> blockEntityBuilder;

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

        public Builder withSource(final @Nullable BlockGetter source)
        {
            this.source = source;
            return this;
        }

        public Builder withBlockEntity(final Supplier<@Nullable BlockEntity> blockEntityBuilder)
        {
            this.blockEntityBuilder = blockEntityBuilder;
            return this;
        }

        public SingleBlockBlockReader createSingleBlockBlockReader()
        {
            if (blockState == null)
                throw new IllegalStateException("A blockstate is required for a single block block reader!");

            final BlockEntity blockEntity = blockEntityBuilder != null ? blockEntityBuilder.get() : null;
            return new SingleBlockBlockReader(blockState, pos, source, blockEntity);
        }
    }
}
