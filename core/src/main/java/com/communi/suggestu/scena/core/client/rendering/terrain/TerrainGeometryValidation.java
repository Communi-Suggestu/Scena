package com.communi.suggestu.scena.core.client.rendering.terrain;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Direction;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import java.util.List;

/**
 * Complete, write-free eligibility for resolved axis-aligned rectangle adapters.
 */
public final class TerrainGeometryValidation
{
    private TerrainGeometryValidation()
    {
    }

    public static boolean supports(final TerrainGeometry geometry)
    {
        if (geometry == null)
        {
            return false;
        }
        final List<TerrainGeometryPart> parts = geometry.parts();
        for (int partIndex = 0; partIndex < parts.size(); partIndex++)
        {
            final TerrainGeometryPart part = parts.get(partIndex);
            if (part.ambientOcclusion() == null
                || (part.materialFlags() & ~(BakedQuad.FLAG_ANIMATED | BakedQuad.FLAG_TRANSLUCENT)) != 0)
            {
                return false;
            }
            final List<TerrainQuad> quads = part.quads();
            for (int quadIndex = 0; quadIndex < quads.size(); quadIndex++)
            {
                final TerrainQuad quad = quads.get(quadIndex);
                if (!supports(quad) || materialFlags(quad) != part.materialFlags())
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * The vanilla terrain shader samples the dynamic lightmap at each vertex. Interpolating packed light coordinates
     * at newly clipped vertices does not in general preserve the original triangles' sampled colors.
     */
    public static boolean supportsCurrentTerrainShader(final TerrainGeometry geometry)
    {
        if (geometry == null)
        {
            return false;
        }
        final List<TerrainGeometryPart> parts = geometry.parts();
        for (int partIndex = 0; partIndex < parts.size(); partIndex++)
        {
            final List<TerrainQuad> quads = parts.get(partIndex).quads();
            for (int quadIndex = 0; quadIndex < quads.size(); quadIndex++)
            {
                if (quads.get(quadIndex).clipRegions() != null)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean supports(final TerrainQuad quad)
    {
        if (quad == null || quad.face() == null || quad.material() == null || quad.tintIndex() < -1
            || quad.material().lightEmission() < 0 || quad.material().lightEmission() > 15
            || (quad.material().layer() != ChunkSectionLayer.SOLID
                && quad.material().layer() != ChunkSectionLayer.CUTOUT
                && quad.material().layer() != ChunkSectionLayer.TRANSLUCENT))
        {
            return false;
        }
        float minA = Float.POSITIVE_INFINITY;
        float minB = Float.POSITIVE_INFINITY;
        float maxA = Float.NEGATIVE_INFINITY;
        float maxB = Float.NEGATIVE_INFINITY;
        final Direction.Axis axis = quad.face().getAxis();
        final float plane = coordinate(quad.v0(), axis);
        for (int i = 0; i < 4; i++)
        {
            final TerrainVertex vertex = quad.vertex(i);
            if (vertex == null || !Float.isFinite(vertex.x()) || !Float.isFinite(vertex.y()) || !Float.isFinite(vertex.z())
                || !Float.isFinite(vertex.u()) || !Float.isFinite(vertex.v())
                || vertex.x() < 0 || vertex.x() > 1 || vertex.y() < 0 || vertex.y() > 1 || vertex.z() < 0 || vertex.z() > 1
                || coordinate(vertex, axis) != plane)
            {
                return false;
            }
            minA = Math.min(minA, first(vertex, axis));
            maxA = Math.max(maxA, first(vertex, axis));
            minB = Math.min(minB, second(vertex, axis));
            maxB = Math.max(maxB, second(vertex, axis));
        }
        if (minA == maxA || minB == maxB)
        {
            return false;
        }
        int corners = 0;
        for (int i = 0; i < 4; i++)
        {
            final float a = first(quad.vertex(i), axis);
            final float b = second(quad.vertex(i), axis);
            if ((a != minA && a != maxA) || (b != minB && b != maxB))
            {
                return false;
            }
            final int corner = 1 << ((a == maxA ? 1 : 0) | (b == maxB ? 2 : 0));
            if ((corners & corner) != 0)
            {
                return false;
            }
            corners |= corner;
        }
        final TerrainVertex p = quad.v0();
        final TerrainVertex a = quad.v1();
        final TerrainVertex b = quad.v2();
        final float nx = (a.y() - p.y()) * (b.z() - p.z()) - (a.z() - p.z()) * (b.y() - p.y());
        final float ny = (a.z() - p.z()) * (b.x() - p.x()) - (a.x() - p.x()) * (b.z() - p.z());
        final float nz = (a.x() - p.x()) * (b.y() - p.y()) - (a.y() - p.y()) * (b.x() - p.x());
        return nx * quad.face().getStepX() + ny * quad.face().getStepY() + nz * quad.face().getStepZ() > 0
            && (first(quad.v0(), axis) == first(quad.v1(), axis)
                || second(quad.v0(), axis) == second(quad.v1(), axis))
            && (first(quad.v1(), axis) == first(quad.v2(), axis)
                || second(quad.v1(), axis) == second(quad.v2(), axis))
            && supportsClipRegions(quad, axis, plane, minA, maxA, minB, maxB);
    }

    private static boolean supportsClipRegions(
        final TerrainQuad source, final Direction.Axis axis, final float plane,
        final float minA, final float maxA, final float minB, final float maxB
    )
    {
        final List<TerrainQuad> regions = source.clipRegions();
        if (regions == null)
        {
            return true;
        }
        if (regions.isEmpty())
        {
            return false;
        }
        for (int i = 0; i < regions.size(); i++)
        {
            final TerrainQuad region = regions.get(i);
            if (region.clipRegions() != null || region.face() != source.face()
                || !supports(region) || coordinate(region.v0(), axis) != plane)
            {
                return false;
            }
            final float a0 = Math.min(first(region.v0(), axis), first(region.v2(), axis));
            final float a1 = Math.max(first(region.v0(), axis), first(region.v2(), axis));
            final float b0 = Math.min(second(region.v0(), axis), second(region.v2(), axis));
            final float b1 = Math.max(second(region.v0(), axis), second(region.v2(), axis));
            if (a0 < minA || a1 > maxA || b0 < minB || b1 > maxB)
            {
                return false;
            }
            for (int j = 0; j < i; j++)
            {
                final TerrainQuad other = regions.get(j);
                final float otherA0 = Math.min(first(other.v0(), axis), first(other.v2(), axis));
                final float otherA1 = Math.max(first(other.v0(), axis), first(other.v2(), axis));
                final float otherB0 = Math.min(second(other.v0(), axis), second(other.v2(), axis));
                final float otherB1 = Math.max(second(other.v0(), axis), second(other.v2(), axis));
                if (a0 < otherA1 && a1 > otherA0 && b0 < otherB1 && b1 > otherB0)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static int materialFlags(final TerrainQuad quad)
    {
        int flags = quad.material().layer() == ChunkSectionLayer.TRANSLUCENT ? BakedQuad.FLAG_TRANSLUCENT : 0;
        if (quad.material().sprite() != null && quad.material().sprite().contents().isAnimated())
        {
            flags |= BakedQuad.FLAG_ANIMATED;
        }
        return flags;
    }

    private static float coordinate(final TerrainVertex vertex, final Direction.Axis axis)
    {
        if (vertex == null)
        {
            return Float.NaN;
        }
        return switch (axis) { case X -> vertex.x(); case Y -> vertex.y(); case Z -> vertex.z(); };
    }

    private static float first(final TerrainVertex vertex, final Direction.Axis axis)
    {
        return axis == Direction.Axis.X ? vertex.y() : vertex.x();
    }

    private static float second(final TerrainVertex vertex, final Direction.Axis axis)
    {
        return axis == Direction.Axis.Z ? vertex.y() : vertex.z();
    }
}
