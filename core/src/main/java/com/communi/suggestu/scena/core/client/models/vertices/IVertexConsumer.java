package com.communi.suggestu.scena.core.client.models.vertices;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

/**
 * Represents a consumer of vertex information.
 * Can be used to pipe a vertex's information into a target system,
 * or to read a single piece of information from a packet vertex.
 */
public interface IVertexConsumer
{
    /**
     * @return the format that should be used for passed data.
     */
    VertexFormat getVertexFormat();

    void setQuadTint(int tint);
    void setQuadOrientation(Direction orientation);
    void setApplyDiffuseLighting(boolean diffuse);
    void setTexture(TextureAtlasSprite texture);
    void put(int vertexIndex, int element, float... data);
    default void onComplete() {}
}
