package com.communi.suggestu.scena.forge.mixin.platform.common;

import com.communi.suggestu.scena.core.entity.block.IBlockEntityPositionManager;
import com.communi.suggestu.scena.forge.platform.entity.IForgeBlockEntityPositionHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public class ForgeBlockEntityPositionManager implements IBlockEntityPositionManager {

    private static final ForgeBlockEntityPositionManager INSTANCE = new ForgeBlockEntityPositionManager();

    public static ForgeBlockEntityPositionManager getInstance() {
        return INSTANCE;
    }

    private ForgeBlockEntityPositionManager() {
    }

    private IForgeBlockEntityPositionHolder getHolder(LevelReader level, ChunkPos chunkPos) {
        return (IForgeBlockEntityPositionHolder) level.getChunk(chunkPos.x, chunkPos.z);
    }

    @Override
    public Set<BlockPos> getPositions(Class<? extends BlockEntity> blockEntityClass, LevelReader level, ChunkPos chunkPos) {
        return getHolder(level, chunkPos).scena$getPositions(blockEntityClass);
    }

    @Override
    public void add(BlockEntity blockEntity) {
        if (blockEntity.getLevel() != null)
            throw new IllegalArgumentException("Block entity must be in a level to be added to the position manager");

        getHolder(blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos())).scena$add(blockEntity.getClass(), blockEntity.getBlockPos());
    }

    @Override
    public void remove(BlockEntity blockEntity) {
        if (blockEntity.getLevel() != null)
            throw new IllegalArgumentException("Block entity must be in a level to be removed from the position manager");

        getHolder(blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos())).scena$remove(blockEntity.getClass(), blockEntity.getBlockPos());
    }
}
