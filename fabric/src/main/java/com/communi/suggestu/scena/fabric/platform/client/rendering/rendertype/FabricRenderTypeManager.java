package com.communi.suggestu.scena.fabric.platform.client.rendering.rendertype;

import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.util.SingleBlockBlockAndTintGetter;
import com.communi.suggestu.scena.fabric.mixin.platform.client.FabricBlockStateModelMixin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
        final FabricBlockStateModelMixin blockStateModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        final BlockAndTintGetter wrapper = new SingleBlockBlockAndTintGetter.Builder()
            .withBlockState(state)
            .withPos(position)
            .withBlockEntity(blockEntitySupplier)
            .withSource(blockAndTintGetter)
            .createSingleBlockBlockAndTintGetter();
        RANDOM.setSeed(state.getSeed(position));
        blockStateModel.emitQuads(
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

    @SuppressWarnings("UnstableApiUsage")
    private static final class QuadView extends MutableQuadViewImpl
    {
        private final Consumer<@Nullable ChunkSectionLayer> pipeline;

        @SuppressWarnings("UnstableApiUsage")
        private QuadView(
            final Consumer<@Nullable ChunkSectionLayer> pipeline
        ) {
            this.pipeline = pipeline;

            this.data = new int[EncodingFormat.TOTAL_STRIDE];
        }

        @SuppressWarnings({"UnstableApiUsage"})
        @Override
        protected void emitDirectly()
        {
            pipeline.accept(renderLayer());
        }
    }

}
