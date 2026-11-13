package com.communi.suggestu.scena.mixin.terrain;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainBufferCapacity;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UberGpuBuffer.class)
public abstract class UberGpuBufferMixin implements TerrainBufferCapacity
{
    @Shadow
    @Final
    private int heapSize;

    @Override
    public int scena$capacity()
    {
        return heapSize;
    }
}
