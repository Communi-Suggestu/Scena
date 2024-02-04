package com.communi.suggestu.scena.forge.platform.client.rendering;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.forge.platform.client.model.ForgeModelManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ForgeRenderingManager implements IRenderingManager
{
    private static final ForgeRenderingManager INSTANCE = new ForgeRenderingManager();

    public static ForgeRenderingManager getInstance()
    {
        return INSTANCE;
    }

    private final List<Consumer<IBlockEntityRendererRegistrar>> blockEntityRegistrars = Collections.synchronizedList(Lists.newArrayList());
    private final List<Consumer<IBlockEntityWithoutLevelRendererRegistrar>> blockEntityWithoutLevelRegistrars = Collections.synchronizedList(Lists.newArrayList());
    private final AtomicBoolean registered = new AtomicBoolean(false);
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
    public void registerBlockEntityWithoutLevelRenderer(final Consumer<IBlockEntityWithoutLevelRendererRegistrar> callback)
    {
        if (registered.get())
        {
            throw new IllegalStateException("Cannot register a block entity without level renderer after the client setup event has been fired.");
        }

        blockEntityWithoutLevelRegistrars.add(callback);
    }

    @Override
    public void registerBlockEntityRenderer(final Consumer<IBlockEntityRendererRegistrar> callback)
    {
        if (registered.get())
        {
            throw new IllegalStateException("Cannot register a block entity renderer after the client setup event has been fired.");
        }

        blockEntityRegistrars.add(callback);
    }

    @SubscribeEvent
    public static void onRegisterBlockEntityWithoutLevelRenderers(final FMLClientSetupEvent event)
    {
        getInstance().registered.set(true);
        getInstance().blockEntityWithoutLevelRegistrars.forEach(callback -> callback.accept((item, renderer) -> getInstance().bewlrs.put(item, renderer)));
        getInstance().blockEntityRegistrars.forEach(callback -> callback.accept(BlockEntityRenderers::register));
    }

    @Override
    public IModelManager getModelManager()
    {
        return ForgeModelManager.getInstance();
    }

    @Override
    public int adaptVertexColor(int color) {
        return color;
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
