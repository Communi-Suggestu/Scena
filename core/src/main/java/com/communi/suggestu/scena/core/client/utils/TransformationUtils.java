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

        Vector3fc trans = transformation.translation();
        stack.translate(trans.x(), trans.y(), trans.z());

        stack.mulPose(transformation.leftRotation());

        Vector3fc scale = transformation.scale();
        stack.scale(scale.x(), scale.y(), scale.z());

        stack.mulPose(transformation.rightRotation());
    }

}
