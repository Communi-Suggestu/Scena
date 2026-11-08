package com.communi.suggestu.scena.forge.mixin.platform.client;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.entity.block.IBlockEntityWithModelData;
import com.communi.suggestu.scena.forge.platform.client.model.data.ForgeBlockModelDataPlatformDelegate;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IBlockExtension.class)
public interface IBlockExtensionMixin {

    @SuppressWarnings("ConstantValue")
    @WrapOperation(
            method = "getModelData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getModelData()Lnet/neoforged/neoforge/model/data/ModelData;"
            )
    )
    default ModelData scena$getModelDataWithBlockEntityCheck(BlockEntity instance, Operation<ModelData> original) {
        if (instance instanceof IBlockEntityWithModelData blockEntityWithModelData) {
           final IBlockModelData blockModelData = blockEntityWithModelData.getBlockModelData();
            if (blockModelData == null) {
                return ModelData.EMPTY;
            }

            if (!(blockModelData instanceof ForgeBlockModelDataPlatformDelegate platformDelegate)) {
                throw new IllegalStateException("Block model data is not compatible with the current platform.");
            }

            return platformDelegate.getDelegate();
        } else {
            return original.call(instance);
        }
    }
}
