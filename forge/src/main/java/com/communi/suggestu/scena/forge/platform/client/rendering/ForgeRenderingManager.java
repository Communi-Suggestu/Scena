package com.communi.suggestu.scena.forge.platform.client.rendering;

import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class ForgeRenderingManager implements IRenderingManager
{
    private static final ForgeRenderingManager INSTANCE = new ForgeRenderingManager();

    public static ForgeRenderingManager getInstance()
    {
        return INSTANCE;
    }

    private final Map<Item, BlockEntityWithoutLevelRenderer> bewlrs = Maps.newConcurrentMap();

    private ForgeRenderingManager()
    {
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
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(last, buffer, defaultBlockState, model, r, g, b, combinedLight, combinedOverlay, ModelData.EMPTY, renderType);
    }

    @Override
    public ResourceLocation getFlowingFluidTexture(final FluidInformation fluidInformation)
    {
        return IClientFluidTypeExtensions.of(fluidInformation.fluid())
                .getFlowingTexture(buildFluidStack(fluidInformation));
    }

    @Override
    public ResourceLocation getFlowingFluidTexture(final Fluid fluid)
    {
        return IClientFluidTypeExtensions.of(fluid)
                       .getFlowingTexture();
    }

    @Override
    public ResourceLocation getStillFluidTexture(final FluidInformation fluidInformation)
    {
        return IClientFluidTypeExtensions.of(fluidInformation.fluid())
                                         .getStillTexture(buildFluidStack(fluidInformation));
    }

    @Override
    public ResourceLocation getStillFluidTexture(final Fluid fluid)
    {
        return IClientFluidTypeExtensions.of(fluid)
                                         .getStillTexture();
    }

    @Override
    public @NotNull IRenderTypeManager getRenderTypeManager()
    {
        return ForgeRenderTypeManager.getInstance();
    }

    @Override
    public void registerBlockEntityWithoutLevelRenderer(final Item item, final BlockEntityWithoutLevelRenderer renderer)
    {
        this.bewlrs.put(item, renderer);
    }

    public Optional<BlockEntityWithoutLevelRenderer> getRenderer(final Item item) {
        return Optional.ofNullable(this.bewlrs.get(item));
    }

    @NotNull
    private FluidStack buildFluidStack(final FluidInformation fluid)
    {
        if (fluid.data() == null)
            return new FluidStack(fluid.fluid(), (int) fluid.amount());

        return new FluidStack(fluid.fluid(), (int) fluid.amount(), fluid.data());
    }
}
