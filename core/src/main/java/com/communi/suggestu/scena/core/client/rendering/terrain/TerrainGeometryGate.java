package com.communi.suggestu.scena.core.client.rendering.terrain;

/**
 * Independent startup gate for experimental native terrain geometry.
 *
 * <p>Enabling native geometry does not enable terrain sharding, and enabling sharding does not enable this path.
 */
public final class TerrainGeometryGate
{
    public static final String PROPERTY = "scena.experimentalNativeTerrainGeometry";

    private TerrainGeometryGate()
    {
    }

    public static boolean isEnabled()
    {
        return Boolean.getBoolean(PROPERTY);
    }
}
