package com.communi.suggestu.scena.core.client.rendering.terrain;

import java.util.Objects;
import java.util.List;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;

/**
 * Writes already-lit quads into section-owned layer builders. Never retains a builder across quad boundaries.
 */
public final class NativeTerrainQuadWriter implements BlockQuadOutput
{
    private final Function<ChunkSectionLayer, ? extends VertexConsumer> layers;
    private final boolean forceOpaque;
    private final Function<BakedQuad, List<FaceTriangleClipper.Triangle>> clippedTriangles;

    public NativeTerrainQuadWriter(
        final Function<ChunkSectionLayer, ? extends VertexConsumer> layers,
        final boolean forceOpaque
    )
    {
        this(layers, forceOpaque, quad -> null);
    }

    public NativeTerrainQuadWriter(
        final Function<ChunkSectionLayer, ? extends VertexConsumer> layers,
        final boolean forceOpaque,
        final Function<BakedQuad, List<FaceTriangleClipper.Triangle>> clippedTriangles
    )
    {
        this.layers = Objects.requireNonNull(layers, "layers");
        this.forceOpaque = forceOpaque;
        this.clippedTriangles = Objects.requireNonNull(clippedTriangles, "clippedTriangles");
    }

    public static boolean supports(final BakedQuad quad)
    {
        return quad != null && quad.materialInfo() != null
            && (quad.materialInfo().layer() == ChunkSectionLayer.SOLID
                || quad.materialInfo().layer() == ChunkSectionLayer.CUTOUT
                || quad.materialInfo().layer() == ChunkSectionLayer.TRANSLUCENT)
            && quad.materialInfo().tintIndex() >= -1;
    }

    @Override
    public void put(final float x, final float y, final float z, final BakedQuad quad, final QuadInstance instance)
    {
        if (!supports(quad))
        {
            throw new IllegalArgumentException("Native terrain requires preflighted section-layer quads");
        }
        final List<FaceTriangleClipper.Triangle> triangles = clippedTriangles.apply(quad);
        if (triangles != null)
        {
            putClipped(x, y, z, quad, instance, triangles);
            return;
        }
        final VertexConsumer builder = layers.apply(forceOpaque ? ChunkSectionLayer.SOLID : quad.materialInfo().layer());
        builder.putBlockBakedQuad(x, y, z, quad, instance);
    }

    /**
     * Writes triangles clipped from an already-lit original quad. Degenerate fourth vertices keep the section's
     * quad topology while preserving the original diagonal and interpolated vertex attributes. This does not by
     * itself preserve the terrain shader's dynamically sampled lightmap result.
     */
    public void putClipped(
        final float x, final float y, final float z, final BakedQuad quad, final QuadInstance instance,
        final List<FaceTriangleClipper.Triangle> triangles
    )
    {
        if (!supports(quad))
        {
            throw new IllegalArgumentException("Native terrain requires preflighted section-layer quads");
        }
        Objects.requireNonNull(instance, "instance");
        final int[] colors = new int[4];
        final int[] lights = new int[4];
        for (int vertex = 0; vertex < 4; vertex++)
        {
            colors[vertex] = instance.getColor(vertex);
            lights[vertex] = instance.getLightCoordsWithEmission(vertex, quad.materialInfo().lightEmission());
        }
        putClipped(x, y, z, quad.direction(), quad.materialInfo().layer(), instance.overlayCoords(), colors, lights, triangles);
    }

    /**
     * Writes an original quad's already-lit attributes clipped to the supplied triangles.
     */
    public void putClipped(
        final float x, final float y, final float z, final Direction face, final ChunkSectionLayer layer,
        final int overlay, final int[] colors, final int[] lights, final List<FaceTriangleClipper.Triangle> triangles
    )
    {
        Objects.requireNonNull(face, "face");
        if (layer != ChunkSectionLayer.SOLID && layer != ChunkSectionLayer.CUTOUT && layer != ChunkSectionLayer.TRANSLUCENT)
        {
            throw new IllegalArgumentException("Native terrain requires a supported section layer");
        }
        if (colors == null || colors.length != 4 || lights == null || lights.length != 4)
        {
            throw new IllegalArgumentException("Clipped terrain requires four lit color and light values");
        }
        Objects.requireNonNull(triangles, "triangles");
        for (final var triangle : triangles)
        {
            Objects.requireNonNull(triangle, "triangle");
            Objects.requireNonNull(triangle.v0(), "triangle.v0");
            Objects.requireNonNull(triangle.v1(), "triangle.v1");
            Objects.requireNonNull(triangle.v2(), "triangle.v2");
        }

        final var normal = face.getUnitVec3f();
        for (final var triangle : triangles)
        {
            final VertexConsumer builder = layers.apply(forceOpaque ? ChunkSectionLayer.SOLID : layer);
            final FaceTriangleClipper.Vertex[] vertices = {triangle.v0(), triangle.v1(), triangle.v2(), triangle.v2()};
            for (final var vertex : vertices)
            {
                final var position = vertex.position();
                builder.addVertex(
                    x + position.x(), y + position.y(), z + position.z(),
                    interpolateColor(vertex, colors), position.u(), position.v(), overlay,
                    interpolateLight(vertex, lights),
                    normal.x(), normal.y(), normal.z()
                );
            }
        }
    }

    private static int interpolateColor(final FaceTriangleClipper.Vertex vertex, final int[] colors)
    {
        int result = 0;
        for (int shift = 0; shift < 32; shift += 8)
        {
            result |= interpolateChannel(vertex, colors, shift, 0xff) << shift;
        }
        return result;
    }

    private static int interpolateLight(final FaceTriangleClipper.Vertex vertex, final int[] lights)
    {
        return interpolateChannel(vertex, lights, 0, 0xffff) | interpolateChannel(vertex, lights, 16, 0xffff) << 16;
    }

    private static int interpolateChannel(
        final FaceTriangleClipper.Vertex vertex, final int[] values, final int shift, final int mask
    )
    {
        return Math.round(vertex.interpolate((values[0] >>> shift) & mask, (values[1] >>> shift) & mask,
            (values[2] >>> shift) & mask, (values[3] >>> shift) & mask)) & mask;
    }
}
