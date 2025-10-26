package com.communi.suggestu.scena.forge.platform.client.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.processing.ModelQuadLayer;
import com.communi.suggestu.scena.core.client.utils.LightUtil;
import com.communi.suggestu.scena.core.util.SingleBlockBlockAndTintGetter;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public final class ForgeModelManager implements IModelManager
{
    private static final ForgeModelManager INSTANCE = new ForgeModelManager();
    private static final RandomSource      RANDOM   = Util.make(RandomSource.createNewThreadLocalInstance(), (random) -> random.setSeed(42L));

    public static ForgeModelManager getInstance()
    {
        return INSTANCE;
    }

    private final Collection<Consumer<IItemModelPropertyRegistrar>> modelPropertyRegistrars = Collections.synchronizedCollection(Lists.newArrayList());
    private final AtomicBoolean registeredModelProperties = new AtomicBoolean(false);

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
    public void extractQuads(
        final BlockState blockState,
        final Supplier<@Nullable BlockEntity> blockEntitySupplier,
        @Nullable final Direction cullDirection,
        final @Nullable BlockAndTintGetter blockAndTintGetter, final BlockPos pos, final Consumer<ModelQuadLayer> pipeline)
    {
        final BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        RANDOM.setSeed(blockState.getSeed(pos));
        final List<BlockModelPart> parts = model.collectParts(
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
        for (final BlockModelPart part : parts)
        {
            for (final BakedQuad quad : part.getQuads(cullDirection))
            {
                final ModelQuadLayer.Builder builder = ModelQuadLayer.Builder.create(
                    blockState,
                    part.particleIcon(),
                    part.ambientOcclusion()
                );

                LightUtil.put(builder, quad);

                pipeline.accept(builder.build());
            }
        }
    }

    @SubscribeEvent
    public void onRegisterRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        getInstance().registeredModelProperties.set(true);
        getInstance().modelPropertyRegistrars.forEach(registrar -> registrar.accept(event::register));
    }
}
