package com.communi.suggestu.scena.fabric.platform.client.rendering;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.FabricModelManager;
import com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype.FabricRenderTypeManager;
import com.communi.suggestu.scena.fabric.platform.client.tooltip.ClientTooltipComponentConverterRegistry;
import com.communi.suggestu.scena.fabric.platform.fluid.FabricFluidManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
import net.fabricmc.fabric.api.renderer.v1.render.FabricBlockRenderManager;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class FabricRenderingManager implements IRenderingManager
{
    private static final FabricRenderingManager INSTANCE = new FabricRenderingManager();
    
    public static FabricRenderingManager getInstance() {
        return INSTANCE;
    }

    private FabricRenderingManager() {
    }

    @Override
    public void renderModel(
        final PoseStack matrices,
        final MultiBufferSource source,
        final BlockStateModel blockStateModel,
        final float r,
        final float g,
        final float b,
        final int combinedLight,
        final int combinedOverlay,
        final BlockAndTintGetter level,
        final BlockPos blockPos,
        final BlockState blockState)
    {
        final var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        if (!(blockRenderer instanceof FabricBlockRenderManager fabricBlockRenderManager))
            return;

        //TODO: Figure out what to do with rendering the colors!
        fabricBlockRenderManager.renderBlockAsEntity(
            blockState,
            matrices,
            source,
            combinedLight,
            combinedOverlay,
            level,
            blockPos
        );
    }

    @Override
    public ResourceLocation getFlowingFluidTexture(final FluidInformation fluidInformation)
    {
        return FluidVariantRendering.getSprite(FabricFluidManager.makeVariant(fluidInformation)).contents().name();
    }

    @Override
    public ResourceLocation getFlowingFluidTexture(final Fluid fluid)
    {
        return getFlowingFluidTexture(new FluidInformation(fluid));
    }

    @Override
    public ResourceLocation getStillFluidTexture(final FluidInformation fluidInformation)
    {
        return FluidVariantRendering.getSprite(FabricFluidManager.makeVariant(fluidInformation)).contents().name();
    }

    @Override
    public ResourceLocation getStillFluidTexture(final Fluid fluid)
    {
        return getFlowingFluidTexture(new FluidInformation(fluid));
    }

    @Override
    public @NotNull IRenderTypeManager getRenderTypeManager()
    {
        return FabricRenderTypeManager.getInstance();
    }

    @Override
    public void registerClientTooltipComponentConverter(Consumer<IClientTooltipComponentConverterRegistrar> callback) {
        callback.accept(ClientTooltipComponentConverterRegistry.getInstance()::register);
    }

    @Override
    public void registerBlockEntityWithoutLevelRenderer(final Consumer<IBlockEntityWithoutLevelRendererRegistrar> callback)
    {
        callback.accept((name, renders, defaultUnbaked) -> {
            for (final Block render : renders)
            {
                SpecialBlockRendererRegistry.register(render, defaultUnbaked);
            }
        });
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
