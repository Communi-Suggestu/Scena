package com.communi.suggestu.scena.core.client.rendering.terrain;

import java.util.List;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Resolved block-local rectangle. UVs and vertex order are supplied by the source model, never inferred from a sprite.
 * The tint index addresses the original compile state's color provider, independently of the source material index.
 * A non-null {@code clipRegions} lists disjoint visible subrectangles; the original is lit before clipping.
 */
public record TerrainQuad(
    TerrainVertex v0, TerrainVertex v1, TerrainVertex v2, TerrainVertex v3,
    Direction face, @Nullable Direction cullFace, BakedQuad.MaterialInfo material, int tintIndex,
    @Nullable List<TerrainQuad> clipRegions
)
{
    public TerrainQuad(
        final TerrainVertex v0, final TerrainVertex v1, final TerrainVertex v2, final TerrainVertex v3,
        final Direction face, final @Nullable Direction cullFace, final BakedQuad.MaterialInfo material, final int tintIndex
    )
    {
        this(v0, v1, v2, v3, face, cullFace, material, tintIndex, null);
    }

    public TerrainQuad
    {
        clipRegions = clipRegions == null ? null : List.copyOf(clipRegions);
    }

    public TerrainVertex vertex(final int index)
    {
        return switch (index)
        {
            case 0 -> v0;
            case 1 -> v1;
            case 2 -> v2;
            case 3 -> v3;
            default -> throw new IndexOutOfBoundsException(index);
        };
    }
}
