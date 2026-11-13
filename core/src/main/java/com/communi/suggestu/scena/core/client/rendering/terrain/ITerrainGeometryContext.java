package com.communi.suggestu.scena.core.client.rendering.terrain;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Compile-local context for a terrain geometry model.
 *
 * <p>All values must come from the renderer's compile snapshot. Implementations must not expose a live world to worker
 * threads. A context for an unsupported renderer must report {@link TerrainBackend#UNSUPPORTED}.
 */
public interface ITerrainGeometryContext
{
    BlockAndTintGetter region();

    BlockPos pos();

    BlockState state();

    @Nullable Object blockEntitySnapshot();

    @Nullable ITerrainLighter lighter();

    @Nullable ITerrainQuadSink sink(ChunkSectionLayer layer);

    TerrainBackend backend();

    default boolean supportsBakedModelEmission()
    {
        return false;
    }

    /**
     * Prepares a complete model for native vertex emission without writing any geometry.
     *
     * <p>The model supplies resolved source UVs, vertex ordering, cull groups, material metadata and part AO policy.
     * Returns {@code null} if any part is unsupported. A non-null action is compile-local and one-shot; run it only
     * during emission, never during model preflight. Failures from the action must propagate, not trigger fallback.
     * The initial NeoForge implementation supports solid/cutout models only and uses the live renderer's
     * lighter. Tint indices are resolved against the original compile state, exactly like legacy rendering; callers
     * must preserve that state's tint-index mapping (not substitute indices from another material/model table).
     * Supported part extension contracts must survive preparation; unknown part contracts reject the complete model.
     * Fabric and replacement renderers deliberately return {@code null}.
     */
    default @Nullable Runnable prepareNativeModel(final BlockStateModel model)
    {
        return null;
    }

    /**
     * Preflights complete producer-supplied resolved rectangles without writing or creating baked rectangle models.
     * Returns a compile-local, one-shot emission action, or null when any geometry/backend/material is unsupported.
     * The action must only run during emission; failures must propagate without partial-write fallback.
     */
    default @Nullable Runnable prepareNativeGeometry(final TerrainGeometry geometry)
    {
        return null;
    }

    /**
     * Re-emits a model through the section compiler's original {@code ModelBlockRenderer} call. This correctness-first
     * adapter retains the exact BakedQuad culling, tint, lighting/AO, layer routing, and translucent sorting behavior.
     * It is available only in the vanilla compile hook.
     */
    void emitBakedModel(BlockStateModel model);
}
