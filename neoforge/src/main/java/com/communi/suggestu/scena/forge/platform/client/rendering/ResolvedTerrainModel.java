package com.communi.suggestu.scena.forge.platform.client.rendering;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.IdentityHashMap;

import com.communi.suggestu.scena.core.client.rendering.ExtendedBlockStateModelPart;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainGeometry;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainGeometryPart;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainQuad;
import com.communi.suggestu.scena.core.client.rendering.terrain.TerrainVertex;
import com.communi.suggestu.scena.core.client.rendering.terrain.FaceTriangleClipper;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

/**
 * Transient lighter adapter over resolved geometry. It never caches or pre-generates rectangle BakedQuads.
 */
public final class ResolvedTerrainModel implements BlockStateModel
{
    private final List<BlockStateModelPart> parts;
    private final int flags;
    private Map<BakedQuad, List<FaceTriangleClipper.Triangle>> clips;

    public ResolvedTerrainModel(final TerrainGeometry geometry)
    {
        final List<BlockStateModelPart> parts = new ArrayList<>();
        int flags = 0;
        for (final TerrainGeometryPart source : geometry.parts())
        {
            parts.add(new Part(source, this));
            flags |= source.materialFlags();
        }
        this.parts = List.copyOf(parts);
        this.flags = flags;
    }

    @Override
    public void collectParts(final RandomSource random, final List<BlockStateModelPart> output)
    {
        output.addAll(parts);
    }

    @Override
    public Material.Baked particleMaterial()
    {
        return null;
    }

    @Override
    public int materialFlags()
    {
        return flags;
    }

    public List<FaceTriangleClipper.Triangle> takeClippedTriangles(final BakedQuad quad)
    {
        return clips == null ? null : clips.remove(quad);
    }

    static BakedQuad lightingQuad(final TerrainQuad quad)
    {
        final BakedQuad.MaterialInfo source = quad.material();
        final BakedQuad.MaterialInfo material = new BakedQuad.MaterialInfo(
            source.sprite(), source.layer(), source.itemRenderType(), quad.tintIndex(),
            source.shade(), source.lightEmission(), source.ambientOcclusion()
        );
        return new BakedQuad(
            position(quad.v0()), position(quad.v1()), position(quad.v2()), position(quad.v3()),
            uv(quad.v0()), uv(quad.v1()), uv(quad.v2()), uv(quad.v3()), quad.face(), material
        );
    }

    private static Vector3f position(final TerrainVertex vertex)
    {
        return new Vector3f(vertex.x(), vertex.y(), vertex.z());
    }

    private static long uv(final TerrainVertex vertex)
    {
        return UVPair.pack(vertex.u(), vertex.v());
    }

    private static final class Part implements BlockStateModelPart, ExtendedBlockStateModelPart
    {
        private final TerrainGeometryPart source;
        private final List<List<BakedQuad>> groups = new ArrayList<>(7);

        private Part(
            final TerrainGeometryPart source, final ResolvedTerrainModel owner
        )
        {
            this.source = source;
            final int[] counts = new int[7];
            for (final TerrainQuad quad : source.quads())
            {
                counts[quad.cullFace() == null ? 6 : quad.cullFace().ordinal()]++;
            }
            final int[] offsets = new int[7];
            for (int group = 1; group < 7; group++)
            {
                offsets[group] = offsets[group - 1] + counts[group - 1];
            }
            System.arraycopy(offsets, 0, counts, 0, counts.length);
            final TerrainQuad[] grouped = new TerrainQuad[source.quads().size()];
            for (final TerrainQuad quad : source.quads())
            {
                final int group = quad.cullFace() == null ? 6 : quad.cullFace().ordinal();
                grouped[counts[group]++] = quad;
            }
            for (int group = 0; group < 7; group++)
            {
                final int size = counts[group] - offsets[group];
                if (size == 0)
                {
                    groups.add(List.of());
                    continue;
                }
                final int offset = offsets[group];
                groups.add(new AbstractList<>()
                {
                    public BakedQuad get(final int index)
                    {
                        java.util.Objects.checkIndex(index, size);
                        final TerrainQuad quad = grouped[offset + index];
                        final BakedQuad result = lightingQuad(quad);
                        if (quad.clipRegions() != null)
                        {
                            if (owner.clips == null)
                            {
                                owner.clips = new IdentityHashMap<>();
                            }
                            owner.clips.put(result, quad.clipRegions().stream()
                                .flatMap(region -> FaceTriangleClipper.clip(quad, region).stream()).toList());
                        }
                        return result;
                    }
                    public int size() { return size; }
                });
            }
        }

        public List<BakedQuad> getQuads(final Direction direction) { return groups.get(direction == null ? 6 : direction.ordinal()); }
        public boolean useAmbientOcclusion() { return source.ambientOcclusion().toBoolean(true); }
        public TriState ambientOcclusion() { return source.ambientOcclusion(); }
        public BlockState getBlockAppearance() { return source.appearance(); }
        public Material.Baked particleMaterial() { return null; }
        public int materialFlags() { return source.materialFlags(); }
    }
}
