package com.communi.suggestu.scena.fabric.platform.client.rendering;

import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype.FabricRenderTypeManager;
import com.communi.suggestu.scena.fabric.platform.fluid.FabricFluidManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

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
      final PoseStack.Pose last,
      final VertexConsumer buffer,
      final BlockState defaultBlockState,
      final BakedModel model,
      final float r,
      final float g,
      final float b,
      final int combinedLight,
      final int combinedOverlay,
      final RenderType renderType)
    {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(last, buffer, defaultBlockState, model, r, g, b, combinedLight, combinedOverlay);
    }

    @Override
    public ResourceLocation getFlowingFluidTexture(final FluidInformation fluidInformation)
    {
        return FluidVariantRendering.getSprite(FabricFluidManager.makeVariant(fluidInformation)).getName();
    }

    @Override
    public ResourceLocation getFlowingFluidTexture(final Fluid fluid)
    {
        return getFlowingFluidTexture(new FluidInformation(fluid));
    }

    @Override
    public ResourceLocation getStillFluidTexture(final FluidInformation fluidInformation)
    {
        return FluidVariantRendering.getSprite(FabricFluidManager.makeVariant(fluidInformation)).getName();
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
    public void registerBlockEntityWithoutLevelRenderer(final Item item, final BlockEntityWithoutLevelRenderer renderer)
    {
        BuiltinItemRendererRegistry.INSTANCE.register(
          item,
          renderer::renderByItem
        );
    }

    @Override
    public <T extends BlockEntity> void registerBlockEntityRenderer(final BlockEntityType<? extends T> type, final BlockEntityRendererProvider<T> provider)
    {

    }
}
