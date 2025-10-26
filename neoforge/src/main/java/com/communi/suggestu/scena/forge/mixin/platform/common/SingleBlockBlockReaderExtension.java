package com.communi.suggestu.scena.forge.mixin.platform.common;

import com.communi.suggestu.scena.core.util.SingleBlockBlockReader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SingleBlockBlockReader.class)
public class SingleBlockBlockReaderExtension implements IBlockGetterExtension
{

    @Shadow
    @Final
    private BlockEntity blockEntity;

    @Override
    public @NotNull ModelData getModelData(final @NotNull BlockPos pos)
    {
        if (this.blockEntity == null)
            return ModelData.EMPTY;

        return blockEntity.getModelData();
    }
}
