package com.communi.suggestu.scena.core.util;


import org.joml.Matrix4f;

import java.nio.FloatBuffer;

public class MatrixUtils
{

    private MatrixUtils()
    {
        throw new IllegalStateException("Can not instantiate an instance of: MatrixUtils. This is a utility class");
    }

    public static void multiplyBackward(Matrix4f current, Matrix4f other) {
        Matrix4f copy = new Matrix4f(other);
        copy.mul(current);
        current.set(copy);
    }

    public static void setTranslation(Matrix4f m, final float x, final float y, final float z) {
        final float[] translationMatrix = {
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1
        };

        m.set(FloatBuffer.wrap(translationMatrix));
    }

}
