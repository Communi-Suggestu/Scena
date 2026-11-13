package com.communi.suggestu.scena.mixin.terrain;

import com.communi.suggestu.scena.core.client.rendering.sharding.TerrainBuilderAccess;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BufferBuilder.class)
public interface BufferBuilderMixin extends TerrainBuilderAccess
{
    @Override
    @Accessor("vertices")
    int scena$vertexCount();

    @Override
    @Accessor("format")
    VertexFormat scena$format();
}
