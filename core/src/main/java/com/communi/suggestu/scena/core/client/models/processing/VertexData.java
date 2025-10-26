package com.communi.suggestu.scena.core.client.models.processing;

import com.communi.suggestu.scena.core.util.VectorUtils;
import net.minecraft.core.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record VertexData(float x, float y, float z, float u, float v, int vertexIndex) {

    public VertexData(final Vector3f position, final Vector2f uv, final int vertexIndex) {
        this(position.x(), position.y(), position.z(), uv.x(), uv.y(), vertexIndex);
    }

    public float[] positionData() {
        return new float[] {x, y, z, 0f};
    }

    public float[] uvData() {
        return new float[] {u, v, 0, 0};
    }

    public Vector3f position() {
        return new Vector3f(x, y, z);
    }

    public Vector2f projectOntoPlaneOf(Direction cullDirection) {
        return VectorUtils.projectOntoPlaneOf(position(), cullDirection);
    }

    public Vector2f uv() {
        return new Vector2f(u, v);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder {
        private float x;
        private float y;
        private float z;
        private float u;
        private float v;
        private int vertexIndex;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withX(float x) {
            this.x = x;
            return this;
        }

        public Builder withY(float y) {
            this.y = y;
            return this;
        }

        public Builder withZ(float z) {
            this.z = z;
            return this;
        }

        public Builder withU(float u) {
            this.u = u;
            return this;
        }

        public Builder withV(float v) {
            this.v = v;
            return this;
        }

        public Builder withVertexIndex(int vertexIndex) {
            this.vertexIndex = vertexIndex;
            return this;
        }

        public VertexData build() {
            return new VertexData(x, y, z, u, v, vertexIndex);
        }
    }
}
