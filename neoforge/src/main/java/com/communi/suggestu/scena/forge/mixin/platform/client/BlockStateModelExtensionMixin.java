package com.communi.suggestu.scena.forge.mixin.platform.client;

import com.communi.suggestu.scena.core.client.rendering.DataAwareBlockStateModel;
import net.neoforged.neoforge.client.extensions.BlockStateModelExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockStateModelExtension.class)
public abstract class BlockStateModelExtensionMixin implements DataAwareBlockStateModel
{
}
