package com.communi.suggestu.scena.core.client.models.data;

import com.communi.suggestu.scena.core.client.IClientManager;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The model data manager.
 * Allows the model data to be managed on the current level.
 */
public interface IModelDataManager {

    /**
     * The current model data manager.
     * Allows the model data to be managed on the current level.
     *
     * @return The model data manager.
     */
    static IModelDataManager getInstance() {
        return IClientManager.getInstance().getModelDataManager();
    }

    /**
     * Refreshes the model data for the block entity.
     *
     * @param blockEntity The block entity to refresh the model data for.
     */
    void requestModelDataRefresh(BlockEntity blockEntity);

    /**
     * Returns an empty block model data to use in cases where it is not available.
     *
     * @return The block model data.
     */
    IBlockModelData empty();
}
