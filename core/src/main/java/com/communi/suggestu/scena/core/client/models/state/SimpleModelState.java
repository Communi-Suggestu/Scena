/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.communi.suggestu.scena.core.client.models.state;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

/**
 * Simple implementation of {@link ModelState}.
 */
public final class SimpleModelState implements ModelState
{
    private final Transformation transformation;
    private final boolean uvLocked;

    public SimpleModelState(Transformation transformation, boolean uvLocked)
    {
        this.transformation = transformation;
        this.uvLocked = uvLocked;
    }

    public SimpleModelState(Transformation transformation)
    {
        this(transformation, false);
    }

    @Override
    public Transformation getRotation()
    {
        return transformation;
    }

    @Override
    public boolean isUvLocked()
    {
        return uvLocked;
    }
}
