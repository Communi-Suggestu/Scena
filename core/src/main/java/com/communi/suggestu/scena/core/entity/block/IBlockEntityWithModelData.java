package com.communi.suggestu.scena.core.entity.block;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a block entity which has its own model data.
 */
public interface IBlockEntityWithModelData
{
    /**
     * The model data of the block entity.
     *
     * @return The model data of the block entity.
     */
    @NotNull
    IBlockModelData getBlockModelData();
}
