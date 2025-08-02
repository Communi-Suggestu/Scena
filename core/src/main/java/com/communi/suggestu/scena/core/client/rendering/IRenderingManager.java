package com.communi.suggestu.scena.core.client.rendering;

import com.communi.suggestu.scena.core.client.IClientManager;
import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.client.tooltip.IClientTooltipComponentConverter;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
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
     * Renders a specific blockstate on the given position.
     */
    void renderModel(
        PoseStack matrices,
        MultiBufferSource source,
        BlockStateModel blockStateModel,
        float r,
        float g,
        float b,
        int combinedLight,
        int combinedOverlay,
        BlockAndTintGetter level,
        BlockPos blockPos,
        BlockState blockState);

    /**
     * Gains access to the texture that is used to render a flowing fluid.
     *
     * @param fluidInformation The fluid to get the texture for.
     * @return The texture.
     */
    ResourceLocation getFlowingFluidTexture(final FluidInformation fluidInformation);

    /**
     * Gains access to the texture that is used to render a flowing fluid.
     *
     * @param fluid The fluid to get the texture for.
     * @return The texture.
     */
    ResourceLocation getFlowingFluidTexture(final Fluid fluid);

    /**
     * Gains access to the texture that is used to render a still fluid.
     *
     * @param fluidInformation The fluid to get the texture for.
     * @return The texture.
     */
    ResourceLocation getStillFluidTexture(final FluidInformation fluidInformation);

    /**
     * Gains access to the texture that is used to render a still fluid.
     *
     * @param fluid The fluid to get the texture for.
     * @return The texture.
     */
    ResourceLocation getStillFluidTexture(final Fluid fluid);

    /**
     * The render type manager.
     * Deals with the render types which are available on different platforms.
     *
     * @return The render type manager.
     */
    @NotNull
    IRenderTypeManager getRenderTypeManager();

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
    void registerBlockEntityWithoutLevelRenderer(final Consumer<IBlockEntityWithoutLevelRendererRegistrar> callback);

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
    interface IBlockEntityWithoutLevelRendererRegistrar {
        void registerBlockEntityWithoutLevelRenderer(final ResourceLocation name, Set<Block> renders, final SpecialModelRenderer.Unbaked defaultUnbaked);
    }

    interface IBlockEntityRendererRegistrar {
        /**
         * Registers a new {@link BlockEntityRendererProvider} for a specific {@link BlockEntityType}.
         *
         * @param type The type to register the provider for.
         * @param provider The provider.
         * @param <T> The type of the block entity.
         */
        <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider);
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
