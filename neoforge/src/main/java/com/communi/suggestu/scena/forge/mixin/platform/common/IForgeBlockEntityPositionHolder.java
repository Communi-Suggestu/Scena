package com.communi.suggestu.scena.forge.mixin.platform.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.Set;

public interface IForgeBlockEntityPositionHolder {
    Set<BlockPos> scena$getPositions(Class<? extends BlockEntity> blockEntityClass);

    void scena$add(Class<? extends BlockEntity> blockEntityClass, BlockPos pos);

    void scena$remove(Class<? extends BlockEntity> blockEntityClass, BlockPos pos);

    Map<Class<?>, Set<BlockPos>> scena$getBlockEntityPositions();
}
