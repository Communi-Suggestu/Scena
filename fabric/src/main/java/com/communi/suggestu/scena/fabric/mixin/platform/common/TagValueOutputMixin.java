package com.communi.suggestu.scena.fabric.mixin.platform.common;

import com.communi.suggestu.scena.core.world.level.storage.WriteableTagValueOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.TagValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(TagValueOutput.class)
public class TagValueOutputMixin implements WriteableTagValueOutput
{

    @Shadow @Final private CompoundTag output;

    @Override
    public void scena$write(final Set<Map.Entry<String, Tag>> toWrite)
    {
        toWrite.forEach(e -> output.put(e.getKey(), e.getValue()));
    }
}
