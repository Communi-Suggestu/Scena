package com.communi.suggestu.scena.mixin.terrain;

import java.nio.ByteBuffer;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainBufferCapacity;
import com.mojang.blaze3d.vertex.StagingBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StagingBuffer.class)
public abstract class StagingBufferMixin implements TerrainBufferCapacity
{
    @Shadow
    protected abstract ByteBuffer getWriteBuffer();

    @Override
    public int scena$capacity()
    {
        return getWriteBuffer().capacity();
    }
}
