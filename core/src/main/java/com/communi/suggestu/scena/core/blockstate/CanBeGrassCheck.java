package com.communi.suggestu.scena.core.blockstate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface CanBeGrassCheck {
    /**
     * Determines if the target blockstate at the target position can have the grass state on the grass position below it become a grass block.
     *
     * @param levelReader    The level reader of the world.
     * @param grassState     The state of the grass supporting block.
     * @param grassBlockPos  The position of the grass supporting block.
     * @param targetState    The target state of the block above the grass.
     * @param targetPosition The position of the target state in the level reader.
     * @return An optional indicating if the target can sustain the grass state. Empty if no decision can be made and vanilla logic needs to be executed.
     */
    Optional<Boolean> canBeGrass(LevelReader levelReader, BlockState grassState, BlockPos grassBlockPos, BlockState targetState, BlockPos targetPosition);
}
