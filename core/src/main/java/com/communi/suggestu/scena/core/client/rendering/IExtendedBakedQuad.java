package com.communi.suggestu.scena.core.client.rendering;

import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IExtendedBakedQuad
{

    void setBlockState(BlockState blockState);

    @Nullable
    BlockState getBlockState();
}
