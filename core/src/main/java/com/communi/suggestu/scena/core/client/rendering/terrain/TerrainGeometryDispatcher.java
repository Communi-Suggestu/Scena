package com.communi.suggestu.scena.core.client.rendering.terrain;

import java.util.Objects;

/**
 * Fallback-first dispatch helper for section compiler integrations.
 */
public final class TerrainGeometryDispatcher
{
    private TerrainGeometryDispatcher()
    {
    }

    /**
     * Emits custom geometry only when the separate experimental gate is enabled and the model has explicitly
     * preflighted the complete context. Otherwise the ordinary model path runs unchanged.
     *
     * <p>Emission failures are not swallowed: falling back after a partial native write could duplicate or corrupt
     * geometry.
     *
     * @return true only when the opt-in model emitted its complete geometry
     */
    public static boolean emitOrFallback(
        final TerrainGeometryBlockStateModel model,
        final ITerrainGeometryContext context,
        final Runnable fallback
    )
    {
        Objects.requireNonNull(fallback, "fallback");
        if (!TerrainGeometryGate.isEnabled()
            || model == null
            || context == null
            || context.backend() == TerrainBackend.UNSUPPORTED)
        {
            fallback.run();
            return false;
        }

        final Object preflight = model.preflightTerrainGeometry(context);
        if (preflight == null)
        {
            fallback.run();
            return false;
        }

        model.emitTerrainGeometry(context, preflight);
        return true;
    }
}
