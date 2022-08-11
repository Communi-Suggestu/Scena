package com.communi.suggestu.scena.forge.platform.level;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.communi.suggestu.scena.core.blockstate.ILevelBasedPropertyAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ForgeLevelBasedPropertyAccessor implements ILevelBasedPropertyAccessor
{
    private static final ForgeLevelBasedPropertyAccessor INSTANCE = new ForgeLevelBasedPropertyAccessor();

    public static ForgeLevelBasedPropertyAccessor getInstance()
    {
        return INSTANCE;
    }

    private ForgeLevelBasedPropertyAccessor()
    {
    }

    @Override
    public boolean shouldCheckWeakPower(final LevelReader levelReader, final BlockPos blockPos, final Direction direction)
    {
        return levelReader.getBlockState(blockPos)
          .shouldCheckWeakPower(levelReader, blockPos, direction);
    }

    @Override
    public float getFriction(final LevelReader levelReader, final BlockPos blockPos, @Nullable final Entity entity)
    {
        return levelReader.getBlockState(blockPos)
          .getFriction(levelReader, blockPos, entity);
    }

    @Override
    public int getLightEmission(final LevelReader levelReader, final BlockPos blockPos)
    {
        return levelReader.getBlockState(blockPos)
          .getLightEmission(levelReader, blockPos);
    }

    @Override
    public boolean canHarvestBlock(final BlockGetter blockGetter, final BlockPos pos, final Player player)
    {
        return blockGetter.getBlockState(pos)
          .canHarvestBlock(blockGetter, pos, player);
    }

    @Override
    public float[] getBeaconColorMultiplier(final LevelReader levelReader, final BlockPos pos, final BlockPos beaconPos)
    {
        return levelReader.getBlockState(pos)
          .getBeaconColorMultiplier(levelReader, pos, beaconPos);
    }

    @Override
    public SoundType getSoundType(final LevelReader levelReader, final BlockPos pos, final Entity entity)
    {
        return levelReader.getBlockState(pos)
          .getSoundType(levelReader, pos , entity);
    }

    @Override
    public float getExplosionResistance(final BlockGetter blockGetter, final BlockPos position, final Explosion explosion)
    {
        return blockGetter.getBlockState(position)
          .getExplosionResistance(blockGetter, position, explosion);
    }

    @Override
    public Optional<Boolean> canBeGrass(
      final LevelReader levelReader, final BlockState grassState, final BlockPos grassBlockPos, final BlockState targetState, final BlockPos targetPosition)
    {
        if (targetState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return Optional.of(blockWithWorldlyProperties.canBeGrass(levelReader, grassState, grassBlockPos, targetState, targetPosition));
        }

        return Optional.empty();
    }
}
