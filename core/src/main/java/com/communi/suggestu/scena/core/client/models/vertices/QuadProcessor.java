package com.communi.suggestu.scena.core.client.models.vertices;

import com.communi.suggestu.scena.core.client.models.processing.VertexData;
import com.communi.suggestu.scena.core.client.utils.BakedQuadUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Represents a consumer of vertex information.
 * Can be used to pipe a vertex's information into a target system,
 * or to read a single piece of information from a packet vertex.
 */
public interface QuadProcessor<T extends QuadProcessor<T>> extends Consumer<VertexData>
{

    /**
     * The current instance of the processor.
     * Correctly typed to it-self.
     *
     * @return The current instance.
     */
    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    /**
     * Processes the culling direction for the quad that is being processed.
     * Can be {@code null} if culling should be disabled.
     *
     * @param orientation The orientation of the culling.
     * @return The processor with the culling direction stored.
     */
    default T cullDirection(@Nullable Direction orientation) {
        return self();
    }

    /**
     * Processes the material of a quad.
     *
     * @param material The material of the quad.
     * @return The processor with the material stored.
     */
    default T material(BakedQuad.MaterialInfo material) {
        return self();
    }

    /**
     * Processes the given vertex as the next vertex in a quad.
     * <p>
     *     Whether this throws when this is called for to many vertices for a quad
     *     is up to the implementation.
     * </p>
     *
     * @param data The vertex data to add as the next vertex.
     * @return The processor with the vertex data added.
     */
    default T vertex(VertexData data) {
        return self();
    }

    @Override
    default void accept(VertexData vertexData) {
        vertex(vertexData);
    }

    /**
     * Invoked to indicate that you completed the processing of an entire quad.
     * This allows the processor to be reused and reset ths internal storage.
     *
     * @return The processor now reset to its original state.
     */
    default T complete() {
        return self();
    }

    /**
     * Extracts all information from the given quad and immediately completes it.
     * <p>
     *     Note this requires an empty processor.
     * </p>
     * <p>
     *     Note this leaves the processor back in its original empty state.
     * </p>
     * @param quad The quad to process.
     */
    default T from(BakedQuad quad)
    {
        return BakedQuadUtils.forEachVertex(quad, material(quad.materialInfo())
            .cullDirection(quad.direction()))
            .complete();
    }
}
