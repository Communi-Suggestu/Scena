package com.communi.suggestu.scena.mixin.terrain;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCpuBatch;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainCpuResultAccess;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionCompiler.Results.class)
public abstract class SectionCompilerResultsMixin implements TerrainCpuResultAccess
{
    @Unique
    private TerrainCpuBatch scena$batch;

    @Override
    public TerrainCpuBatch scena$cpuBatch()
    {
        return scena$batch;
    }

    @Override
    public void scena$cpuBatch(final TerrainCpuBatch batch)
    {
        scena$batch = batch;
    }

    @Inject(method = "release()V", at = @At("HEAD"))
    private void scena$release(final CallbackInfo ci)
    {
        if (scena$batch != null)
        {
            scena$batch.close();
        }
    }
}
