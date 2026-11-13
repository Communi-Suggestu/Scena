package com.communi.suggestu.scena.fabric.platform.client.rendering;

import java.util.Objects;
import java.util.function.Function;

import com.communi.suggestu.scena.core.client.rendering.terrain.NativeTerrainQuadWriter;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadView;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Buffers Indigo's already-lit quads, clipping only after its lighting and tint transforms have run.
 */
public final class ClippedTerrainOutput
{
    private final ResolvedTerrainModel model;
    private final Function<ChunkSectionLayer, ? extends VertexConsumer> layers;
    private final NativeTerrainQuadWriter writer;
    private final float x;
    private final float y;
    private final float z;

    public ClippedTerrainOutput(
        final ResolvedTerrainModel model, final Function<ChunkSectionLayer, ? extends VertexConsumer> layers,
        final float x, final float y, final float z
    )
    {
        this.model = Objects.requireNonNull(model, "model");
        this.layers = Objects.requireNonNull(layers, "layers");
        this.writer = new NativeTerrainQuadWriter(layers, false);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void accept(final QuadView lit)
    {
        final var source = Objects.requireNonNull(model.emittingQuad(), "Quad emitted outside the resolved model");
        final var triangles = model.clippedTriangles(source);
        if (triangles == null)
        {
            lit.buffer(OverlayTexture.NO_OVERLAY, layers.apply(lit.chunkLayer()));
            return;
        }
        final int[] colors = new int[4];
        final int[] lights = new int[4];
        for (int vertex = 0; vertex < 4; vertex++)
        {
            colors[vertex] = lit.color(vertex);
            lights[vertex] = lit.lightmap(vertex);
        }
        writer.putClipped(x, y, z, source.face(), lit.chunkLayer(), OverlayTexture.NO_OVERLAY, colors, lights, triangles);
    }
}
