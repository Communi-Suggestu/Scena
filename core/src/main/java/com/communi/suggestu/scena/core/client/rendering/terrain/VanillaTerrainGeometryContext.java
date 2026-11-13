package com.communi.suggestu.scena.core.client.rendering.terrain;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compile-local vanilla context whose correctness-first emission path reuses the active ModelBlockRenderer.
 *
 * <p>The general quad/lighter sinks remain absent. Both loaders may supply resolved-geometry preparation;
 * NeoForge additionally supports preparation of already-baked models.
 */
public final class VanillaTerrainGeometryContext implements ITerrainGeometryContext
{
    private final BlockAndTintGetter region;
    private final BlockPos pos;
    private final BlockState state;
    private final Consumer<BlockStateModel> bakedModelEmitter;
    private final Function<BlockStateModel, Runnable> nativeModelPreparer;
    private final Function<TerrainGeometry, Runnable> nativeGeometryPreparer;
    private boolean emitted;

    public VanillaTerrainGeometryContext(
        final BlockAndTintGetter region,
        final BlockPos pos,
        final BlockState state,
        final Consumer<BlockStateModel> bakedModelEmitter
    )
    {
        this(region, pos, state, bakedModelEmitter, model -> null, geometry -> null);
    }

    public VanillaTerrainGeometryContext(
        final BlockAndTintGetter region,
        final BlockPos pos,
        final BlockState state,
        final Consumer<BlockStateModel> bakedModelEmitter,
        final Function<BlockStateModel, Runnable> nativeModelPreparer
    )
    {
        this(region, pos, state, bakedModelEmitter, nativeModelPreparer, geometry -> null);
    }

    public VanillaTerrainGeometryContext(
        final BlockAndTintGetter region,
        final BlockPos pos,
        final BlockState state,
        final Consumer<BlockStateModel> bakedModelEmitter,
        final Function<BlockStateModel, Runnable> nativeModelPreparer,
        final Function<TerrainGeometry, Runnable> nativeGeometryPreparer
    )
    {
        this.region = Objects.requireNonNull(region, "region");
        this.pos = Objects.requireNonNull(pos, "pos").immutable();
        this.state = Objects.requireNonNull(state, "state");
        this.bakedModelEmitter = Objects.requireNonNull(bakedModelEmitter, "bakedModelEmitter");
        this.nativeModelPreparer = Objects.requireNonNull(nativeModelPreparer, "nativeModelPreparer");
        this.nativeGeometryPreparer = Objects.requireNonNull(nativeGeometryPreparer, "nativeGeometryPreparer");
    }

    @Override
    public BlockAndTintGetter region()
    {
        return region;
    }

    @Override
    public BlockPos pos()
    {
        return pos;
    }

    @Override
    public BlockState state()
    {
        return state;
    }

    @Override
    public Object blockEntitySnapshot()
    {
        return null;
    }

    @Override
    public ITerrainLighter lighter()
    {
        return null;
    }

    @Override
    public ITerrainQuadSink sink(final net.minecraft.client.renderer.chunk.ChunkSectionLayer layer)
    {
        return null;
    }

    @Override
    public TerrainBackend backend()
    {
        return TerrainBackend.VANILLA;
    }

    @Override
    public boolean supportsBakedModelEmission()
    {
        return true;
    }

    @Override
    public Runnable prepareNativeModel(final BlockStateModel model)
    {
        ensureNotEmitted();
        final Runnable prepared = nativeModelPreparer.apply(Objects.requireNonNull(model, "model"));
        if (prepared == null)
        {
            return null;
        }
        return () -> {
            ensureNotEmitted();
            emitted = true;
            prepared.run();
        };
    }

    @Override
    public Runnable prepareNativeGeometry(final TerrainGeometry geometry)
    {
        ensureNotEmitted();
        if (!TerrainGeometryValidation.supports(geometry))
        {
            return null;
        }
        final Runnable prepared = nativeGeometryPreparer.apply(geometry);
        if (prepared == null)
        {
            return null;
        }
        return () -> {
            ensureNotEmitted();
            emitted = true;
            prepared.run();
        };
    }

    @Override
    public void emitBakedModel(final BlockStateModel model)
    {
        ensureNotEmitted();
        emitted = true;
        bakedModelEmitter.accept(Objects.requireNonNull(model, "model"));
    }

    private void ensureNotEmitted()
    {
        if (emitted)
        {
            throw new IllegalStateException("A terrain geometry context may emit only once");
        }
    }
}
