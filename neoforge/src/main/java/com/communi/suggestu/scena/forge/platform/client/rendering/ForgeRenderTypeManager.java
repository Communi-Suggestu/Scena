package com.communi.suggestu.scena.forge.platform.client.rendering;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ForgeRenderTypeManager implements IRenderTypeManager
{
    private static final RandomSource       RANDOM   = Util.make(RandomSource.createNewThreadLocalInstance(), (random) -> random.setSeed(42L));
    private static final ForgeRenderTypeManager INSTANCE = new ForgeRenderTypeManager();

    public static ForgeRenderTypeManager getInstance()
    {
        return INSTANCE;
    }

    private final AtomicBoolean registeredRenderTypes = new AtomicBoolean(false);
    private final List<Consumer<IFallbackBlockRenderTypeRegistrar>> fallbackBlockRenderTypeRegistrars = Collections.synchronizedList(Lists.newArrayList());

    private ForgeRenderTypeManager()
    {
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canRenderInType(final BlockState blockState, final ChunkSectionLayer renderType)
    {
        var partsList = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(blockState)
            .collectParts(RANDOM);

        for (final BlockModelPart blockModelPart : partsList)
        {
            if (blockModelPart.getRenderType(blockState) == renderType)
                return true;
        }

        return false;
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final ChunkSectionLayer renderType)
    {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onClientInit(final FMLClientSetupEvent clientSetupEvent) {
        getInstance().registeredRenderTypes.set(true);

        for (final Consumer<IFallbackBlockRenderTypeRegistrar> fallbackBlockRenderTypeRegistrar : getInstance().fallbackBlockRenderTypeRegistrars)
        {
            fallbackBlockRenderTypeRegistrar.accept(ItemBlockRenderTypes::setRenderLayer);
        }
    }

    @Override
    public void registerBlockFallbackRenderTypes(final Consumer<IFallbackBlockRenderTypeRegistrar> consumer)
    {
        if (registeredRenderTypes.get())
        {
            throw new IllegalStateException("Cannot register fallback render types after they have been registered.");
        }

        fallbackBlockRenderTypeRegistrars.add(consumer);
    }

    @Override
    public @NotNull Collection<ChunkSectionLayer> getRenderTypesFor(
        final BlockAndTintGetter blockAndTintGetter,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final BlockPos position,
        final BlockState state)
    {
        final var model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
        RANDOM.setSeed(state.getSeed(position));
        var parts = model.collectParts(blockAndTintGetter, position, state, RANDOM);
        var layers = EnumSet.noneOf(ChunkSectionLayer.class);

        for (final BlockModelPart part : parts)
        {
            layers.add(part.getRenderType(state));
        }

        return layers;
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final ItemModel model, final ItemStack stack, final boolean isFabulous)
    {
        var state = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().appendItemLayers(
            state,
            stack,
            ItemDisplayContext.NONE,
            null,
            null,
            0
        );

        var types = new HashSet<RenderType>();
        for (final ItemStackRenderState.LayerRenderState layer : state.layers)
        {
            if (layer.renderType != null)
                types.add(layer.renderType);
        }

        if (types.isEmpty())
            return List.of(ItemBlockRenderTypes.getRenderType(stack));

        return types;
    }
}
