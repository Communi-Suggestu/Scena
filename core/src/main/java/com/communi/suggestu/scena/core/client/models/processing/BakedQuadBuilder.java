package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.client.models.vertices.QuadProcessor;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class BakedQuadBuilder implements QuadProcessor<BakedQuadBuilder>
{
    private final List<VertexData> data = new ArrayList<>(4);
    private Direction            orientation;
    private BakedQuad.MaterialInfo texture;
    private Integer tintIndex = null;

    public BakedQuadBuilder(BakedQuad.MaterialInfo texture) {
        this.texture = texture;
    }

    protected BakedQuadBuilder() {
    }
    @Override
    public BakedQuadBuilder cullDirection(Direction orientation) {
        this.orientation = orientation;
        return this;
    }

    @Override
    public BakedQuadBuilder texture(BakedQuad.MaterialInfo texture) {
        this.texture = texture;
        return this;
    }

    public BakedQuadBuilder tintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
        return this;
    }

    @Override
    public BakedQuadBuilder vertex(final VertexData data)
    {
        this.data.add(data.vertexIndex(), data);
        return this;
    }

    public BakedQuad build() {
        if (data.size() != 4) {
            throw new IllegalStateException("not enough data");
        }
        if (texture == null) {
            throw new IllegalStateException("material not set");
        }

        if (this.tintIndex != null) {
            texture = new BakedQuad.MaterialInfo(
                texture.sprite(),
                texture.layer(),
                texture.itemRenderType(),
                tintIndex,
                texture.shade(),
                texture.lightEmission()
            );
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
            orientation,
            texture
        );
    }
}
