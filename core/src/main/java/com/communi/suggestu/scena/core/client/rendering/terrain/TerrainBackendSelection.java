package com.communi.suggestu.scena.core.client.rendering.terrain;

/**
 * Keeps vanilla-only terrain hooks out of renderers that replace the vanilla section compiler.
 */
public final class TerrainBackendSelection
{
    private TerrainBackendSelection()
    {
    }

    public static boolean enableVanillaHook(final boolean requested, final boolean sodiumDetected)
    {
        return requested && !sodiumDetected;
    }
}
