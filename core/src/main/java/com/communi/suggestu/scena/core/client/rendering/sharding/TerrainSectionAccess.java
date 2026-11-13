package com.communi.suggestu.scena.core.client.rendering.sharding;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

public interface TerrainSectionAccess
{
    SectionRenderDispatcher scena$dispatcher();

    TerrainSectionState<CompiledSectionMesh> scena$pending();

    void scena$vertexUploaded(CompiledSectionMesh mesh, ChunkSectionLayer layer);

    void scena$release(SectionMesh mesh);

    void scena$cancelTasks();
}
