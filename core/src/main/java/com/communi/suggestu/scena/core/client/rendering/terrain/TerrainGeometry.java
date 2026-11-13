package com.communi.suggestu.scena.core.client.rendering.terrain;

import java.util.List;

/**
 * Complete immutable producer output for one block. Part ordering is significant to the active loader's AO policy.
 */
public record TerrainGeometry(List<TerrainGeometryPart> parts)
{
    public TerrainGeometry
    {
        parts = List.copyOf(parts);
    }
}
