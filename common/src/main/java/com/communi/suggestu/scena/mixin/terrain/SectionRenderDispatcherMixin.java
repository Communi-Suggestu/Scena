package com.communi.suggestu.scena.mixin.terrain;

import java.util.Map;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainBufferCapacity;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainDispatcherAccess;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainUberBuffersAccess;
import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainUploadRegistry;
import com.mojang.blaze3d.vertex.StagingBuffer;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionRenderDispatcher.class)
public abstract class SectionRenderDispatcherMixin implements TerrainDispatcherAccess
{
    @Shadow
    @Final
    private Map<ChunkSectionLayer, ?> chunkUberBuffers;

    @Shadow
    @Final
    private StagingBuffer stagingBuffer;

    @Shadow
    public abstract void lock();

    @Shadow
    public abstract void unlock();

    @Unique
    private final TerrainUploadRegistry scena$uploads = new TerrainUploadRegistry();

    @Override
    public UberGpuBuffer<SectionMesh> scena$vertices(final ChunkSectionLayer layer)
    {
        return ((TerrainUberBuffersAccess) chunkUberBuffers.get(layer)).scena$vertexBuffer();
    }

    @Override
    public int scena$stagingCapacity()
    {
        return ((TerrainBufferCapacity) stagingBuffer).scena$capacity();
    }

    @Override
    public int scena$heapCapacity(final ChunkSectionLayer layer)
    {
        return ((TerrainBufferCapacity) scena$vertices(layer)).scena$capacity();
    }

    @Override
    public TerrainUploadRegistry scena$uploads()
    {
        return scena$uploads;
    }

    @Inject(method = "dispose()V", at = @At("HEAD"))
    private void scena$dispose(final CallbackInfo ci)
    {
        lock();
        try
        {
            scena$uploads.close();
        }
        finally
        {
            unlock();
        }
    }
}
