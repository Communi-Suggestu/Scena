package com.communi.suggestu.scena.fabric.platform.client.rendering.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.processing.ModelQuadLayer;
import com.communi.suggestu.scena.core.client.utils.LightUtil;
import com.communi.suggestu.scena.core.util.SingleBlockBlockAndTintGetter;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
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
    private static final RandomSource       RANDOM   = Util.make(RandomSource.createNewThreadLocalInstance(), (random) -> random.setSeed(42L));

    public static FabricModelManager getInstance()
    {
        return INSTANCE;
    }

    private final Map<
            MapCodec<? extends BlockStateModel.Unbaked>,
            MapCodec<? extends BlockStateModel.Unbaked>
        > blockStateModelCodecDelegates = Maps.newConcurrentMap();

    private FabricModelManager()
    {
    }

    @Override
    public void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback)
    {
        callback.accept(new IItemModelPropertyRegistrar() {
            @Override
            public void registerRangeProperty(final @NotNull ResourceLocation name, final @NotNull MapCodec<? extends RangeSelectItemModelProperty> property)
            {
                RangeSelectItemModelProperties.ID_MAPPER.put(name, property);
            }

            @Override
            public void registerConditionalProperty(final @NotNull ResourceLocation name, final @NotNull MapCodec<? extends ConditionalItemModelProperty> property)
            {
                ConditionalItemModelProperties.ID_MAPPER.put(name, property);
            }
        });
    }

    @Override
    public TextureAtlasSprite getParticleTexture(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos)
    {
        final FabricBlockStateModel blockStateModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        final BlockAndTintGetter wrapper = new SingleBlockBlockAndTintGetter.Builder()
            .withBlockState(blockState)
            .withBlockEntity(blockEntitySupplier)
            .withPos(pos)
            .withSource(blockAndTintGetter)
            .createSingleBlockBlockAndTintGetter();
        RANDOM.setSeed(blockState.getSeed(pos));

        return blockStateModel.particleSprite(
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
        final FabricBlockStateModel blockStateModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
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

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void extractQuads(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable Direction cullDirection,
        final @Nullable BlockAndTintGetter blockAndTintGetter, final BlockPos pos, final Consumer<ModelQuadLayer> pipeline)
    {
        final FabricBlockStateModel blockStateModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        final BlockAndTintGetter wrapper = new SingleBlockBlockAndTintGetter.Builder()
            .withBlockState(blockState)
            .withBlockEntity(blockEntitySupplier)
            .withPos(pos)
            .withSource(blockAndTintGetter)
            .createSingleBlockBlockAndTintGetter();
        RANDOM.setSeed(blockState.getSeed(pos));
        blockStateModel.emitQuads(
            new MutableQuadViewImpl() {
                @SuppressWarnings({"UnstableApiUsage", "deprecation"})
                @Override
                protected void emitDirectly()
                {
                    final BakedQuad quad = toBakedQuad(
                        Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS)
                            .spriteFinder().find(this)
                    );

                    final ModelQuadLayer.Builder builder = ModelQuadLayer.Builder.create(
                        blockState,
                        blockStateModel.particleSprite(
                            wrapper,
                            pos,
                            blockState
                        ),
                        toMinecraftTriState(ambientOcclusion())
                    );

                    LightUtil.put(builder, quad);

                    pipeline.accept(builder.build());
                }
            },
            wrapper,
            pos,
            blockState,
            RANDOM,
            dir -> dir == cullDirection
        );
    }

    @Override
    public MapCodec<? extends BlockStateModel.Unbaked> wrapUnbakedModelCodec(final MapCodec<? extends BlockStateModel.Unbaked> platformAgnosticCodec)
    {
        return blockStateModelCodecDelegates.getOrDefault(platformAgnosticCodec, platformAgnosticCodec);
    }

    public void registerUnbakedModelCodecWrapping(
        final MapCodec<? extends BlockStateModel.Unbaked> platformAgnostic,
        final MapCodec<? extends BlockStateModel.Unbaked> platformSpecific
    ) {
        this.blockStateModelCodecDelegates.put(platformAgnostic, platformSpecific);
    }

    private static TriState toMinecraftTriState(net.fabricmc.fabric.api.util.TriState fabricTriState) {
        return switch (fabricTriState) {
            case FALSE -> TriState.FALSE;
            case DEFAULT -> TriState.DEFAULT;
            case TRUE -> TriState.TRUE;
        };
    }
}
