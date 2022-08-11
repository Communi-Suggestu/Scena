package com.communi.suggestu.scena.forge.mixin.platform.common;

import com.communi.suggestu.scena.core.blockstate.ILevelBasedPropertyAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = SpreadingSnowyDirtBlock.class)
public abstract class WorldlyBlockGrassGrowMixin
{

    @Inject(
      method = "canBeGrass",
      at = @At("HEAD"),
      cancellable = true
    )
    private static void canBeGrassWorldlyBlock(BlockState grassState, LevelReader levelReader, BlockPos grassBlockPos, CallbackInfoReturnable<Boolean> ci){
        BlockPos targetPosition = grassBlockPos.above();
        BlockState targetState = levelReader.getBlockState(targetPosition);

        final Optional<Boolean> levelBasedResult = ILevelBasedPropertyAccessor.getInstance()
                                                                              .canBeGrass(levelReader, grassState, grassBlockPos, targetState, targetPosition);

        levelBasedResult.ifPresent(ci::setReturnValue);
    }
}
