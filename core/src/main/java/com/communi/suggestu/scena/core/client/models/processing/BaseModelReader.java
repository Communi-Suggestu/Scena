package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.client.models.vertices.VertexProcessor;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class BaseModelReader implements VertexProcessor
{

    @Override
    public void cullDirection(
      @Nullable final Direction orientation)
    {
    }

    @Override
    public void texture(final BakedQuad.MaterialInfo texture)
    {

    }

    @Override
    public void vertex(final VertexData data)
    {
    }
}