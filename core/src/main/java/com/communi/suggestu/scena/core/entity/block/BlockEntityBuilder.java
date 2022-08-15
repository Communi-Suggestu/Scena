package com.communi.suggestu.scena.core.entity.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A functional builder interface to get a new instance for a given block entity.
 *
 * @param <T>
 */
@FunctionalInterface
public
interface BlockEntityBuilder<T extends BlockEntity>
{
    T create(BlockPos pPos, BlockState pState);
}
