package com.communi.suggestu.scena.fabric.platform.level;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.communi.suggestu.scena.core.blockstate.ILevelBasedPropertyAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class FabricLevelBasedPropertyAccessor implements ILevelBasedPropertyAccessor
{
    private static final FabricLevelBasedPropertyAccessor INSTANCE = new FabricLevelBasedPropertyAccessor();

    public static FabricLevelBasedPropertyAccessor getInstance()
    {
        return INSTANCE;
    }

    private FabricLevelBasedPropertyAccessor()
    {
    }

    @Override
    public boolean shouldCheckWeakPower(final LevelReader levelReader, final BlockPos blockPos, final Direction direction)
    {
        final BlockState blockState = levelReader.getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.shouldCheckWeakPower(
              blockState, levelReader, blockPos, direction
            );
        }

        return blockState.isRedstoneConductor(levelReader, blockPos);
    }

    @Override
    public float getFriction(final LevelReader levelReader, final BlockPos blockPos, @Nullable final Entity entity)
    {
        final BlockState blockState = levelReader.getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getFriction(
              blockState, levelReader, blockPos, entity
            );
        }

        return blockState.getBlock().getFriction();
    }

    @Override
    public int getLightEmission(final LevelReader levelReader, final BlockPos blockPos)
    {
        final BlockState blockState = levelReader.getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getLightEmission(
              blockState, levelReader, blockPos
            );
        }

        return blockState.getLightEmission();
    }

    @Override
    public boolean canHarvestBlock(final BlockGetter blockGetter, final BlockPos blockPos, final Player player)
    {
        final BlockState blockState = blockGetter.getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.canHarvestBlock(
              blockState, blockGetter, blockPos, player
            );
        }

        return player.hasCorrectToolForDrops(blockState);
    }

    @Override
    public float[] getBeaconColorMultiplier(final LevelReader levelReader, final BlockPos blockPos, final BlockPos beaconPos)
    {
        final BlockState blockState = levelReader.getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getBeaconColorMultiplier(
              blockState, levelReader, blockPos, beaconPos
            );
        }

        if (blockState.getBlock() instanceof BeaconBeamBlock beaconBeamBlock) {
            return beaconBeamBlock.getColor().getTextureDiffuseColors();
        }

        return null;
    }

    @Override
    public SoundType getSoundType(final LevelReader levelReader, final BlockPos blockPos, final Entity entity)
    {
        final BlockState blockState = levelReader.getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getSoundType(
              blockState, levelReader, blockPos, entity
            );
        }

        return blockState.getSoundType();
    }

    @Override
    public float getExplosionResistance(final BlockGetter blockGetter, final BlockPos blockPos, final Explosion explosion)
    {
        final BlockState blockState = blockGetter.getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getExplosionResistance(
              blockState, blockGetter, blockPos, explosion
            );
        }

        return blockState.getBlock().getExplosionResistance();
    }

    @Override
    public Optional<Boolean> canBeGrass(
      final LevelReader levelReader, final BlockState grassState, final BlockPos grassBlockPos, final BlockState targetState, final BlockPos targetPosition)
    {
        if (targetState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return Optional.of(blockWithWorldlyProperties.canBeGrass(
              levelReader, grassState, grassBlockPos, targetState, targetPosition
              )
            );
        }

        return Optional.empty();
    }
}
