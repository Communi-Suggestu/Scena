package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.ExtendedBlockModelPart;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.renderer.VanillaBlockModelPartEncoder;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnstableApiUsage")
@Mixin(VanillaBlockModelPartEncoder.class)
public class VanillaBlockModelPartEncoderMixin
{

    @WrapOperation(
        method = "emitQuads(Lnet/minecraft/client/renderer/block/model/BlockModelPart;Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;Ljava/util/function/Predicate;)V",
        at = @At(
            target = "Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;ambientOcclusion(Lnet/fabricmc/fabric/api/util/TriState;)Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;",
            value = "INVOKE"
        )
    )
    private static QuadEmitter onEmitAmbientOcclusion(
        final QuadEmitter instance, TriState triState, final Operation<QuadEmitter> original, @Local(argsOnly = true) BlockModelPart blockModelPart) {
        if (blockModelPart instanceof ExtendedBlockModelPart extendedBlockModelPart) {
            triState = switch(extendedBlockModelPart.ambientOcclusion()) {
                case TRUE -> TriState.TRUE;
                case FALSE -> TriState.FALSE;
                case DEFAULT -> TriState.DEFAULT;
            };
        }

        return original.call(instance, triState);
    }

    @WrapOperation(
        method = "emitQuads(Lnet/minecraft/client/renderer/block/model/BlockModelPart;Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;Ljava/util/function/Predicate;)V",
        at = @At(
            target = "Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;emit()Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;",
            value = "INVOKE"
        )
    )
    private static QuadEmitter onEmitQuad(
        final QuadEmitter instance, final Operation<QuadEmitter> original, @Local(argsOnly = true) BlockModelPart blockModelPart) {
        if (blockModelPart instanceof ExtendedBlockModelPart extendedBlockModelPart) {
            instance.renderLayer(extendedBlockModelPart.renderType());
        }

        return original.call(instance);
    }
}
