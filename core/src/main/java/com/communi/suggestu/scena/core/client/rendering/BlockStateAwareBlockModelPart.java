package com.communi.suggestu.scena.core.client.rendering;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockStateAwareBlockModelPart extends BlockModelPart
{
    // Soft-override of an Iris extension to provide the "shader state" to its pipeline
    @Nullable
    BlockState getBlockAppearance();
}
