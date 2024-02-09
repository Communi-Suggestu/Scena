package com.communi.suggestu.scena.forge.mixin.platform.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelDataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = ModelDataManager.Active.class, remap = false)
public abstract class ActiveModelDataManagerMixin
{
    @WrapOperation(
            method = "refreshAt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getModelData()Lnet/neoforged/neoforge/client/model/data/ModelData;")
    )
    private ModelData bypassExpensiveCalculationIfNecessary(BlockEntity instance, Operation<ModelData> original) {
            return original.call(instance);
    }
}
