package com.communi.suggestu.scena.core.client.rendering.sharding;

import com.mojang.blaze3d.vertex.UberGpuBuffer;
import net.minecraft.client.renderer.chunk.SectionMesh;

public interface TerrainUberBuffersAccess
{
    UberGpuBuffer<SectionMesh> scena$vertexBuffer();
}
