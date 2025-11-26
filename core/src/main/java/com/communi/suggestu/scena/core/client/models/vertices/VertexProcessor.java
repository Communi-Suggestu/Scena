package com.communi.suggestu.scena.core.client.models.vertices;

import com.communi.suggestu.scena.core.client.models.processing.VertexData;
import com.communi.suggestu.scena.core.client.utils.BakedQuadUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a consumer of vertex information.
 * Can be used to pipe a vertex's information into a target system,
 * or to read a single piece of information from a packet vertex.
 */
public interface VertexProcessor
{
    void tintIndex(int tint);
    void cullDirection(@Nullable Direction orientation);
    void shade(boolean diffuse);
    void texture(TextureAtlasSprite texture);
    void vertex(VertexData data);
    void light(int lightEmission);
    default void onComplete() {}

    default void put(BakedQuad quad)
    {
        this.texture(quad.sprite());
        this.cullDirection(quad.direction());
        this.light(quad.lightEmission());
        if(quad.isTinted())
        {
            this.tintIndex(quad.tintIndex());
        }
        this.shade(quad.shade());

        BakedQuadUtils.forEachVertex(quad, this::vertex);

        this.onComplete();
    }
}
