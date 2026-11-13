package com.communi.suggestu.scena.core.client.rendering.sharding;

import com.mojang.blaze3d.vertex.VertexFormat;

public interface TerrainBuilderAccess
{
    int scena$vertexCount();

    VertexFormat scena$format();
}
