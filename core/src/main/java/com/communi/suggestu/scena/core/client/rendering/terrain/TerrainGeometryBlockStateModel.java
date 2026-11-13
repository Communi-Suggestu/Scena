package com.communi.suggestu.scena.core.client.rendering.terrain;

import org.jetbrains.annotations.Nullable;

/**
 * Opt-in contract for models that can emit fully faithful geometry into a native terrain section mesh.
 *
 * <p>{@link #preflightTerrainGeometry(ITerrainGeometryContext)} must be side-effect-free and return {@code null} for
 * any model, material, layer, dynamic data or renderer case that is not completely supported. A non-null result is
 * passed unchanged to emission so implementations can bind preflight to one immutable compile snapshot.
 * {@link #emitTerrainGeometry(ITerrainGeometryContext, Object)} must emit the complete prepared model; an incomplete
 * emission must never be reported as success. Both supported loader adapters accept resolved rectangles through
 * {@link ITerrainGeometryContext#prepareNativeGeometry}; NeoForge additionally accepts already-baked models through
 * {@link ITerrainGeometryContext#prepareNativeModel}.
 */
public interface TerrainGeometryBlockStateModel
{
    boolean canEmitTerrainGeometry(ITerrainGeometryContext context);

    void emitTerrainGeometry(ITerrainGeometryContext context);

    default @Nullable Object preflightTerrainGeometry(final ITerrainGeometryContext context)
    {
        return canEmitTerrainGeometry(context) ? this : null;
    }

    default void emitTerrainGeometry(final ITerrainGeometryContext context, final Object preflight)
    {
        emitTerrainGeometry(context);
    }
}
