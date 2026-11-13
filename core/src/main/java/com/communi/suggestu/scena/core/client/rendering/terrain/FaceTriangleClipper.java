package com.communi.suggestu.scena.core.client.rendering.terrain;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Direction;

/**
 * Clips the two original triangles, retaining weights for interpolation of their already-computed lighting.
 */
public final class FaceTriangleClipper {
	private static final int[][] TRIANGLES = {{0, 1, 2}, {2, 3, 0}};

	private FaceTriangleClipper() {
	}

	public static List<Triangle> clip(final TerrainQuad source, final TerrainQuad bounds) {
		if (source.face() != bounds.face()) {
			throw new IllegalArgumentException("Clip face and source face must have the same direction");
		}
		final Direction.Axis axis = source.face().getAxis();
		final float plane = coordinate(source.v0(), axis);
		float minA = Float.POSITIVE_INFINITY;
		float maxA = Float.NEGATIVE_INFINITY;
		float minB = Float.POSITIVE_INFINITY;
		float maxB = Float.NEGATIVE_INFINITY;
		for (int index = 0; index < 4; index++) {
			if (!finite(source.vertex(index)) || !finite(bounds.vertex(index))
					|| coordinate(source.vertex(index), axis) != plane || coordinate(bounds.vertex(index), axis) != plane) {
				throw new IllegalArgumentException("Clip face and source face must share a plane");
			}
			minA = Math.min(minA, first(bounds.vertex(index), axis));
			maxA = Math.max(maxA, first(bounds.vertex(index), axis));
			minB = Math.min(minB, second(bounds.vertex(index), axis));
			maxB = Math.max(maxB, second(bounds.vertex(index), axis));
		}
		if (!(minA < maxA && minB < maxB)) {
			throw new IllegalArgumentException("Clip bounds must have positive area");
		}
		int corners = 0;
		for (int index = 0; index < 4; index++) {
			final float a = first(bounds.vertex(index), axis);
			final float b = second(bounds.vertex(index), axis);
			if ((a != minA && a != maxA) || (b != minB && b != maxB)) {
				throw new IllegalArgumentException("Clip bounds must be an axis-aligned rectangle");
			}
			final int corner = 1 << ((a == maxA ? 1 : 0) | (b == maxB ? 2 : 0));
			if ((corners & corner) != 0) {
				throw new IllegalArgumentException("Clip bounds must have four distinct corners");
			}
			corners |= corner;
		}

		final List<Triangle> result = new ArrayList<>();
		for (final int[] triangle : TRIANGLES) {
			List<Vertex> polygon = List.of(original(source, triangle[0]), original(source, triangle[1]), original(source, triangle[2]));
			polygon = clipEdge(polygon, axis, true, minA, true);
			polygon = clipEdge(polygon, axis, true, maxA, false);
			polygon = clipEdge(polygon, axis, false, minB, true);
			polygon = clipEdge(polygon, axis, false, maxB, false);
			for (int index = 1; index + 1 < polygon.size(); index++) {
				final Vertex a = polygon.getFirst(), b = polygon.get(index), c = polygon.get(index + 1);
				if (area(a, b, c, axis) != 0) {
					result.add(new Triangle(a, b, c));
				}
			}
		}
		return List.copyOf(result);
	}

	private static List<Vertex> clipEdge(
			final List<Vertex> polygon, final Direction.Axis axis, final boolean firstAxis,
			final float boundary, final boolean minimum
	) {
		if (polygon.isEmpty()) {
			return polygon;
		}
		final List<Vertex> result = new ArrayList<>();
		Vertex previous = polygon.getLast();
		float previousValue = projected(previous, axis, firstAxis);
		boolean previousInside = minimum ? previousValue >= boundary : previousValue <= boundary;
		for (final Vertex current : polygon) {
			final float currentValue = projected(current, axis, firstAxis);
			final boolean currentInside = minimum ? currentValue >= boundary : currentValue <= boundary;
			if (currentInside != previousInside) {
				result.add(interpolate(previous, current, (boundary - previousValue) / (currentValue - previousValue)));
			}
			if (currentInside) {
				result.add(current);
			}
			previous = current;
			previousValue = currentValue;
			previousInside = currentInside;
		}
		return result;
	}

	private static Vertex original(final TerrainQuad quad, final int index) {
		return new Vertex(quad.vertex(index), index == 0 ? 1 : 0, index == 1 ? 1 : 0,
				index == 2 ? 1 : 0, index == 3 ? 1 : 0);
	}

	private static Vertex interpolate(final Vertex a, final Vertex b, final float t) {
		final TerrainVertex p = a.position(), q = b.position();
		return new Vertex(new TerrainVertex(mix(p.x(), q.x(), t), mix(p.y(), q.y(), t), mix(p.z(), q.z(), t),
				mix(p.u(), q.u(), t), mix(p.v(), q.v(), t)), mix(a.w0(), b.w0(), t), mix(a.w1(), b.w1(), t),
				mix(a.w2(), b.w2(), t), mix(a.w3(), b.w3(), t));
	}

	private static float mix(final float a, final float b, final float t) {
		return a + (b - a) * t;
	}

	private static float area(final Vertex a, final Vertex b, final Vertex c, final Direction.Axis axis) {
		return (first(b.position(), axis) - first(a.position(), axis)) * (second(c.position(), axis) - second(a.position(), axis))
				- (second(b.position(), axis) - second(a.position(), axis)) * (first(c.position(), axis) - first(a.position(), axis));
	}

	private static float projected(final Vertex vertex, final Direction.Axis axis, final boolean firstAxis) {
		return firstAxis ? first(vertex.position(), axis) : second(vertex.position(), axis);
	}

	private static float coordinate(final TerrainVertex vertex, final Direction.Axis axis) {
		return switch (axis) { case X -> vertex.x(); case Y -> vertex.y(); case Z -> vertex.z(); };
	}

	private static boolean finite(final TerrainVertex vertex) {
		return Float.isFinite(vertex.x()) && Float.isFinite(vertex.y()) && Float.isFinite(vertex.z())
				&& Float.isFinite(vertex.u()) && Float.isFinite(vertex.v());
	}

	private static float first(final TerrainVertex vertex, final Direction.Axis axis) {
		return axis == Direction.Axis.X ? vertex.y() : vertex.x();
	}

	private static float second(final TerrainVertex vertex, final Direction.Axis axis) {
		return axis == Direction.Axis.Z ? vertex.y() : vertex.z();
	}

	public record Vertex(TerrainVertex position, float w0, float w1, float w2, float w3) {
		public float interpolate(final float v0, final float v1, final float v2, final float v3) {
			return w0 * v0 + w1 * v1 + w2 * v2 + w3 * v3;
		}
	}

	public record Triangle(Vertex v0, Vertex v1, Vertex v2) {
	}
}
