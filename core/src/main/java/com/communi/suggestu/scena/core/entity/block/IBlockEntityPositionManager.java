package com.communi.suggestu.scena.core.entity.block;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

/**
 * Defines a manager which handles the positions of block entities in a level.
 */
public interface IBlockEntityPositionManager {

    /**
     * The current instance of the block entity position manager for the current platform.
     * @return The current instance of the block entity position manager.
     */
    static IBlockEntityPositionManager getInstance() {
        return IScenaPlatform.getInstance().getBlockEntityPositionManager();
    }

    /**
     * Gets all positions of the given block entity class in the given chunk.
     *
     * @param blockEntityClass The class of the block entity.
     * @param level The level to search in.
     * @param chunkPos The chunk to search in.
     * @return All positions of the given block entity class in the given chunk.
     */
    Set<BlockPos> getPositions(Class<? extends BlockEntity> blockEntityClass, LevelReader level, ChunkPos chunkPos);

    /**
     * Gets all positions of the given block entity class in the given chunk.
     *
     * @param blockEntityClass The class of the block entity.
     * @param level The level to search in.
     * @param chunkX The x coordinate of the chunk to search in.
     * @param chunkZ The z coordinate of the chunk to search in.
     * @return All positions of the given block entity class in the given chunk.
     */
    default Set<BlockPos> getPositions(Class<? extends BlockEntity> blockEntityClass, LevelReader level, int chunkX, int chunkZ) {
        return getPositions(blockEntityClass, level, new ChunkPos(chunkX, chunkZ));
    }

    /**
     * Gets all positions of the given block entity class in the chunk of the given position.
     *
     * @param blockEntityClass The class of the block entity.
     * @param level The level to search in.
     * @param pos The position to search in.
     * @return All positions of the given block entity class in the given chunk.
     */
    default Set<BlockPos> getPositions(Class<? extends BlockEntity> blockEntityClass, LevelReader level, BlockPos pos) {
        return getPositions(blockEntityClass, level, new ChunkPos(pos));
    }

    /**
     * Gets all positions of the given block entity class in the chunk of the given position.
     *
     * @param blockEntityClass The class of the block entity.
     * @param level The level to search in.
     * @param x The x coordinate of the position to search in.
     * @param y The y coordinate of the position to search in.
     * @param z The z coordinate of the position to search in.
     * @return All positions of the given block entity class in the given chunk.
     */
    default Set<BlockPos> getPositions(Class<? extends BlockEntity> blockEntityClass, LevelReader level, int x, int y, int z) {
        return getPositions(blockEntityClass, level, new BlockPos(x, y, z));
    }

    /**
     * Gets all positions of the given block entity in the given chunk.
     *
     * @param instance The block entity instance.
     * @param level The level to search in.
     * @param chunkPos The chunk to search in.
     * @param <T> The type of the block entity.
     * @return All positions of the given block entity in the given chunk.
     */
    default <T extends BlockEntity> Set<BlockPos> getPositions(T instance, LevelReader level, ChunkPos chunkPos) {
        return getPositions(instance.getClass(), level, chunkPos);
    }

    /**
     * Gets all positions of the given block entity in the given chunk.
     *
     * @param instance The block entity instance.
     * @param level The level to search in.
     * @param chunkX The x coordinate of the chunk to search in.
     * @param chunkZ The z coordinate of the chunk to search in.
     * @param <T> The type of the block entity.
     * @return All positions of the given block entity in the given chunk.
     */
    default <T extends BlockEntity> Set<BlockPos> getPositions(T instance, LevelReader level, int chunkX, int chunkZ) {
        return getPositions(instance.getClass(), level, chunkX, chunkZ);
    }

    /**
     * Gets all positions of the given block entity in the chunk of the given position.
     *
     * @param instance The block entity instance.
     * @param level The level to search in.
     * @param pos The position to search in.
     * @param <T> The type of the block entity.
     * @return All positions of the given block entity in the given chunk.
     */
    default <T extends BlockEntity> Set<BlockPos> getPositions(T instance, LevelReader level, BlockPos pos) {
        return getPositions(instance.getClass(), level, pos);
    }

    /**
     * Gets all positions of the given block entity in the chunk of the given position.
     *
     * @param instance The block entity instance.
     * @param level The level to search in.
     * @param x The x coordinate of the position to search in.
     * @param y The y coordinate of the position to search in.
     * @param z The z coordinate of the position to search in.
     * @param <T> The type of the block entity.
     * @return All positions of the given block entity in the given chunk.
     */
    default <T extends BlockEntity> Set<BlockPos> getPositions(T instance, LevelReader level, int x, int y, int z) {
        return getPositions(instance.getClass(), level, x, y, z);
    }

    /**
     * Adds the given block entity to the manager.
     *
     * @param blockEntity The block entity to add.
     */
    void add(BlockEntity blockEntity);

    /**
     * Removes the given block entity from the manager.
     *
     * @param blockEntity The block entity to remove.
     */
    void remove(BlockEntity blockEntity);
}
