package com.communi.suggestu.scena.core.client.rendering;

import com.communi.suggestu.scena.core.client.IClientManager;
import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.tooltip.IClientTooltipComponentConverter;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;

/**
 * Gives access to the platforms specific rendering tasks.
 */
public interface IRenderingManager
{

    /**
     * Gives access to the clients rendering manager.
     *
     * @return The client rendering manager.
     */
    static IRenderingManager getInstance() {
        return IClientManager.getInstance().getRenderingManager();
    }

    /**
     * Registers a callback which can register a new {@link IClientTooltipComponentConverter} for the current platform.
     *
     * @param callback The callback that registers the converter.
     */
    void registerClientTooltipComponentConverter(final Consumer<IClientTooltipComponentConverterRegistrar> callback);

    /**
     * Registers a callback which can register a new {@link SpecialModelRenderer} for a specific item.
     *
     * @param callback The callback that registers the renderer.
     */
    void registerSpecialModelRenderer(final Consumer<ISpecialModelRendererRegistrar> callback);

    /**
     * Registers a callback which can register a new {@link BlockEntityRendererProvider} for a specific {@link BlockEntityType}.
     *
     * @param callback The callback that registers the renderer.
     */
    void registerBlockEntityRenderer(final Consumer<IBlockEntityRendererRegistrar> callback);

    /**
     * Gives access to this platform's model manager.
     *
     * @return The model manager
     */
    IModelManager getModelManager();

    /**
     * Adapts the given vertex color so that it can be used on this platform.
     *
     * @param color The color to adapt in default minecraft format ARGB.
     * @return The adapted color.
     */
    int adaptVertexColor(final int color);

    /**
     * A registrar for the {@link SpecialModelRenderer}s.
     */
    interface ISpecialModelRendererRegistrar
    {
        /**
         * Invoke to register a new codec to instantiate a given renderer.
         *
         * @param name The name to use.
         * @param source The codec to instantiate with.
         */
        void register(final Identifier name, final MapCodec<? extends SpecialModelRenderer.Unbaked<?>> source);
    }

    interface IBlockEntityRendererRegistrar {
        /**
         * Registers a new {@link BlockEntityRendererProvider} for a specific {@link BlockEntityType}.
         *
         * @param type The type to register the provider for.
         * @param provider The provider.
         * @param <T> The type of the block entity.
         */
        <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T, S> provider);
    }

    interface IClientTooltipComponentConverterRegistrar {

        /**
         * Registers a new {@link IClientTooltipComponentConverter} for the current platform.
         *
         * @param tooltipComponentType The type of {@link net.minecraft.world.inventory.tooltip.TooltipComponent} this converter can convert.
         * @param converter The converter to register.
         */
        <T extends TooltipComponent> void registerConvert(final Class<T> tooltipComponentType, final IClientTooltipComponentConverter converter);
    }
}
