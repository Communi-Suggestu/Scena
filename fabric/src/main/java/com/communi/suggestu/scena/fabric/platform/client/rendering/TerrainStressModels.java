package com.communi.suggestu.scena.fabric.platform.client.rendering;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Dev-only stress model used to push a section layer past the sharding upload budget.
 * Registers a repeating emitter for netherite blocks when {@code scena.terrainStress} is set.
 * A plain netherite block emits six quads; with {@code scena.terrainStress.repeats} copies
 * a 16x16x16 section of netherite emits over 300k quads, past the 32 MiB builder limit.
 */
public final class TerrainStressModels
{
    public static final String STRESS_PROPERTY = "scena.terrainStress";
    private static final int REPEATS = Integer.getInteger("scena.terrainStress.repeats", 200);
    private static final Logger LOGGER = LogUtils.getLogger();

    private TerrainStressModels()
    {
    }
    private static int LOGGED;

    private static final AtomicLong EMISSIONS = new AtomicLong();

    public static int repeats()
    {
        return REPEATS;
    }

    public static long emissions()
    {
        return EMISSIONS.get();
    }

    public static boolean enabled()
    {
        return Boolean.getBoolean(STRESS_PROPERTY);
    }

    public static void register()
    {
        if (!enabled())
        {
            return;
        }

        ModelLoadingPlugin.register(context -> {
            LOGGER.info("Terrain stress plugin registered for {} repeats per netherite block", REPEATS);
            context.modifyBlockModelAfterBake().register((model, bakeContext) -> {
                if (LOGGED < 5)
                {
                    LOGGED++;
                    LOGGER.info("Baked block model {} for {}", model.getClass().getName(), bakeContext.state());
                }
                if (bakeContext.state().getBlock() == Blocks.NETHERITE_BLOCK && model instanceof FabricBlockStateModel fabricModel)
                {
                    LOGGER.info("Stress-wrapped netherite model {}", model.getClass().getName());
                    return new StressWrapped(fabricModel);
                }
                return model;
            });
        });
    }

    private record StressWrapped(FabricBlockStateModel inner) implements BlockStateModel, FabricBlockStateModel
    {
        @Override
        public void emitQuads(final QuadEmitter emitter, final BlockAndTintGetter level, final BlockPos pos,
                              final BlockState state, final RandomSource random,
                              final Predicate<@Nullable Direction> cullTest)
        {
            if (EMISSIONS.getAndIncrement() == 0)
            {
                LOGGER.info("Stress emission first call at {} in {}", pos, level.getClass().getName());
            }
            for (int repeat = 1; repeat < REPEATS; repeat++)
            {
                EMISSIONS.incrementAndGet();
                inner().emitQuads(emitter, level, pos, state, random, cullTest);
            }
            inner().emitQuads(emitter, level, pos, state, random, cullTest);
        }

        @Override
        public @Nullable Object createGeometryKey(final BlockAndTintGetter level, final BlockPos pos,
                                                  final BlockState state, final RandomSource random)
        {
            return inner().createGeometryKey(level, pos, state, random);
        }

        @Override
        public Material.@Nullable Baked particleMaterial(final BlockAndTintGetter level, final BlockPos pos,
                                                         final BlockState state)
        {
            return inner().particleMaterial(level, pos, state);
        }

        @Override
        public int materialFlags(final BlockAndTintGetter level, final BlockPos pos, final BlockState state,
                                 final RandomSource random)
        {
            return inner().materialFlags(level, pos, state, random);
        }

        @Override
        public void collectParts(final RandomSource random, final List<BlockStateModelPart> output)
        {
            ((BlockStateModel) inner()).collectParts(random, output);
        }

        @Override
        public Material.Baked particleMaterial()
        {
            return ((BlockStateModel) inner()).particleMaterial();
        }

        @Override
        public int materialFlags()
        {
            return ((BlockStateModel) inner()).materialFlags();
        }
    }
}
