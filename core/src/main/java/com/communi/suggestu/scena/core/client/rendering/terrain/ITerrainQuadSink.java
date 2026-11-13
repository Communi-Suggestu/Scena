package com.communi.suggestu.scena.core.client.rendering.terrain;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Writes terrain geometry into the active renderer's native section mesh.
 *
 * <p>Positions are block-local. The sink owns native material encoding, tint resolution, light application and buffer
 * ownership; it must not start a separate render pass or GPU resource lifecycle.
 */
public interface ITerrainQuadSink
{
    /**
     * Emits a rectangle on a block-local axis-aligned plane.
     *
     * @param layer native render layer for the material
     * @param materialState state represented by the geometry (including shader-facing block identity)
     * @param face face normal
     * @param depth plane coordinate in block-local units, 0..1
     * @param minA minimum first in-plane coordinate, 0..1
     * @param minB minimum second in-plane coordinate, 0..1
     * @param maxA maximum first in-plane coordinate, 0..1
     * @param maxB maximum second in-plane coordinate, 0..1
     * @param sprite exact source sprite
     * @param tintIndex source tint index, or -1 when untinted
     * @param shade whether directional shade is enabled
     * @param emission source material emission
     */
    void axisAlignedQuad(
        ChunkSectionLayer layer,
        BlockState materialState,
        Direction face,
        float depth,
        float minA,
        float minB,
        float maxA,
        float maxB,
        TextureAtlasSprite sprite,
        int tintIndex,
        boolean shade,
        int emission
    );

    /**
     * Emits a general four-vertex quad. UVs must already be derived from the source model; the sink does not invent
     * texture coordinates.
     *
     */
    void quad(
        ChunkSectionLayer layer,
        BlockState materialState,
        TerrainVertex v0,
        TerrainVertex v1,
        TerrainVertex v2,
        TerrainVertex v3,
        Direction cullFace,
        TextureAtlasSprite sprite,
        int tintIndex,
        boolean shade,
        int emission
    );
}
