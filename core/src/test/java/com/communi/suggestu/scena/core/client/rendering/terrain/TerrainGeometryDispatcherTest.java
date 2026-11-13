package com.communi.suggestu.scena.core.client.rendering.terrain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TerrainGeometryDispatcherTest
{
    private final String priorProperty = System.getProperty(TerrainGeometryGate.PROPERTY);

    @AfterEach
    void restoreProperty()
    {
        if (priorProperty == null)
        {
            System.clearProperty(TerrainGeometryGate.PROPERTY);
        }
        else
        {
            System.setProperty(TerrainGeometryGate.PROPERTY, priorProperty);
        }
    }

    @Test
    void disabledGateKeepsOrdinaryModelPath()
    {
        System.clearProperty(TerrainGeometryGate.PROPERTY);
        final AtomicInteger fallbackCalls = new AtomicInteger();
        final AtomicInteger emissions = new AtomicInteger();

        final boolean emitted = TerrainGeometryDispatcher.emitOrFallback(
            model(emissions, true),
            context(TerrainBackend.VANILLA),
            fallbackCalls::incrementAndGet
        );

        assertFalse(emitted);
        assertEquals(1, fallbackCalls.get());
        assertEquals(0, emissions.get());
    }

    @Test
    void unsupportedBackendKeepsOrdinaryModelPath()
    {
        System.setProperty(TerrainGeometryGate.PROPERTY, "true");
        final AtomicInteger fallbackCalls = new AtomicInteger();
        final AtomicInteger emissions = new AtomicInteger();

        final boolean emitted = TerrainGeometryDispatcher.emitOrFallback(
            model(emissions, true),
            context(TerrainBackend.UNSUPPORTED),
            fallbackCalls::incrementAndGet
        );

        assertFalse(emitted);
        assertEquals(1, fallbackCalls.get());
        assertEquals(0, emissions.get());
    }

    @Test
    void failedPreflightKeepsOrdinaryModelPath()
    {
        System.setProperty(TerrainGeometryGate.PROPERTY, "true");
        final AtomicInteger fallbackCalls = new AtomicInteger();
        final AtomicInteger emissions = new AtomicInteger();

        final boolean emitted = TerrainGeometryDispatcher.emitOrFallback(
            model(emissions, false),
            context(TerrainBackend.VANILLA),
            fallbackCalls::incrementAndGet
        );

        assertFalse(emitted);
        assertEquals(1, fallbackCalls.get());
        assertEquals(0, emissions.get());
    }

    @Test
    void supportedPreflightEmitsWithoutInvokingFallback()
    {
        System.setProperty(TerrainGeometryGate.PROPERTY, "true");
        final AtomicInteger fallbackCalls = new AtomicInteger();
        final AtomicInteger emissions = new AtomicInteger();

        final boolean emitted = TerrainGeometryDispatcher.emitOrFallback(
            model(emissions, true),
            context(TerrainBackend.VANILLA),
            fallbackCalls::incrementAndGet
        );

        assertTrue(emitted);
        assertEquals(0, fallbackCalls.get());
        assertEquals(1, emissions.get());
    }

    @Test
    void emissionReceivesTheExactPreflightSnapshot()
    {
        System.setProperty(TerrainGeometryGate.PROPERTY, "true");
        final Object preparedSnapshot = new Object();
        final AtomicReference<Object> receivedSnapshot = new AtomicReference<>();
        final AtomicInteger fallbackCalls = new AtomicInteger();
        final TerrainGeometryBlockStateModel model = new TerrainGeometryBlockStateModel()
        {
            @Override
            public boolean canEmitTerrainGeometry(final ITerrainGeometryContext context)
            {
                return false;
            }

            @Override
            public Object preflightTerrainGeometry(final ITerrainGeometryContext context)
            {
                return preparedSnapshot;
            }

            @Override
            public void emitTerrainGeometry(final ITerrainGeometryContext context)
            {
                throw new AssertionError("The dispatcher must pass the prepared snapshot");
            }

            @Override
            public void emitTerrainGeometry(final ITerrainGeometryContext context, final Object preflight)
            {
                receivedSnapshot.set(preflight);
            }
        };

        final boolean emitted = TerrainGeometryDispatcher.emitOrFallback(
            model,
            context(TerrainBackend.VANILLA),
            fallbackCalls::incrementAndGet
        );

        assertTrue(emitted);
        assertEquals(0, fallbackCalls.get());
        assertEquals(preparedSnapshot, receivedSnapshot.get());
    }

    private static TerrainGeometryBlockStateModel model(final AtomicInteger emissions, final boolean canEmit)
    {
        return new TerrainGeometryBlockStateModel()
        {
            @Override
            public boolean canEmitTerrainGeometry(final ITerrainGeometryContext context)
            {
                return canEmit;
            }

            @Override
            public void emitTerrainGeometry(final ITerrainGeometryContext context)
            {
                emissions.incrementAndGet();
            }
        };
    }

    private static ITerrainGeometryContext context(final TerrainBackend backend)
    {
        return new ITerrainGeometryContext()
        {
            @Override
            public net.minecraft.client.renderer.block.BlockAndTintGetter region()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public net.minecraft.core.BlockPos pos()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public net.minecraft.world.level.block.state.BlockState state()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object blockEntitySnapshot()
            {
                return null;
            }

            @Override
            public ITerrainLighter lighter()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public ITerrainQuadSink sink(final net.minecraft.client.renderer.chunk.ChunkSectionLayer layer)
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public TerrainBackend backend()
            {
                return backend;
            }

            @Override
            public void emitBakedModel(final net.minecraft.client.renderer.block.dispatch.BlockStateModel model)
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
