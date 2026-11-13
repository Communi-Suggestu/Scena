package com.communi.suggestu.scena.forge.platform.client.rendering;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import com.communi.suggestu.scena.core.client.rendering.ExtendedBlockStateModelPart;
import com.communi.suggestu.scena.core.client.rendering.terrain.*;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import org.junit.jupiter.api.Test;

class ResolvedTerrainModelTest
{
    @Test
    void onlyCreatesTransientLightingQuadsAndPreservesSourceAndMappedMetadata()
    {
        final BakedQuad.MaterialInfo source = new BakedQuad.MaterialInfo(null, ChunkSectionLayer.CUTOUT, null, 42, false, 9, false);
        final TerrainQuad quad = new TerrainQuad(
            new TerrainVertex(.2f, .1f, .5f, .75f, .25f), new TerrainVertex(.9f, .1f, .5f, .25f, .25f),
            new TerrainVertex(.9f, .8f, .5f, .25f, .75f), new TerrainVertex(.2f, .8f, .5f, .75f, .75f),
            Direction.SOUTH, Direction.EAST, source, 3
        );
        final ResolvedTerrainModel model = new ResolvedTerrainModel(new TerrainGeometry(List.of(
            new TerrainGeometryPart(TriState.TRUE, null, 2, List.of(quad))
        )));
        final List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(123), parts);
        final BlockStateModelPart part = parts.getFirst();
        assertInstanceOf(ExtendedBlockStateModelPart.class, part);
        assertEquals(TriState.TRUE, part.ambientOcclusion());
        assertTrue(part.getQuads(null).isEmpty());
        final BakedQuad lit = part.getQuads(Direction.EAST).getFirst();
        assertNotSame(lit, part.getQuads(Direction.EAST).getFirst());
        assertEquals(3, lit.materialInfo().tintIndex());
        assertEquals(42, source.tintIndex());
        assertFalse(lit.materialInfo().ambientOcclusion());
        assertFalse(lit.materialInfo().shade());
        assertEquals(9, lit.materialInfo().lightEmission());
        assertEquals(Direction.SOUTH, lit.direction());
        assertEquals(UVPair.pack(.75f, .25f), lit.packedUV0());
        assertEquals(.2f, lit.position0().x());
    }

    @Test
    void associatesClipsWithTheOriginalQuadPassedToTheLighter()
    {
        final BakedQuad.MaterialInfo material = new BakedQuad.MaterialInfo(null, ChunkSectionLayer.TRANSLUCENT, null, -1, true, 0);
        final TerrainQuad region = rectangle(.25f, .25f, .75f, .75f, material);
        final TerrainQuad original = rectangle(0, 0, 1, 1, material);
        final TerrainQuad clipped = new TerrainQuad(original.v0(), original.v1(), original.v2(), original.v3(),
            original.face(), original.cullFace(), material, -1, List.of(region));
        assertTrue(TerrainGeometryValidation.supports(clipped));
        final ResolvedTerrainModel model = new ResolvedTerrainModel(new TerrainGeometry(List.of(
            new TerrainGeometryPart(TriState.DEFAULT, null, BakedQuad.FLAG_TRANSLUCENT, List.of(clipped))
        )));
        final List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(123), parts);
        final BakedQuad first = parts.getFirst().getQuads(Direction.SOUTH).getFirst();
        final BakedQuad second = parts.getFirst().getQuads(Direction.SOUTH).getFirst();
        assertNotSame(first, second);
        assertEquals(0f, first.position0().x());
        assertEquals(1f, first.position2().x());
        assertEquals(FaceTriangleClipper.clip(original, region), model.takeClippedTriangles(first));
        assertNull(model.takeClippedTriangles(first));
        assertNotNull(model.takeClippedTriangles(second));
    }

    @Test
    void groupsInterleavedFacesWithoutChangingTheirOrder()
    {
        final BakedQuad.MaterialInfo material = new BakedQuad.MaterialInfo(null, ChunkSectionLayer.SOLID, null, -1, true, 0);
        final TerrainQuad a = rectangle(0, 0, .25f, .5f, material);
        final TerrainQuad b = rectangle(.25f, 0, .5f, .5f, material);
        final TerrainQuad c = rectangle(.5f, 0, .75f, .5f, material);
        final TerrainQuad unculled = new TerrainQuad(b.v0(), b.v1(), b.v2(), b.v3(), b.face(), null, material, -1);
        final TerrainQuad west = new TerrainQuad(c.v0(), c.v1(), c.v2(), c.v3(), c.face(), Direction.WEST, material, -1);
        final var model = new ResolvedTerrainModel(new TerrainGeometry(List.of(
            new TerrainGeometryPart(TriState.DEFAULT, null, 0, List.of(a, unculled, c, west))
        )));
        final List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(123), parts);
        final var part = parts.getFirst();
        assertEquals(List.of(0f, .5f), part.getQuads(Direction.SOUTH).stream().map(quad -> quad.position0().x()).toList());
        assertEquals(.25f, part.getQuads(null).getFirst().position0().x());
        assertEquals(.5f, part.getQuads(Direction.WEST).getFirst().position0().x());
        assertTrue(part.getQuads(Direction.EAST).isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> part.getQuads(Direction.WEST).get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> part.getQuads(Direction.WEST).get(-1));
    }

    private static TerrainQuad rectangle(
        final float minX, final float minY, final float maxX, final float maxY, final BakedQuad.MaterialInfo material
    )
    {
        return new TerrainQuad(
            new TerrainVertex(minX, minY, .5f, minX, minY), new TerrainVertex(maxX, minY, .5f, maxX, minY),
            new TerrainVertex(maxX, maxY, .5f, maxX, maxY), new TerrainVertex(minX, maxY, .5f, minX, maxY),
            Direction.SOUTH, Direction.SOUTH, material, -1
        );
    }
}
