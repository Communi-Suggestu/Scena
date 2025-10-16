package com.communi.suggestu.scena.core.world.level.storage;

import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.TagValueInput;

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

    /**
     * Returns the current tag value output as a tag value input.
     *
     * @return The copyable tag value output.
     */
    CopyableTagValueInput scena$asInput();

    /**
     * The copyable tag value input.
     *
     * @return The copyable input variant of this output.
     */
    default CopyableTagValueInput asInput() {
        return scena$asInput();
    }
}
