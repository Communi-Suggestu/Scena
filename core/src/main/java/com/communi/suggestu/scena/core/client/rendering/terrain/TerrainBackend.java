package com.communi.suggestu.scena.core.client.rendering.terrain;

/**
 * Terrain renderer selected for a geometry emission context.
 *
 * <p>{@link #UNSUPPORTED} is deliberately available so integrations can retain their normal model path instead of
 * guessing which native vertex format is active.
 */
public enum TerrainBackend
{
    VANILLA,
    SODIUM,
    UNSUPPORTED
}
