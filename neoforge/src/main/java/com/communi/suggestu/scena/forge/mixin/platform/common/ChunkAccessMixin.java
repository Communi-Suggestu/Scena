package com.communi.suggestu.scena.forge.mixin.platform.common;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements IForgeBlockEntityPositionHolder {

    @Unique
    public ConcurrentHashMap<Class<?>, Set<BlockPos>> blockEntityPositions = new ConcurrentHashMap<>();


    @Override
    public Set<BlockPos> scena$getPositions(Class<? extends BlockEntity> blockEntityClass) {
        return blockEntityPositions.getOrDefault(blockEntityClass, Set.of());
    }

    @Override
    public void scena$add(Class<? extends BlockEntity> blockEntityClass, BlockPos pos) {
        blockEntityPositions.computeIfAbsent(blockEntityClass, k -> ConcurrentHashMap.newKeySet()).add(pos);
    }

    @Override
    public void scena$remove(Class<? extends BlockEntity> blockEntityClass, BlockPos pos) {
        blockEntityPositions.computeIfAbsent(blockEntityClass, k -> ConcurrentHashMap.newKeySet()).remove(pos);
    }

    @Override
    public Map<Class<?>, Set<BlockPos>> scena$getBlockEntityPositions() {
        return blockEntityPositions;
    }
}
