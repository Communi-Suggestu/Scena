package com.communi.suggestu.scena.forge.mixin.platform.common;

import com.communi.suggestu.scena.core.world.level.storage.CopyableTagValueInput;
import com.communi.suggestu.scena.core.world.level.storage.WriteableTagValueOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagValueInput.class)
public class TagValueInputMixin implements CopyableTagValueInput
{
    @Shadow @Final private CompoundTag input;

    @Override
    public void scena$copyTo(final WriteableTagValueOutput valueOutput)
    {
        valueOutput.scena$write(input.entrySet());
    }
}
