package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(AtlasManager.class)
public class AtlasManagerMixin
{

    @Definition(id = "iterator", method = "Ljava/util/List;iterator()Ljava/util/Iterator;")
    @Definition(id = "KNOWN_ATLASES", field = "Lnet/minecraft/client/resources/model/sprite/AtlasManager;KNOWN_ATLASES:Ljava/util/List;")
    @Expression("KNOWN_ATLASES.iterator()")
    @ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Iterator<AtlasManager.AtlasConfig> onInit(final Iterator<AtlasManager.AtlasConfig> original) {
        final List<AtlasManager.AtlasConfig> customConfigs = new ArrayList<>();
        FabricClientEvents.REGISTER_TEXTURE_ATLASES_EVENT.invoker()
            .handle(customConfigs::add);

        return Iterators.concat(
            original,
            customConfigs.iterator()
        );
    }
}
