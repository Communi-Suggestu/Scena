package com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.util.SingleBlockBlockAndTintGetter;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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
        if (!(blockStateModel instanceof FabricBlockStateModel fabricBlockStateModel))
            return Collections.emptyList();

        final BlockAndTintGetter wrapper = new SingleBlockBlockAndTintGetter.Builder()
            .withBlockState(state)
            .withPos(position)
            .withBlockEntity(blockEntitySupplier)
            .withSource(blockAndTintGetter)
            .createSingleBlockBlockAndTintGetter();
        RANDOM.setSeed(state.getSeed(position));
        fabricBlockStateModel.emitQuads(
            new QuadView(
                layer -> {
                    if (layer != null)
                        layers.add(layer);
                }
            ),
            wrapper,
            position,
            state,
            RANDOM,
            dir -> {
                return false;
            }
        );
        return layers;
    }

    @Override
    public @NotNull Collection<RenderType> getRenderTypesFor(final ItemModel model, final ItemStack stack, final boolean isFabulous)
    {
        return List.of(ItemBlockRenderTypes.getRenderType(stack));
    }

    private static final class QuadView extends MutableQuadViewImpl
    {
        private final Consumer<@Nullable ChunkSectionLayer> pipeline;

        private QuadView(
            final Consumer<@Nullable ChunkSectionLayer> pipeline
        ) {
            this.pipeline = pipeline;

            this.data = new int[EncodingFormat.TOTAL_STRIDE];
        }

        @Override
        protected void emitDirectly()
        {
            pipeline.accept(renderLayer());
        }
    }

}
