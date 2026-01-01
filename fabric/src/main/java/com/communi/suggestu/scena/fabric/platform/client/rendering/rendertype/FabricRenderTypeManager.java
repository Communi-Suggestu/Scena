package com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.util.SingleBlockBlockAndTintGetter;
import com.communi.suggestu.scena.fabric.mixin.platform.client.FabricBlockStateModelMixin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricRenderTypeManager implements IRenderTypeManager
{
    private static final FabricRenderTypeManager INSTANCE = new FabricRenderTypeManager();
    private static final RandomSource       RANDOM   = Util.make(RandomSource.createNewThreadLocalInstance(), (random) -> random.setSeed(42L));

    public static FabricRenderTypeManager getInstance()
    {
        return INSTANCE;
    }

    @Override
    public boolean canRenderInType(final BlockState blockState, final ChunkSectionLayer renderType)
    {
        return ItemBlockRenderTypes.getChunkRenderType(blockState) == renderType;
    }

    @Override
    public boolean canRenderInType(final FluidState fluidState, final ChunkSectionLayer renderType)
    {
        return ItemBlockRenderTypes.getRenderLayer(fluidState) == renderType;
    }

    private FabricRenderTypeManager()
    {
    }

    @Override
    public void registerBlockFallbackRenderTypes(final Consumer<IFallbackBlockRenderTypeRegistrar> consumer)
    {
        consumer.accept(BlockRenderLayerMap::putBlock);
    }

    @Override
    public @NotNull Collection<ChunkSectionLayer> getRenderTypesFor(
        final BlockAndTintGetter blockAndTintGetter,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final BlockPos position,
        final BlockState state)
    {
        final EnumSet<ChunkSectionLayer> layers = EnumSet.noneOf(ChunkSectionLayer.class);
        final BlockStateModel blockStateModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        final BlockAndTintGetter wrapper = new SingleBlockBlockAndTintGetter.Builder()
            .withBlockState(state)
            .withPos(position)
            .withBlockEntity(blockEntitySupplier)
            .withSource(blockAndTintGetter)
            .createSingleBlockBlockAndTintGetter();
        RANDOM.setSeed(state.getSeed(position));

        var mutableMesh = Renderer.get().mutableMesh();
        blockStateModel.emitQuads(
            mutableMesh.emitter(),
            wrapper,
            position,
            state,
            RANDOM,
            dir -> false
        );

        mutableMesh.forEachMutable(view -> {
            if (view.renderLayer() != null) {
                layers.add(view.renderLayer());
            }
        });

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

        return types;
    }

}
