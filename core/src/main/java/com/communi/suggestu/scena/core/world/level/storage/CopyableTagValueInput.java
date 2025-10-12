package com.communi.suggestu.scena.core.world.level.storage;

import net.minecraft.world.level.storage.ValueOutput;

/**
 * A value input which is copyable.
 */
public interface CopyableTagValueInput
{
    /**
     * Copies the existing value inputs keys to the value output.
     *
     * @param output The value output to copy to.
     */
    default void copyTo(ValueOutput output) throws UnsupportedOperationException {
        if (output instanceof WriteableTagValueOutput writeableTagValueOutput) {
            scena$copyTo(writeableTagValueOutput);
        } else {
            throw new UnsupportedOperationException("Copying can only happen on writable tags");
        }
    }

    /**
     * Copies the existing value inputs keys to the value output.
     *
     * @param valueOutput The value output to copy to.
     */
    void scena$copyTo(WriteableTagValueOutput valueOutput);
}
