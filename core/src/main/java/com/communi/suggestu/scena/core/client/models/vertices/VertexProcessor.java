package com.communi.suggestu.scena.core.client.models.vertices;

import com.communi.suggestu.scena.core.client.models.processing.VertexData;
import com.communi.suggestu.scena.core.client.utils.BakedQuadUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a consumer of vertex information.
 * Can be used to pipe a vertex's information into a target system,
 * or to read a single piece of information from a packet vertex.
 */
public interface VertexProcessor
{
    void cullDirection(@Nullable Direction orientation);
    void texture(BakedQuad.MaterialInfo texture);
    void vertex(VertexData data);
    default void onComplete() {}

    default void put(BakedQuad quad)
    {
        this.texture(quad.materialInfo());
        this.cullDirection(quad.direction());

        BakedQuadUtils.forEachVertex(quad, this::vertex);

        this.onComplete();
    }
}
