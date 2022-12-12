package com.communi.suggestu.scena.core.client.rendering;

import com.communi.suggestu.scena.core.client.IClientManager;
import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

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
     *
     * @param last The current position matrix.
     * @param buffer The buffer to render into.
     * @param defaultBlockState The blockstate to render the model for.
     * @param model The model to render.
     * @param r The r color channel to apply.
     * @param g The g color channel to apply.
     * @param b The b color channel to apply-
     * @param combinedLight The combined light value to render.
     * @param combinedOverlay The combined overlay value to render.
     * @param renderType The render type to render the model in.
     */
    void renderModel(
      PoseStack.Pose last,
      VertexConsumer buffer,
      BlockState defaultBlockState,
      BakedModel model,
      float r,
      float g,
      float b,
      int combinedLight,
      int combinedOverlay,
      RenderType renderType);

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
     * Registers a callback which can register a new {@link BlockEntityWithoutLevelRenderer} for a specific item.
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
     * A registrar for the {@link BlockEntityWithoutLevelRenderer}s.
     */
    interface IBlockEntityWithoutLevelRendererRegistrar {
        /**
         * Registers a new {@link BlockEntityWithoutLevelRenderer} for a specific item.
         *
         * @param item The item to register the {@link BlockEntityWithoutLevelRenderer} for.
         * @param renderer The {@link BlockEntityWithoutLevelRenderer} to register.
         */
        void registerBlockEntityWithoutLevelRenderer(final Item item, final BlockEntityWithoutLevelRenderer renderer);
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
}
