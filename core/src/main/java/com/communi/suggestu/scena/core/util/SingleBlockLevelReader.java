package com.communi.suggestu.scena.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class SingleBlockLevelReader extends SingleBlockBlockAndTintGetter implements LevelReader
{
    @Nullable
    private final LevelReader reader;

    protected SingleBlockLevelReader(
        final BlockState blockState,
        final BlockPos pos,
        final @Nullable BlockEntity blockEntity,
        final @Nullable LevelReader source)
    {
        super(blockState, pos, source, blockEntity);
        this.reader = source;
    }

    @Nullable
    @Override
    public ChunkAccess getChunk(final int x, final int z, @NotNull final ChunkStatus requiredStatus, final boolean nonnull)
    {
        if (this.reader == null)
            return null;

        return this.reader.getChunk(x, z, requiredStatus, nonnull);
    }

    @Override
    public boolean hasChunk(final int chunkX, final int chunkZ)
    {
        return this.reader != null && this.reader.hasChunk(chunkX, chunkZ);
    }

    @Override
    public int getHeight(@NotNull final Heightmap.Types heightmapType, final int x, final int z)
    {
        if (this.reader == null)
            return 0;

        return this.reader.getHeight(heightmapType, x, z);
    }

    @Override
    public int getSkyDarken()
    {
        return 15;
    }

    @NotNull
    @Override
    public BiomeManager getBiomeManager()
    {
        if (this.reader == null)
            throw new IllegalStateException("No reader available.");

        return this.reader.getBiomeManager();
    }

    @NotNull
    @Override
    public Holder<Biome> getUncachedNoiseBiome(final int x, final int y, final int z)
    {
        if (this.reader == null)
            throw new IllegalStateException("No reader available.");

        return this.reader.getUncachedNoiseBiome(x, y, z);
    }

    @Override
    public boolean isClientSide()
    {
        return this.reader == null || this.reader.isClientSide();
    }

    @Override
    public int getSeaLevel()
    {
        if (this.reader == null)
            return 63;

        return this.reader.getSeaLevel();
    }

    @NotNull
    @Override
    public DimensionType dimensionType()
    {
        if (this.reader == null)
            throw new IllegalStateException("No reader available.");

        return this.reader.dimensionType();
    }

    @Override
    public @NotNull RegistryAccess registryAccess() {
        if (this.reader == null)
            return RegistryAccess.EMPTY;

        return this.reader.registryAccess();
    }

    @Override
    public @NotNull FeatureFlagSet enabledFeatures() {
        if (this.reader == null)
            return FeatureFlagSet.of();

        return this.reader.enabledFeatures();
    }

    @NotNull
    @Override
    public WorldBorder getWorldBorder()
    {
        if (this.reader == null)
            throw new IllegalStateException("No reader available.");

        return this.reader.getWorldBorder();
    }

    @NotNull
    @Override
    public List<VoxelShape> getEntityCollisions(@Nullable final Entity entity, final @NotNull AABB aabb)
    {
        if (this.reader == null || entity == null)
            return List.of();

        return this.reader.getEntityCollisions(entity, aabb);
    }

    public static class Builder {
        private           BlockState  blockState;
        private           BlockPos                        pos = BlockPos.ZERO;
        private           Supplier<@Nullable BlockEntity> blockEntityBuilder;
        private @Nullable LevelReader                     source;

        public Builder withBlockState(final BlockState blockState)
        {
            this.blockState = blockState;
            return this;
        }

        public Builder withPos(final BlockPos pos)
        {
            this.pos = pos;
            return this;
        }

        public Builder withBlockEntity(final Supplier<@Nullable BlockEntity> blockEntityBuilder)
        {
            this.blockEntityBuilder = blockEntityBuilder;
            return this;
        }

        public Builder withSource(final @Nullable LevelReader source)
        {
            this.source = source;
            return this;
        }

        public SingleBlockLevelReader createSingleBlockLevelReader()
        {
            if (blockState == null)
                throw new IllegalStateException("A blockstate is required for a single block level reader!");

            final BlockEntity blockEntity = blockEntityBuilder != null ? blockEntityBuilder.get() : null;
            return new SingleBlockLevelReader(blockState, pos, blockEntity, source);
        }
    }
}
