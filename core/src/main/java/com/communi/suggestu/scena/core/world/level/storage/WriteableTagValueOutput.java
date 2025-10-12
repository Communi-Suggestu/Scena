package com.communi.suggestu.scena.core.world.level.storage;

import net.minecraft.nbt.Tag;

import java.util.Map;
import java.util.Set;

/**
 * Defines a value output to which can be written
 */
public interface WriteableTagValueOutput
{

    /**
     * Write raw data to this tag value output.
     *
     * @param toWrite To write.
     */
    void scena$write(Set<Map.Entry<String, Tag>> toWrite);
}
