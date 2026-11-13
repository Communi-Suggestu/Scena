package com.communi.suggestu.scena.mixin.terrain;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainUberBuffersAccess;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import net.minecraft.client.renderer.chunk.SectionMesh;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.chunk.SectionRenderDispatcher$SectionUberBuffers")
public interface SectionUberBuffersMixin extends TerrainUberBuffersAccess
{
    @Override
    @Accessor("vertexBuffer")
    UberGpuBuffer<SectionMesh> scena$vertexBuffer();
}
