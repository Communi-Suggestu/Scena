package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.client.models.vertices.VertexProcessor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseModelReader implements VertexProcessor
{

    @Override
    public void tintIndex(
      final int tint)
    {
    }

    @Override
    public void cullDirection(
      @Nullable final Direction orientation)
    {
    }

    @Override
    public void shade(
      final boolean diffuse)
    {

    }

    @Override
    public void texture(
      @NotNull final TextureAtlasSprite texture)
    {
    }

    @Override
    public void light(final int lightEmission)
    {
    }

    @Override
    public void vertex(final VertexData data)
    {
    }
}