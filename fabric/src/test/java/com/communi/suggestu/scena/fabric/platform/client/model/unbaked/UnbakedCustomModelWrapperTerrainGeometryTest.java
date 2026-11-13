package com.communi.suggestu.scena.fabric.platform.client.model.unbaked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import com.communi.suggestu.scena.core.client.rendering.terrain.ITerrainGeometryContext;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainGeometryBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import org.junit.jupiter.api.Test;

class UnbakedCustomModelWrapperTerrainGeometryTest
{
    @Test
    void forwardsOptInAndEmissionToWrappedModel()
    {
        final AtomicInteger emissionCount = new AtomicInteger();
        final BlockStateModel inner = (BlockStateModel) Proxy.newProxyInstance(
            BlockStateModel.class.getClassLoader(),
            new Class<?>[] {BlockStateModel.class, TerrainGeometryBlockStateModel.class},
            (proxy, method, args) -> switch (method.getName())
            {
                case "canEmitTerrainGeometry" -> true;
                case "emitTerrainGeometry" ->
                {
                    emissionCount.incrementAndGet();
                    yield null;
                }
                default -> null;
            }
        );
        final UnbakedCustomModelWrapper.DataAware wrapper = new UnbakedCustomModelWrapper.DataAware(inner);
        final ITerrainGeometryContext context = null;

        assertTrue(wrapper.canEmitTerrainGeometry(context));
        wrapper.emitTerrainGeometry(context);
        assertEquals(1, emissionCount.get());
    }

    @Test
    void doesNotOptInWhenWrappedModelDoesNot()
    {
        final BlockStateModel inner = (BlockStateModel) Proxy.newProxyInstance(
            BlockStateModel.class.getClassLoader(),
            new Class<?>[] {BlockStateModel.class},
            (proxy, method, args) -> null
        );
        final UnbakedCustomModelWrapper.DataAware wrapper = new UnbakedCustomModelWrapper.DataAware(inner);

        assertFalse(wrapper.canEmitTerrainGeometry(null));
    }
}
