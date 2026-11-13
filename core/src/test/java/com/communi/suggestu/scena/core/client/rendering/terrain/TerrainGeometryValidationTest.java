package com.communi.suggestu.scena.core.client.rendering.terrain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import org.junit.jupiter.api.Test;

class TerrainGeometryValidationTest
{
    @Test
    void acceptsResolvedRotatedUvsButRejectsTheCompleteBlockOnAnyUnsupportedQuad()
    {
        final TerrainQuad valid = quad(ChunkSectionLayer.SOLID);
        assertTrue(TerrainGeometryValidation.supports(geometry(List.of(valid))));
        final TerrainQuad translucent = quad(ChunkSectionLayer.TRANSLUCENT);
        assertTrue(TerrainGeometryValidation.supports(new TerrainGeometry(List.of(
            part(List.of(valid), 0),
            part(List.of(translucent), BakedQuad.FLAG_TRANSLUCENT)
        ))));
        assertFalse(TerrainGeometryValidation.supports(new TerrainQuad(
            valid.v0(), valid.v3(), valid.v2(), valid.v1(), valid.face(), valid.cullFace(), valid.material(), 3
        )));
        assertFalse(TerrainGeometryValidation.supports(new TerrainQuad(
            valid.v0(), valid.v2(), valid.v1(), valid.v3(), valid.face(), valid.cullFace(), valid.material(), 3
        )));
        assertFalse(TerrainGeometryValidation.supports(new TerrainQuad(
            new TerrainVertex(.2f, .1f, .5f, Float.NaN, 0), valid.v1(), valid.v2(), valid.v3(),
            valid.face(), valid.cullFace(), valid.material(), 3
        )));
    }

    @Test
    void immutableGeometryDoesNotRetainMutableProducerLists()
    {
        final List<TerrainQuad> quads = new ArrayList<>(List.of(quad(ChunkSectionLayer.CUTOUT)));
        final TerrainGeometryPart part = new TerrainGeometryPart(TriState.DEFAULT, null, 0, quads);
        final List<TerrainGeometryPart> parts = new ArrayList<>(List.of(part));
        final TerrainGeometry geometry = new TerrainGeometry(parts);
        quads.clear();
        parts.clear();
        assertTrue(TerrainGeometryValidation.supports(geometry));
        assertEquals(1, geometry.parts().getFirst().quads().size());
        assertThrows(UnsupportedOperationException.class, () -> geometry.parts().clear());
    }

    @Test
    void acceptsTranslucentFacesOnInteriorDownwardPlanes()
    {
        final var material = new BakedQuad.MaterialInfo(null, ChunkSectionLayer.TRANSLUCENT, null, -1, true, 0);
        final TerrainQuad quad = new TerrainQuad(
            new TerrainVertex(.4375f, .4375f, .5f, .67529297f, .22265625f),
            new TerrainVertex(.4375f, .4375f, .4375f, .67529297f, .22314453f),
            new TerrainVertex(.5f, .4375f, .4375f, .67578125f, .22314453f),
            new TerrainVertex(.5f, .4375f, .5f, .67578125f, .22265625f),
            Direction.DOWN, null, material, 0
        );
        assertTrue(TerrainGeometryValidation.supports(quad));
    }

    @Test
    void preflightsDisjointContainedClipRegionsBeforeAnyEmission()
    {
        final TerrainQuad source = quad(ChunkSectionLayer.TRANSLUCENT);
        final TerrainQuad left = region(source, .2f, .1f, .5f, .8f);
        final TerrainQuad right = region(source, .5f, .1f, .9f, .8f);
        final List<TerrainQuad> mutable = new ArrayList<>(List.of(left, right));
        final TerrainQuad clipped = withRegions(source, mutable);
        mutable.clear();
        assertTrue(TerrainGeometryValidation.supports(clipped));
        assertFalse(TerrainGeometryValidation.supportsCurrentTerrainShader(geometry(List.of(clipped))));
        assertTrue(TerrainGeometryValidation.supportsCurrentTerrainShader(geometry(List.of(source))));
        assertEquals(2, clipped.clipRegions().size());
        assertFalse(TerrainGeometryValidation.supports(withRegions(source, List.of())));
        assertFalse(TerrainGeometryValidation.supports(withRegions(source, List.of(left, left))));
        assertFalse(TerrainGeometryValidation.supports(withRegions(source, List.of(region(source, .1f, .1f, .5f, .8f)))));
        assertFalse(TerrainGeometryValidation.supports(withRegions(source, List.of(clipped))));
    }

    private static TerrainQuad withRegions(final TerrainQuad source, final List<TerrainQuad> regions)
    {
        return new TerrainQuad(source.v0(), source.v1(), source.v2(), source.v3(), source.face(), source.cullFace(),
            source.material(), source.tintIndex(), regions);
    }

    private static TerrainQuad region(
        final TerrainQuad source, final float minX, final float minY, final float maxX, final float maxY
    )
    {
        return new TerrainQuad(
            new TerrainVertex(minX, minY, .5f, 0, 0), new TerrainVertex(maxX, minY, .5f, 0, 0),
            new TerrainVertex(maxX, maxY, .5f, 0, 0), new TerrainVertex(minX, maxY, .5f, 0, 0),
            source.face(), source.cullFace(), source.material(), source.tintIndex()
        );
    }

    static TerrainGeometry geometry(final List<TerrainQuad> quads)
    {
        final int flags = quads.isEmpty() ? 0 : quads.getFirst().material().layer() == ChunkSectionLayer.TRANSLUCENT
            ? BakedQuad.FLAG_TRANSLUCENT : 0;
        return new TerrainGeometry(List.of(part(quads, flags)));
    }

    private static TerrainGeometryPart part(final List<TerrainQuad> quads, final int flags)
    {
        return new TerrainGeometryPart(TriState.DEFAULT, null, flags, quads);
    }

    static TerrainQuad quad(final ChunkSectionLayer layer)
    {
        return new TerrainQuad(
            new TerrainVertex(.2f, .1f, .5f, .75f, .25f),
            new TerrainVertex(.9f, .1f, .5f, .25f, .25f),
            new TerrainVertex(.9f, .8f, .5f, .25f, .75f),
            new TerrainVertex(.2f, .8f, .5f, .75f, .75f),
            Direction.SOUTH, null, new BakedQuad.MaterialInfo(null, layer, null, 42, true, 7),
            layer == ChunkSectionLayer.TRANSLUCENT ? BakedQuad.FLAG_TRANSLUCENT : 0
        );
    }
}
