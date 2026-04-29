package com.communi.suggestu.scena.core.client.effect;

import com.communi.suggestu.scena.core.client.IClientManager;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A manager for registering effect handlers.
 */
public interface IEffectManager
{
    static IEffectManager getInstance()
    {
        return IClientManager.getInstance().getEffectManager();
    }

    /**
     * Called to set up the hit effect handler registration callback.
     *
     * @param setter The callback which sets the hit effect handler.
     */
    void setupHitEffects(
        Consumer<IHitEffectRegistrar> setter
    );

    /**
     * Called to set up the destroy effect handler registration callback.
     *
     * @param setter The callback which sets the destroy effect handler.
     */
    void setupDestroyEffects(
        Consumer<IDestroyEffectRegistrar> setter
    );

    /**
     * Registrar which allows modders to register custom hit effect handlers for blocks.
     */
    @FunctionalInterface
    interface IHitEffectRegistrar {
        /**
         * Called to register a new hit effect handler.
         *
         * @param handler The handler which processes hit effect requests.
         * @param blocks The block for which the handler applies the effect.
         */
        void register(IHitEffectHandler handler, Block... blocks);
    }

    @FunctionalInterface
    interface IDestroyEffectRegistrar {
        /**
         * Called to register a new destroy effect handler.
         *
         * @param handler The handler which processes destroy effect requests.
         * @param blocks The blocks for which the handler applies the effect.
         */
        void register(IDestroyEffectHandler handler, Block... blocks);
    }

    /**
     * Callback handler for adding hit effects when something hits a given block.
     */
    @FunctionalInterface
    interface IHitEffectHandler
    {
        /**
         * Spawn a digging particle effect in the level, this is a wrapper
         * around EffectRenderer.addBlockHitEffects to allow the block more
         * control over the particles. Useful when you have entirely different
         * texture sheets for different sides/locations in the level.
         *
         * @param state  The current state
         * @param level  The current level
         * @param blockPos The position that got hit.
         * @param hitDirection The direction of the block that got hit.
         * @param engine A reference to the current particle manager.
         * @return True to prevent vanilla digging particles form spawning.
         */
        boolean addHitEffects(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction hitDirection, @NotNull ParticleEngine engine);
    }

    /**
     * Callback handler for adding destroy hit effects when something destroys a given block.
     */
    @FunctionalInterface
    interface IDestroyEffectHandler
    {
        /**
         * Spawn particles for when the block is destroyed. Due to the nature
         * of how this is invoked, the x/y/z locations are not always guaranteed
         * to host your block. So be sure to do proper sanity checks before assuming
         * that the location is this block.
         *
         * @param Level   The current Level
         * @param pos     Position to spawn the particle
         * @param manager A reference to the current particle manager.
         * @return True to prevent vanilla break particles from spawning.
         */
        boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager);
    }
}
