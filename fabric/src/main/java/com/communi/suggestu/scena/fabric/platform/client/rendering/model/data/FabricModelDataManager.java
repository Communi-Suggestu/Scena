package com.communi.suggestu.scena.fabric.platform.client.rendering.model.data;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.models.data.IModelDataManager;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class FabricModelDataManager implements IModelDataManager
{
    private static final FabricModelDataManager INSTANCE = new FabricModelDataManager();
    private static final IBlockModelData EMPTY = new ModelDataBuilder().build();

    public static FabricModelDataManager getInstance()
    {
        return INSTANCE;
    }

    private FabricModelDataManager()
    {
    }

    @Override
    public void requestModelDataRefresh(final BlockEntity blockEntity)
    {
        // NO-OP
    }

    @Override
    public IBlockModelData empty()
    {
        return EMPTY;
    }


}
