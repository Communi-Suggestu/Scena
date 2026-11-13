package com.communi.suggestu.scena.core.client.rendering.terrain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

class VanillaTerrainGeometryContextTest
{
    @Test
    void resolvedPreflightIsWriteFreeWholeBlockAndSharesFailureLifecycle()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        final BlockAndTintGetter region = (BlockAndTintGetter) Proxy.newProxyInstance(
            BlockAndTintGetter.class.getClassLoader(), new Class<?>[] {BlockAndTintGetter.class},
            (proxy, method, args) -> null
        );
        final AtomicInteger preparations = new AtomicInteger();
        final AtomicInteger writes = new AtomicInteger();
        final RuntimeException failure = new RuntimeException("Partial resolved write");
        final VanillaTerrainGeometryContext context = new VanillaTerrainGeometryContext(
            region, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), ignored -> {}, ignored -> null,
            ignored -> {
                preparations.incrementAndGet();
                return () -> { writes.incrementAndGet(); throw failure; };
            }
        );
        final var valid = TerrainGeometryValidationTest.quad(net.minecraft.client.renderer.chunk.ChunkSectionLayer.SOLID);
        final var invalid = TerrainGeometryValidationTest.quad(net.minecraft.client.renderer.chunk.ChunkSectionLayer.TRANSLUCENT);
        assertNull(context.prepareNativeGeometry(TerrainGeometryValidationTest.geometry(java.util.List.of(valid, invalid))));
        assertEquals(0, preparations.get());
        final Runnable prepared = context.prepareNativeGeometry(TerrainGeometryValidationTest.geometry(java.util.List.of(valid)));
        assertEquals(1, preparations.get());
        assertEquals(0, writes.get());
        assertSame(failure, assertThrows(RuntimeException.class, prepared::run));
        assertThrows(IllegalStateException.class, prepared::run);
        assertThrows(IllegalStateException.class, () -> context.prepareNativeGeometry(TerrainGeometryValidationTest.geometry(java.util.List.of(valid))));
        assertEquals(1, writes.get());
    }

    @Test
    void nativePreparationDoesNotWriteAndSharesOneShotLifecycleWithBakedEmission()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        final BlockAndTintGetter region = (BlockAndTintGetter) Proxy.newProxyInstance(
            BlockAndTintGetter.class.getClassLoader(), new Class<?>[] {BlockAndTintGetter.class},
            (proxy, method, args) -> null
        );
        final BlockStateModel model = (BlockStateModel) Proxy.newProxyInstance(
            BlockStateModel.class.getClassLoader(), new Class<?>[] {BlockStateModel.class},
            (proxy, method, args) -> null
        );
        final AtomicInteger writes = new AtomicInteger();
        final VanillaTerrainGeometryContext context = new VanillaTerrainGeometryContext(
            region, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), ignored -> writes.incrementAndGet(),
            ignored -> writes::incrementAndGet
        );
        final Runnable first = context.prepareNativeModel(model);
        final Runnable second = context.prepareNativeModel(model);
        assertEquals(0, writes.get());
        first.run();
        assertEquals(1, writes.get());
        assertThrows(IllegalStateException.class, first::run);
        assertThrows(IllegalStateException.class, second::run);
        assertThrows(IllegalStateException.class, () -> context.emitBakedModel(model));
        assertThrows(IllegalStateException.class, () -> context.prepareNativeModel(model));
    }

    @Test
    void failedNativeEmissionCannotRetryOrFallBack()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        final BlockAndTintGetter region = (BlockAndTintGetter) Proxy.newProxyInstance(
            BlockAndTintGetter.class.getClassLoader(), new Class<?>[] {BlockAndTintGetter.class},
            (proxy, method, args) -> null
        );
        final BlockStateModel model = (BlockStateModel) Proxy.newProxyInstance(
            BlockStateModel.class.getClassLoader(), new Class<?>[] {BlockStateModel.class},
            (proxy, method, args) -> null
        );
        final RuntimeException failure = new RuntimeException("Partial native write");
        final VanillaTerrainGeometryContext context = new VanillaTerrainGeometryContext(
            region, BlockPos.ZERO, Blocks.AIR.defaultBlockState(), ignored -> {},
            ignored -> () -> { throw failure; }
        );
        final Runnable prepared = context.prepareNativeModel(model);
        assertSame(failure, assertThrows(RuntimeException.class, prepared::run));
        assertThrows(IllegalStateException.class, prepared::run);
        assertThrows(IllegalStateException.class, () -> context.emitBakedModel(model));
    }

    @Test
    void exposesCompileSnapshotAndAllowsOneBakedModelEmission()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        final BlockAndTintGetter region = (BlockAndTintGetter) Proxy.newProxyInstance(
            BlockAndTintGetter.class.getClassLoader(),
            new Class<?>[] {BlockAndTintGetter.class},
            (proxy, method, args) -> null
        );
        final BlockPos pos = new BlockPos(4, 5, 6);
        final var state = Blocks.AIR.defaultBlockState();
        final AtomicReference<BlockStateModel> emittedModel = new AtomicReference<>();
        final VanillaTerrainGeometryContext context = new VanillaTerrainGeometryContext(region, pos, state, emittedModel::set);
        final BlockStateModel model = (BlockStateModel) Proxy.newProxyInstance(
            BlockStateModel.class.getClassLoader(),
            new Class<?>[] {BlockStateModel.class},
            (proxy, method, args) -> null
        );

        assertSame(region, context.region());
        assertSame(state, context.state());
        assertEquals(pos, context.pos());
        assertSame(TerrainBackend.VANILLA, context.backend());
        assertTrue(context.supportsBakedModelEmission());
        assertNull(context.sink(null));
        assertNull(context.lighter());
        assertNull(context.prepareNativeModel(model));

        context.emitBakedModel(model);
        assertSame(model, emittedModel.get());
        assertThrows(IllegalStateException.class, () -> context.emitBakedModel(model));
    }
}
