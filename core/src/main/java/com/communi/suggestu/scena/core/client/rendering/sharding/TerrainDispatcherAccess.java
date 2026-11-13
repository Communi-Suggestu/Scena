package com.communi.suggestu.scena.core.client.rendering.sharding;

import com.mojang.blaze3d.vertex.UberGpuBuffer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.SectionMesh;

public interface TerrainDispatcherAccess
{
    UberGpuBuffer<SectionMesh> scena$vertices(ChunkSectionLayer layer);

    int scena$stagingCapacity();

    int scena$heapCapacity(ChunkSectionLayer layer);

    TerrainUploadRegistry scena$uploads();
}
