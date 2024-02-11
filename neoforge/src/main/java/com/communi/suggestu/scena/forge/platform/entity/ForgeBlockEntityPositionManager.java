package com.communi.suggestu.scena.forge.platform.entity;

import com.communi.suggestu.scena.core.entity.block.IBlockEntityPositionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ForgeBlockEntityPositionManager implements IBlockEntityPositionManager {

    private static final ForgeBlockEntityPositionManager INSTANCE = new ForgeBlockEntityPositionManager();

    public static ForgeBlockEntityPositionManager getInstance() {
        return INSTANCE;
    }

    private ForgeBlockEntityPositionManager() {
    }

    @Nullable
    private IForgeBlockEntityPositionHolder getHolder(LevelReader level, ChunkPos chunkPos) {
        return (IForgeBlockEntityPositionHolder) level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
    }

    @Override
    public Set<BlockPos> getPositions(Class<? extends BlockEntity> blockEntityClass, LevelReader level, ChunkPos chunkPos) {
        final IForgeBlockEntityPositionHolder holder = getHolder(level, chunkPos);
        if (holder == null) {
            return Set.of();
        }

        return holder.scena$getPositions(blockEntityClass);
    }

    @Override
    public void add(BlockEntity blockEntity) {
        if (blockEntity.getLevel() == null)
            throw new IllegalArgumentException("Block entity must be in a level to be added to the position manager");

        final IForgeBlockEntityPositionHolder holder = getHolder(blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
        if (holder == null) {
            return;
        }

        holder.scena$add(blockEntity.getClass(), blockEntity.getBlockPos());
    }

    @Override
    public void remove(BlockEntity blockEntity) {
        if (blockEntity.getLevel() == null)
            throw new IllegalArgumentException("Block entity must be in a level to be removed from the position manager");

        final IForgeBlockEntityPositionHolder holder = getHolder(blockEntity.getLevel(), new ChunkPos(blockEntity.getBlockPos()));
        if (holder == null) {
            return;
        }

        holder.scena$remove(blockEntity.getClass(), blockEntity.getBlockPos());
    }
}
