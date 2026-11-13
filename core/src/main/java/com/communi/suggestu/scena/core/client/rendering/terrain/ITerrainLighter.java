package com.communi.suggestu.scena.core.client.rendering.terrain;

import net.minecraft.core.Direction;

/**
 * Lighting implementation supplied by the active terrain renderer.
 *
 * <p>Implementations must use the active renderer's normal light/AO path. A caller must not substitute estimated or
 * fabricated values when a compatible lighter is unavailable.
 */
public interface ITerrainLighter
{
    /**
     * Computes the four vertex light and shade values for a quad.
     *
     * @param face face normal
     * @param vertices four block-local vertices, in the same order used for emission
     * @param shade whether directional shade is enabled
     * @param emission material emission in the renderer's native scale
     * @return renderer-native per-vertex lighting, or {@code null} if this geometry cannot be lit faithfully
     */
    TerrainLight light(Direction face, TerrainVertex[] vertices, boolean shade, int emission);
}
