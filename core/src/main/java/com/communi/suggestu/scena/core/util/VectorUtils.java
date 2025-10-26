package com.communi.suggestu.scena.core.util;

import net.minecraft.core.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;

public final class VectorUtils
{

    private VectorUtils() {
        throw new IllegalStateException("Can not instantiate an instance of: VectorUtils. This is a utility class");
    }

    public static Vector2f projectOntoPlaneOf(final Vector3f vector3f, final Direction cullDirection) {
        return switch (cullDirection) {
            case DOWN, UP -> new Vector2f(vector3f.x(), vector3f.z());
            case NORTH, SOUTH -> new Vector2f(vector3f.x(), vector3f.y());
            case WEST, EAST -> new Vector2f(vector3f.z(), vector3f.y());
        };
    }

    public static Vector3f unprojectFromPlaneOf(final Vector2f planeVector, final Vector3f worldVector, final Direction direction) {
        return switch (direction) {
            case DOWN, UP -> new Vector3f(planeVector.x(), worldVector.y(), planeVector.y());
            case NORTH, SOUTH -> new Vector3f(planeVector.x(), planeVector.y(), worldVector.z());
            case WEST, EAST -> new Vector3f(worldVector.x(), planeVector.y(), planeVector.x());
        };
    }
}
