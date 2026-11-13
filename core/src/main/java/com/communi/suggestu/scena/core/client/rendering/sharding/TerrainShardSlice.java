package com.communi.suggestu.scena.core.client.rendering.sharding;

/**
 * One quad-aligned byte range of a parent section's CPU vertex data.
 *
 * @param index       Position of this slice in the plan, zero based.
 * @param byteOffset  Offset of the first byte of this slice in the parent vertex data.
 * @param byteLength  Length of this slice in bytes, a multiple of one quad.
 * @param vertexCount Number of vertices in this slice.
 * @param indexCount  Number of implicit quad indices needed to draw this slice.
 */
public record TerrainShardSlice(int index, int byteOffset, int byteLength, int vertexCount, int indexCount) {
}
