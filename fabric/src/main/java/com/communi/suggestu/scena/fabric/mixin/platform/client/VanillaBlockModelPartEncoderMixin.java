package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.ExtendedBlockStateModelPart;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.client.renderer.VanillaBlockModelPartEncoder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnstableApiUsage")
@Mixin(VanillaBlockModelPartEncoder.class)
public class VanillaBlockModelPartEncoderMixin
{

    @WrapOperation(
        method = "emitQuads(Lnet/minecraft/client/renderer/block/dispatch/BlockStateModelPart;Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;Ljava/util/function/Predicate;)V",
        at = @At(
            target = "Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;ambientOcclusion(Lnet/fabricmc/fabric/api/util/TriState;)Lnet/fabricmc/fabric/api/client/renderer/v1/mesh/QuadEmitter;",
            value = "INVOKE"
        )
    )
    private static QuadEmitter onEmitAmbientOcclusion(
        final QuadEmitter instance, TriState triState, final Operation<QuadEmitter> original, @Local(argsOnly = true, name = "part") BlockStateModelPart part) {        if (part instanceof ExtendedBlockStateModelPart extendedBlockStateModelPart) {
            triState = switch(extendedBlockStateModelPart.ambientOcclusion()) {
                case TRUE -> TriState.TRUE;
                case FALSE -> TriState.FALSE;
                case DEFAULT -> TriState.DEFAULT;
            };
        }

        return original.call(instance, triState);
    }
}
