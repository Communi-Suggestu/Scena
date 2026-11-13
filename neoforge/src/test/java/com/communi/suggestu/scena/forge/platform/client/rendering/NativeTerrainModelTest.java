package com.communi.suggestu.scena.forge.platform.client.rendering;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

import com.communi.suggestu.scena.core.client.rendering.ExtendedBlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class NativeTerrainModelTest
{
    @Test
    void snapshotsAllCullGroupsAndPartAoWithoutEmissionOrTintRemapping()
    {
        final BakedQuad tinted = quad(ChunkSectionLayer.CUTOUT, 42);
        final List<BakedQuad> mutableQuads = new ArrayList<>(List.of(tinted));
        final BlockStateModel model = model(List.of(
            part(TriState.FALSE, mutableQuads), part(TriState.TRUE, List.of(quad(ChunkSectionLayer.SOLID, -1)))
        ));
        final AtomicReference<BlockStateModel> emitted = new AtomicReference<>();
        final Runnable prepared = NativeTerrainModel.prepare(model, null, BlockPos.ZERO, null, 123, emitted::set);
        assertNotNull(prepared);
        assertNull(emitted.get());
        mutableQuads.clear();
        prepared.run();
        final List<BlockStateModelPart> parts = new ArrayList<>();
        emitted.get().collectParts(RandomSource.create(123), parts);
        assertEquals(2, parts.size());
        assertEquals(TriState.FALSE, parts.getFirst().ambientOcclusion());
        assertEquals(TriState.TRUE, parts.getLast().ambientOcclusion());
        assertFalse(parts.getFirst() instanceof ExtendedBlockStateModelPart);
        assertSame(tinted, parts.getFirst().getQuads(null).getFirst());
        assertEquals(42, parts.getFirst().getQuads(Direction.NORTH).getFirst().materialInfo().tintIndex());
        assertEquals(7, parts.getFirst().getQuads(Direction.UP).getFirst().materialInfo().lightEmission());
    }

    @Test
    void preservesExtendedPartIdentityContractAoAndNullableAppearanceSnapshot()
    {
        final ExtendedPart original = new ExtendedPart(part(
            TriState.TRUE, List.of(quad(ChunkSectionLayer.SOLID, 0))
        ));
        final AtomicReference<BlockStateModel> emitted = new AtomicReference<>();
        final Runnable prepared = NativeTerrainModel.prepare(
            model(List.of(original)), null, BlockPos.ZERO, null, 123, emitted::set
        );
        assertNotNull(prepared);
        assertEquals(1, original.appearanceReads.get());
        prepared.run();
        final List<BlockStateModelPart> parts = new ArrayList<>();
        emitted.get().collectParts(RandomSource.create(123), parts);
        final ExtendedBlockStateModelPart extended = assertInstanceOf(ExtendedBlockStateModelPart.class, parts.getFirst());
        assertEquals(TriState.TRUE, parts.getFirst().ambientOcclusion());
        assertNull(extended.getBlockAppearance());
        assertEquals(1, original.appearanceReads.get());
    }

    @Test
    void rejectsUnknownInheritedPartContractsBeforeReadingTheirGeometry()
    {
        final AtomicReference<BlockStateModel> emitted = new AtomicReference<>();
        final ExtendedPart unsupported = new ForeignPart(part(TriState.TRUE, List.of()));
        assertNull(NativeTerrainModel.prepare(
            model(List.of(part(TriState.DEFAULT, List.of(quad(ChunkSectionLayer.SOLID, -1))), unsupported)),
            null, BlockPos.ZERO, null, 123, emitted::set
        ));
        assertEquals(0, unsupported.appearanceReads.get());
        assertNull(emitted.get());
    }

    @Test
    void acceptsRecordPartsWithTheSameContractsAsChiseledBlockModelPart()
    {
        final BlockStateModelPart original = new RecordPart(part(
            TriState.TRUE, List.of(quad(ChunkSectionLayer.SOLID, 0))
        ));
        final AtomicReference<BlockStateModel> emitted = new AtomicReference<>();
        final Runnable prepared = NativeTerrainModel.prepare(
            model(List.of(original)), null, BlockPos.ZERO, null, 123, emitted::set
        );
        assertNotNull(prepared);
        prepared.run();
        final List<BlockStateModelPart> parts = new ArrayList<>();
        emitted.get().collectParts(RandomSource.create(123), parts);
        assertInstanceOf(ExtendedBlockStateModelPart.class, parts.getFirst());
        assertEquals(TriState.TRUE, parts.getFirst().ambientOcclusion());
    }

    @Test
    void acceptsWholeModelWithTranslucentPartForSectionCompilerSorting()
    {
        final AtomicReference<BlockStateModel> emitted = new AtomicReference<>();
        final BlockStateModel model = model(List.of(
            part(TriState.TRUE, List.of(quad(ChunkSectionLayer.SOLID, -1))),
            part(TriState.DEFAULT, List.of(quad(ChunkSectionLayer.TRANSLUCENT, -1)))
        ));
        final Runnable prepared = NativeTerrainModel.prepare(model, null, BlockPos.ZERO, null, 123, emitted::set);
        assertNotNull(prepared);
        prepared.run();
        assertNotNull(emitted.get(), "Translucent vertices are written to the compiler's sorted translucent layer");
    }

    static BlockStateModel model(final List<BlockStateModelPart> parts)
    {
        return new BlockStateModel()
        {
            public void collectParts(final RandomSource random, final List<BlockStateModelPart> output)
            {
                output.addAll(parts);
            }

            public Material.Baked particleMaterial()
            {
                return null;
            }

            public int materialFlags()
            {
                return 0;
            }
        };
    }

    static BlockStateModelPart part(final TriState ao, final List<BakedQuad> quads)
    {
        return new BlockStateModelPart()
        {
            public List<BakedQuad> getQuads(final Direction direction)
            {
                return quads;
            }

            public boolean useAmbientOcclusion()
            {
                return ao != TriState.FALSE;
            }

            public TriState ambientOcclusion()
            {
                return ao;
            }

            public Material.Baked particleMaterial()
            {
                return null;
            }

            public int materialFlags()
            {
                return 0;
            }
        };
    }

    static BakedQuad quad(final ChunkSectionLayer layer, final int tint)
    {
        return new BakedQuad(
            new Vector3f(0, 0, 1), new Vector3f(0, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 0, 1),
            0, 0, 0, 0, Direction.SOUTH, new BakedQuad.MaterialInfo(null, layer, null, tint, true, 7, false)
        );
    }

    private static class ExtendedPart implements BlockStateModelPart, ExtendedBlockStateModelPart
    {
        private final BlockStateModelPart delegate;
        final AtomicInteger appearanceReads = new AtomicInteger();

        private ExtendedPart(final BlockStateModelPart delegate)
        {
            this.delegate = delegate;
        }

        public BlockState getBlockAppearance()
        {
            appearanceReads.incrementAndGet();
            return null;
        }

        public List<BakedQuad> getQuads(final Direction direction) { return delegate.getQuads(direction); }
        public TriState ambientOcclusion() { return delegate.ambientOcclusion(); }
        public boolean useAmbientOcclusion() { return delegate.useAmbientOcclusion(); }
        public Material.Baked particleMaterial() { return delegate.particleMaterial(); }
        public int materialFlags() { return delegate.materialFlags(); }
    }

    private interface ForeignMetadata
    {
    }

    private record RecordPart(BlockStateModelPart delegate) implements BlockStateModelPart, ExtendedBlockStateModelPart
    {
        public BlockState getBlockAppearance() { return null; }
        public List<BakedQuad> getQuads(final Direction direction) { return delegate.getQuads(direction); }
        public TriState ambientOcclusion() { return delegate.ambientOcclusion(); }
        public boolean useAmbientOcclusion() { return delegate.useAmbientOcclusion(); }
        public Material.Baked particleMaterial() { return delegate.particleMaterial(); }
        public int materialFlags() { return delegate.materialFlags(); }
    }

    private static final class ForeignPart extends ExtendedPart implements ForeignMetadata
    {
        private ForeignPart(final BlockStateModelPart delegate)
        {
            super(delegate);
        }

        @Override
        public List<BakedQuad> getQuads(final Direction direction)
        {
            throw new AssertionError("Unsupported part geometry must not be read");
        }
    }
}
