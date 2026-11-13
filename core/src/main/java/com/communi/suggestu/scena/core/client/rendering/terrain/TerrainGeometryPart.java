package com.communi.suggestu.scena.core.client.rendering.terrain;

import java.util.List;

import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * An ordered source part's AO policy, appearance and material flags, with immutable resolved rectangles.
 */
public record TerrainGeometryPart(
    TriState ambientOcclusion, @Nullable BlockState appearance, int materialFlags, List<TerrainQuad> quads
)
{
    public TerrainGeometryPart
    {
        quads = List.copyOf(quads);
    }
}
