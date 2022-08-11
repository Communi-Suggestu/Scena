package com.communi.suggestu.scena.forge.platform.client.model.data;

import com.communi.suggestu.scena.core.client.models.data.IModelDataManager;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ForgeModelDataManager implements IModelDataManager
{
    private static final ForgeModelDataManager INSTANCE = new ForgeModelDataManager();

    public static ForgeModelDataManager getInstance()
    {
        return INSTANCE;
    }

    private ForgeModelDataManager()
    {
    }

    @Override
    public void requestModelDataRefresh(final BlockEntity blockEntity)
    {
        if (blockEntity.getLevel() != null) {
            blockEntity.getLevel().getModelDataManager().requestRefresh(blockEntity);
        }
    }
}
