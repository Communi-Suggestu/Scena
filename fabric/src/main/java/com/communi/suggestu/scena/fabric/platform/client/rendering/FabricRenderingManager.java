package com.communi.suggestu.scena.fabric.platform.client.rendering;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.FabricModelManager;
import com.communi.suggestu.scena.fabric.platform.client.tooltip.ClientTooltipComponentConverterRegistry;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import java.util.function.Consumer;

public final class FabricRenderingManager implements IRenderingManager
{
    private static final FabricRenderingManager INSTANCE = new FabricRenderingManager();

    public static final Event<Consumer<ISpecialModelRendererRegistrar>> ON_SPECIAL_MODEL_BAKE = EventFactory.createArrayBacked(
        Consumer.class,
        handlers -> (r) -> {
            for (final Consumer<ISpecialModelRendererRegistrar> handler : handlers)
            {
                handler.accept(r);
            }
        }
    );

    public static FabricRenderingManager getInstance() {
        return INSTANCE;
    }

    private FabricRenderingManager() {
    }

    @Override
    public void registerClientTooltipComponentConverter(Consumer<IClientTooltipComponentConverterRegistrar> callback) {
        callback.accept(ClientTooltipComponentConverterRegistry.getInstance()::register);
    }

    @Override
    public void registerSpecialModelRenderer(final Consumer<ISpecialModelRendererRegistrar> callback)
    {
        ON_SPECIAL_MODEL_BAKE.register(callback);
    }

    @Override
    public void registerBlockEntityRenderer(final Consumer<IBlockEntityRendererRegistrar> callback)
    {
        callback.accept(BlockEntityRenderers::register);
    }

    @Override
    public IModelManager getModelManager()
    {
        return FabricModelManager.getInstance();
    }

    @Override
    public int adaptVertexColor(int color) {
        final int a = (color >> 24) & 0xFF;
        final int r = (color >> 16) & 0xFF;
        final int g = (color >> 8) & 0xFF;
        final int b = color & 0xFF;

        return (a << 24) | (b << 16) | (g << 8) | r;
    }
}
