package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.client.models.vertices.VertexProcessor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class BakedQuadBuilder implements VertexProcessor
{
    private final List<VertexData> data = new ArrayList<>(4);
    private       int          tint       = -1;
    private int lightEmission = 0;
    private Direction orientation;
    private BakedQuad.SpriteInfo texture;
    private boolean applyDiffuseLighting = true;


    public BakedQuadBuilder(BakedQuad.SpriteInfo texture) {
        this.texture = texture;
    }

    protected BakedQuadBuilder() {
    }

    @Override
    public void tintIndex(int tint) {
        this.tint = tint;
    }

    @Override
    public void cullDirection(Direction orientation) {
        this.orientation = orientation;
    }

    @Override
    public void texture(BakedQuad.SpriteInfo texture) {
        this.texture = texture;
    }

    @Override
    public void vertex(final VertexData data)
    {
        this.data.add(data.vertexIndex(), data);
    }

    @Override
    public void shade(boolean diffuse) {
        this.applyDiffuseLighting = diffuse;
    }

    @Override
    public void light(final int lightEmission)
    {
        this.lightEmission = lightEmission;
    }

    public BakedQuad build() {
        if (data.size() != 4) {
            throw new IllegalStateException("not enough data");
        }
        if (texture == null) {
            throw new IllegalStateException("texture not set");
        }

        final VertexData v0 = data.get(0);
        final VertexData v1 = data.get(1);
        final VertexData v2 = data.get(2);
        final VertexData v3 = data.get(3);

        return new BakedQuad(
            v0.position(),
            v1.position(),
            v2.position(),
            v3.position(),
            v0.uv(),
            v1.uv(),
            v2.uv(),
            v3.uv(),
            tint,
            orientation,
            texture,
            applyDiffuseLighting,
            lightEmission
        );
    }
}
