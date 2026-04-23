package com.communi.suggestu.scena.fabric.platform.client.rendering.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.processing.ModelQuadLayer;
import com.communi.suggestu.scena.core.util.SingleBlockBlockAndTintGetter;
import com.communi.suggestu.scena.fabric.platform.client.model.unbaked.UnbakedCustomModelWrapper;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FabricModelManager implements IModelManager
{
    private static final FabricModelManager INSTANCE = new FabricModelManager();
    private static final RandomSource       RANDOM   = RandomSource.createThreadLocalInstance(42L);

    public static FabricModelManager getInstance()
    {
        return INSTANCE;
    }

    private final Map<
        MapCodec<? extends BlockStateModel.Unbaked>,
        MapCodec<UnbakedCustomModelWrapper<?>>
        > blockStateModelCodecDelegates = Maps.newConcurrentMap();

    private FabricModelManager()
    {
    }

    @Override
    public void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback)
    {
        callback.accept(new IItemModelPropertyRegistrar()
        {
            @Override
            public void registerRangeProperty(final @NotNull Identifier name, final @NotNull MapCodec<? extends RangeSelectItemModelProperty> property)
            {
                RangeSelectItemModelProperties.ID_MAPPER.put(name, property);
            }

            @Override
            public void registerConditionalProperty(final @NotNull Identifier name, final @NotNull MapCodec<? extends ConditionalItemModelProperty> property)
            {
                ConditionalItemModelProperties.ID_MAPPER.put(name, property);
            }
        });
    }

    @Override
    public Material.Baked getParticleMaterial(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos)
    {
        final FabricBlockStateModel blockStateModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        final BlockAndTintGetter wrapper = new SingleBlockBlockAndTintGetter.Builder()
            .withBlockState(blockState)
            .withBlockEntity(blockEntitySupplier)
            .withPos(pos)
            .withSource(blockAndTintGetter)
            .createSingleBlockBlockAndTintGetter();
        RANDOM.setSeed(blockState.getSeed(pos));

        return blockStateModel.particleMaterial(
            wrapper,
            pos,
            blockState
        );
    }

    @Override
    public @Nullable Object determineModelCacheKey(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos)
    {
        final FabricBlockStateModel blockStateModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        final BlockAndTintGetter wrapper = new SingleBlockBlockAndTintGetter.Builder()
            .withBlockState(blockState)
            .withBlockEntity(blockEntitySupplier)
            .withPos(pos)
            .withSource(blockAndTintGetter)
            .createSingleBlockBlockAndTintGetter();
        RANDOM.setSeed(blockState.getSeed(pos));

        return blockStateModel.createGeometryKey(
            wrapper,
            pos,
            blockState,
            RANDOM
        );
    }

    @Override
    public void extractQuads(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable Direction cullDirection,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos,
        final Consumer<ModelQuadLayer> pipeline)
    {
        final FabricBlockStateModel blockStateModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        final BlockAndTintGetter wrapper = new SingleBlockBlockAndTintGetter.Builder()
            .withBlockState(blockState)
            .withBlockEntity(blockEntitySupplier)
            .withPos(pos)
            .withSource(blockAndTintGetter)
            .createSingleBlockBlockAndTintGetter();
        RANDOM.setSeed(blockState.getSeed(pos));

        var mutableMesh = Renderer.get().mutableMesh();
        blockStateModel.emitQuads(
            mutableMesh.emitter(),
            wrapper,
            pos,
            blockState,
            RANDOM,
            dir -> {
                if (dir == null && cullDirection == null)
                    return false;

                return dir == null || dir != cullDirection;
            }
        );

        mutableMesh.forEachMutable(view -> {
            final BakedQuad quad = view.toBakedQuad(
                Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS)
                    .spriteFinder().find(view)
            );

            final ModelQuadLayer.Builder builder = ModelQuadLayer.Builder.create();

            builder.from(quad);
            builder.sourceQuad(quad);

            pipeline.accept(builder.build());
        });
    }
    public MapCodec<UnbakedCustomModelWrapper<?>> wrapUnbakedModelCodec(final MapCodec<? extends BlockStateModel.Unbaked> platformAgnosticCodec)
    {
        return blockStateModelCodecDelegates.get(platformAgnosticCodec);
    }

    public void registerUnbakedModelCodecWrapping(
        final MapCodec<? extends BlockStateModel.Unbaked> platformAgnostic,
        final MapCodec<UnbakedCustomModelWrapper<?>> platformSpecific
    )
    {
        this.blockStateModelCodecDelegates.put(platformAgnostic, platformSpecific);
    }

    private static TriState toMinecraftTriState(net.fabricmc.fabric.api.util.TriState fabricTriState)
    {
        return switch (fabricTriState)
        {
            case FALSE -> TriState.FALSE;
            case DEFAULT -> TriState.DEFAULT;
            case TRUE -> TriState.TRUE;
        };
    }
}
