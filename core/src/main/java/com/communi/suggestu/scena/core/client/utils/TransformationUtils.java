package com.communi.suggestu.scena.core.client.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class TransformationUtils
{

    private TransformationUtils() {
        throw new IllegalStateException("Tried to initialize TransformationUtils. But this is a utility class!");
    }

    public static void push(PoseStack stack, final Transformation transformation, final boolean requiresStackPush)
    {
        if (requiresStackPush)
        {
            stack.pushPose();
        }

        stack.mulPose(transformation);
    }

}
