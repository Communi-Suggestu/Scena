package com.communi.suggestu.scena.forge.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.IExtendedBakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements IExtendedBakedQuad
{
    @Unique
    private BlockState blockState = null;

    @Override
    public void setBlockState(final BlockState blockState)
    {
        this.blockState = blockState;
    }

    @Override
    public @Nullable BlockState getBlockState()
    {
        return this.blockState;
    }
}
