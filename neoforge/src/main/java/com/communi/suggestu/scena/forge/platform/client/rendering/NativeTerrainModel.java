package com.communi.suggestu.scena.forge.platform.client.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.communi.suggestu.scena.core.client.rendering.terrain.NativeTerrainQuadWriter;
import com.communi.suggestu.scena.core.client.rendering.ExtendedBlockStateModelPart;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.BlockStateModelPartExtension;
import org.jetbrains.annotations.Nullable;

/**
 * Complete, write-free preflight. The frozen parts are subsequently lit by the compiler's live ModelBlockRenderer.
 */
public final class NativeTerrainModel
{
    private static final Set<Class<?>> SUPPORTED_PART_INTERFACES = Set.of(
        BlockStateModelPart.class, BlockStateModelPartExtension.class, ExtendedBlockStateModelPart.class
    );

    private NativeTerrainModel()
    {
    }

    public static @Nullable Runnable prepare(
        final BlockStateModel model,
        final BlockAndTintGetter region,
        final BlockPos pos,
        final BlockState state,
        final long seed,
        final Consumer<BlockStateModel> emitter
    )
    {
        final List<BlockStateModelPart> source = new ArrayList<>();
        model.collectParts(region, pos, state, RandomSource.createThreadLocalInstance(seed), source);
        final List<BlockStateModelPart> frozen = new ArrayList<>(source.size());
        for (final BlockStateModelPart part : source)
        {
            if (!supportsPartType(part.getClass()))
            {
                return null;
            }
            final List<List<BakedQuad>> groups = new ArrayList<>(7);
            for (int group = 0; group < 7; group++)
            {
                final List<BakedQuad> quads = List.copyOf(part.getQuads(group == 6 ? null : Direction.values()[group]));
                if (!quads.stream().allMatch(NativeTerrainQuadWriter::supports))
                {
                    return null;
                }
                groups.add(quads);
            }
            final TriState ao = part.ambientOcclusion();
            final boolean legacyAo = part.useAmbientOcclusion();
            final Material.Baked particle = part.particleMaterial();
            final int flags = part.materialFlags();
            frozen.add(part instanceof ExtendedBlockStateModelPart extended
                ? new FrozenExtendedPart(groups, ao, legacyAo, particle, flags, extended.getBlockAppearance())
                : new FrozenPart(groups, ao, legacyAo, particle, flags));
        }
        final Material.Baked particle = model.particleMaterial(region, pos, state);
        final int flags = model.materialFlags(region, pos, state);
        final BlockStateModel snapshot = new BlockStateModel()
        {
            @Override
            public void collectParts(final RandomSource random, final List<BlockStateModelPart> output)
            {
                output.addAll(frozen);
            }

            @Override
            public Material.Baked particleMaterial()
            {
                return particle;
            }

            @Override
            public int materialFlags()
            {
                return flags;
            }
        };
        return () -> emitter.accept(snapshot);
    }

    private static boolean supportsPartType(final Class<?> type)
    {
        for (Class<?> current = type; current != null; current = current.getSuperclass())
        {
            for (final Class<?> contract : current.getInterfaces())
            {
                if (!SUPPORTED_PART_INTERFACES.contains(contract) || !supportsPartType(contract))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static class FrozenPart implements BlockStateModelPart
    {
        private final List<List<BakedQuad>> groups;
        private final TriState ao;
        private final boolean legacyAo;
        private final Material.Baked particle;
        private final int flags;

        private FrozenPart(
            final List<List<BakedQuad>> groups, final TriState ao, final boolean legacyAo,
            final Material.Baked particle, final int flags
        )
        {
            this.groups = groups;
            this.ao = ao;
            this.legacyAo = legacyAo;
            this.particle = particle;
            this.flags = flags;
        }

        @Override
        public List<BakedQuad> getQuads(final Direction direction)
        {
            return groups.get(direction == null ? 6 : direction.ordinal());
        }

        @Override
        public TriState ambientOcclusion()
        {
            return ao;
        }

        @Override
        public boolean useAmbientOcclusion()
        {
            return legacyAo;
        }

        @Override
        public Material.Baked particleMaterial()
        {
            return particle;
        }

        @Override
        public int materialFlags()
        {
            return flags;
        }
    }

    private static final class FrozenExtendedPart extends FrozenPart implements ExtendedBlockStateModelPart
    {
        private final BlockState appearance;

        private FrozenExtendedPart(
            final List<List<BakedQuad>> groups, final TriState ao, final boolean legacyAo,
            final Material.Baked particle, final int flags, final @Nullable BlockState appearance
        )
        {
            super(groups, ao, legacyAo, particle, flags);
            this.appearance = appearance;
        }

        @Override
        public @Nullable BlockState getBlockAppearance()
        {
            return appearance;
        }
    }
}
