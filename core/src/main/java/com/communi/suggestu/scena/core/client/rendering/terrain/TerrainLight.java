package com.communi.suggestu.scena.core.client.rendering.terrain;

/**
 * Per-vertex lighting for one quad. Values are ordered to match its four vertices.
 */
public record TerrainLight(
    int light0, int light1, int light2, int light3,
    int color0, int color1, int color2, int color3
)
{
}
