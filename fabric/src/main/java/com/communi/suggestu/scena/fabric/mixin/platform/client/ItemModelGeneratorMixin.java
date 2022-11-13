package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader.FabricExtendedBlockModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Function;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {

    @Inject(method = "generateBlockModel(Ljava/util/function/Function;Lnet/minecraft/client/renderer/block/model/BlockModel;)Lnet/minecraft/client/renderer/block/model/BlockModel;",
            at = @At("HEAD"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    public void onGenerate(Function<Material, TextureAtlasSprite> spriteGetter, BlockModel model, CallbackInfoReturnable<BlockModel> cir) {
        if (model instanceof FabricExtendedBlockModel extendedModel) {
            cir.setReturnValue(extendedModel);
        }
    }
}
