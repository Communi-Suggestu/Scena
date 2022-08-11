package com.communi.suggestu.scena.core.blockstate;

import com.communi.suggestu.scena.core.IScenaPlatform;
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

/**
 * Allows access to game objects based on the world position.
 * Not every platform supports this natively, so we sometimes have to use different tactics to implement this.
 */
public interface ILevelBasedPropertyAccessor
{

    /**
     * Gives access to level based property accessors.
     *
     * @return The accessor for level based properties.
     */

    static ILevelBasedPropertyAccessor getInstance() {
        return IScenaPlatform.getInstance().getLevelBasedPropertyAccessor();
    }

    /**
     * Indicates if the blockstate on the given position should check for weak power propagation.
     *
     * @param levelReader The level reader to read the properties from.
     * @param blockPos The position to get the blockstate from out of the level reader for the check.0
     * @param direction The direction to check from.
     * @return True when the block propagates weak power from the given direction in the given level reader.
     */
    boolean shouldCheckWeakPower(LevelReader levelReader, BlockPos blockPos, Direction direction);

    /**
     * Determines the friction value of the blockstate on the given position in the given world reader.
     *
     * @param levelReader The world reader to get the blockstate from.
     * @param blockPos The position to get the friction of.
     * @param entity The entity to get the friction for. Might be null if unknown.
     * @return The friction value.
     */
    float getFriction(LevelReader levelReader, BlockPos blockPos, @Nullable Entity entity);

    /**
     * Determines the light emission value (so the amount of light emitted from the block itself) based on the blockstate on the given
     * position in the given reader.
     *
     * @param levelReader The level reader to pull the emission value from.
     * @param blockPos The position to read the value for.
     * @return The light emission value between 0 and 15
     */
    int getLightEmission(LevelReader levelReader, BlockPos blockPos);

    /**
     * Determines the light block value (so the amount of light blocked by the block itself) based on the blockstate on the given
     * position in the given reader.
     *
     * @param blockGetter The block getter to pull the blocking value from.
     * @param blockPos The position to read the value for.
     * @return The light block value between 0 and the max light level retrieved from {@link BlockGetter#getMaxLightLevel()}.
     */
    default int getLightBlock(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos).getLightBlock(
          blockGetter,
          blockPos
        );
    }

    /**
     * Determines if the blockstate at the given position in the given block getter is able to propagate skylight downwards.
     * In other words if this method returns false then the block on the given position blocks skylight.
     *
     * @param blockGetter The block getter to get the blockstate from to determine the propagation.
     * @param blockPos The position of the block to check.
     * @return True when the block propagates skylight (like leaves and air) false when not (like stone)
     */
    default boolean propagatesSkylightDown(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos).propagatesSkylightDown(blockGetter, blockPos);
    }

    /**
     * Determines if the player can harvest the block in the block getter at the given position.
     *
     * @param blockGetter The block getter to check from.
     * @param pos The position of the block that is about to be harvested by the player.
     * @param player The player in question.
     * @return True when harvestable, false when not.
     */
    boolean canHarvestBlock(BlockGetter blockGetter, BlockPos pos, Player player);

    /**
     * Returns the beacon color multiplier of the blockstate on the given position in the block getter for the given beacon position.
     *
     * @param levelReader The level reader to pull the multiplier from.
     * @param pos The position to get the blockstate from.
     * @param beaconPos The position of the beacon.
     * @return The color multiplier for the beacon.
     */
    float[] getBeaconColorMultiplier(LevelReader levelReader, BlockPos pos, BlockPos beaconPos);

    /**
     * Returns the sound type of the blockstate on the given position for the given entity.
     *
     * @param levelReader The level reader to pull the blockstate out of.
     * @param pos The position to get the sound type for.
     * @param entity The entity to get the sound type for.
     * @return The sound type of the blockstate on the given position for the given entity.
     */
    SoundType getSoundType(final LevelReader levelReader, BlockPos pos, Entity entity);

    /**
     * Determines the explosion resistance of the blockstate in the given position against the given explosion.
     *
     * @param blockGetter The block getter to pull contextual information from.
     * @param position The position in question.
     * @param explosion The explosion to get the resistance against for.
     * @return The explosion resistance.
     */
    float getExplosionResistance(BlockGetter blockGetter, BlockPos position, Explosion explosion);

    /**
     * Determines if the target blockstate at the target position can have the grass state on the grass position below it become a grass block.
     *
     * @param levelReader The level reader of the world.
     * @param grassState The state of the grass supporting block.
     * @param grassBlockPos The position of the grass supporting block.
     * @param targetState The target state of the block above the grass.
     * @param targetPosition The position of the target state in the level reader.
     * @return An optional indicating if the target can sustain the grass state. Empty if no decision can be made and vanilla logic needs to be executed.
     */
    Optional<Boolean> canBeGrass(LevelReader levelReader, BlockState grassState, BlockPos grassBlockPos, BlockState targetState, BlockPos targetPosition);
}
