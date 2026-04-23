package com.communi.suggestu.scena.forge.platform.client.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.processing.DeconstructedModelPartComponent;
import com.communi.suggestu.scena.core.util.SingleBlockBlockAndTintGetter;
import com.communi.suggestu.scena.forge.platform.client.model.unbaked.UnbakedCustomModelWrapper;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public final class ForgeModelManager implements IModelManager
{
    private static final ForgeModelManager INSTANCE = new ForgeModelManager();
    private static final RandomSource      RANDOM   = RandomSource.createThreadLocalInstance(42L);

    public static ForgeModelManager getInstance()
    {
        return INSTANCE;
    }

    private final Collection<Consumer<IItemModelPropertyRegistrar>> modelPropertyRegistrars = Collections.synchronizedCollection(Lists.newArrayList());
    private final AtomicBoolean registeredModelProperties = new AtomicBoolean(false);

    private final Map<
        MapCodec<? extends BlockStateModel.Unbaked>,
        MapCodec<UnbakedCustomModelWrapper<?>>
        > blockStateModelCodecDelegates = Maps.newConcurrentMap();

    private ForgeModelManager()
    {
    }

    @Override
    public void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback)
    {
        if (registeredModelProperties.get()) {
            throw new IllegalStateException("Cannot register item model property after model loading has started.");
        }

        modelPropertyRegistrars.add(callback);
    }

    @Override
    public Material.Baked getParticleMaterial(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos)
    {
        final BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        RANDOM.setSeed(blockState.getSeed(pos));
        return model.particleMaterial(
            new SingleBlockBlockAndTintGetter.Builder()
                .withBlockState(blockState)
                .withBlockEntity(blockEntitySupplier)
                .withPos(pos)
                .withSource(blockAndTintGetter)
                .createSingleBlockBlockAndTintGetter(),
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
        final BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        RANDOM.setSeed(blockState.getSeed(pos));
        return model.createGeometryKey(
            new SingleBlockBlockAndTintGetter.Builder()
                .withBlockState(blockState)
                .withBlockEntity(blockEntitySupplier)
                .withPos(pos)
                .withSource(blockAndTintGetter)
                .createSingleBlockBlockAndTintGetter(),
            pos,
            blockState,
            RANDOM
        );
    }

    @Override
    public void extractQuads(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        @Nullable final Direction cullDirection,
        final @Nullable BlockAndTintGetter blockAndTintGetter,
        final BlockPos pos,
        final Consumer<DeconstructedModelPartComponent> pipeline)
    {
        final BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        RANDOM.setSeed(blockState.getSeed(pos));

        final List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(
            new SingleBlockBlockAndTintGetter.Builder()
                .withBlockState(blockState)
                .withBlockEntity(blockEntitySupplier)
                .withPos(pos)
                .withSource(blockAndTintGetter)
                .createSingleBlockBlockAndTintGetter(),
            pos,
            blockState,
            RANDOM,
            parts
        );
        for (final BlockStateModelPart part : parts)
        {
            for (final BakedQuad quad : part.getQuads(cullDirection))
            {
                pipeline.accept(
                    DeconstructedModelPartComponent.Builder
                        .create(part, quad)
                        .build()
                );
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        getInstance().registeredModelProperties.set(true);
        getInstance().modelPropertyRegistrars.forEach(registrar -> registrar.accept(new IItemModelPropertyRegistrar() {
            @Override
            public void registerRangeProperty(final @NotNull Identifier name, final @NotNull MapCodec<? extends RangeSelectItemModelProperty> property)
            {
                event.register(name, property);
            }

            @Override
            public void registerConditionalProperty(final @NotNull Identifier name, final @NotNull MapCodec<? extends ConditionalItemModelProperty> property)
            {
                //Noop
            }
        }));
    }

    @SubscribeEvent
    public static void onRegisterConditionalSelectItemModelProperty(RegisterConditionalItemModelPropertyEvent event) {
        getInstance().registeredModelProperties.set(true);
        getInstance().modelPropertyRegistrars.forEach(registrar -> registrar.accept(new IItemModelPropertyRegistrar() {
            @Override
            public void registerRangeProperty(final @NotNull Identifier name, final @NotNull MapCodec<? extends RangeSelectItemModelProperty> property)
            {
                //Noop
            }

            @Override
            public void registerConditionalProperty(final @NotNull Identifier name, final @NotNull MapCodec<? extends ConditionalItemModelProperty> property)
            {
                event.register(name, property);
            }
        }));
    }

    @Nullable
    public MapCodec<UnbakedCustomModelWrapper<?>> wrapUnbakedModelCodec(final MapCodec<? extends BlockStateModel.Unbaked> platformAgnosticCodec)
    {
        return blockStateModelCodecDelegates.get(platformAgnosticCodec);
    }

    public void registerUnbakedModelCodecWrapping(
        final Identifier location, final MapCodec<? extends BlockStateModel.Unbaked> platformAgnostic,
        final MapCodec<UnbakedCustomModelWrapper<?>> platformSpecific
    ) {
        //Right now DataGen needs both platform type mappers. So lets add it on the coApply.
        this.blockStateModelCodecDelegates.put(platformAgnostic, platformSpecific
            .mapResult(new MapCodec.ResultFunction<>()
            {
                @Override
                public <T> DataResult<UnbakedCustomModelWrapper<?>> apply(final DynamicOps<T> ops, final MapLike<T> input, final DataResult<UnbakedCustomModelWrapper<?>> a)
                {
                    return a;
                }

                @Override
                public <T> RecordBuilder<T> coApply(final DynamicOps<T> ops, final UnbakedCustomModelWrapper<?> input, final RecordBuilder<T> t)
                {
                    t.add("fabric:type", ops.createString(location.toString()));
                    return t;
                }
            })
        );
    }
}
