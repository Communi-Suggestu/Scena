package com.communi.suggestu.scena.core.client.models;

import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("resource")
public class UnbakedGeometryHelper
{

    private UnbakedGeometryHelper()
    {
        throw new IllegalStateException("Can not instantiate an instance of: UnbakedGeometryHelper. This is a utility class");
    }

    public static List<BlockElement> createUnbakedItemElements(int layerIndex, TextureAtlasSprite sprite) {
        var elements = ItemModelGenerator.processFrames(layerIndex, "layer" + layerIndex, sprite.contents());
        fixItemModelSeams(elements, sprite);
        return elements;
    }

    public static List<BlockElement> createUnbakedItemMaskElements(int layerIndex, TextureAtlasSprite sprite) {
        List<BlockElement> elements = createUnbakedItemElements(layerIndex, sprite);
        elements.removeFirst(); // Remove north and south faces

        float expand = -sprite.uvShrinkRatio();
        SpriteContents spriteContents = sprite.contents();
        int width = spriteContents.width();
        int height = spriteContents.height();
        BitSet bits = new BitSet(width * height);

        // For every frame in the texture, mark all the opaque pixels (this is what vanilla does too)
        spriteContents.getUniqueFrames().forEach(frame -> {
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    if (!spriteContents.isTransparent(frame, x, y))
                        bits.set(x + y * width);
        });

        // Scan in search of opaque pixels
        for (int y = 0; y < height; y++) {
            int xStart = -1;
            for (int x = 0; x < width; x++) {
                boolean opaque = bits.get(x + y * width);
                if (opaque == (xStart == -1)) { // (opaque && -1) || (!opaque && !-1)
                    if (xStart == -1) {
                        // We have found the start of a new segment, continue
                        xStart = x;
                        continue;
                    }

                    // The segment is over, expand down as far as possible
                    int yEnd = y + 1;
                    expand:
                    for (; yEnd < height; yEnd++)
                        for (int x2 = xStart; x2 <= x; x2++)
                            if (!bits.get(x2 + yEnd * width))
                                break expand;

                    // Mark all pixels in the area as visited
                    for (int i = xStart; i < x; i++)
                        for (int j = y; j < yEnd; j++)
                            bits.clear(i + j * width);

                    Vector3f from = new Vector3f(16 * xStart / (float) width, 16 - 16 * yEnd / (float) height, 7.5F);
                    Vector3f to = new Vector3f(16 * x / (float) width, 16 - 16 * y / (float) height, 8.5F);
                    // Create initial default UVs
                    BlockElementFace.UVs northUvs = FaceBakery.defaultFaceUV(from, to, Direction.NORTH);
                    BlockElementFace.UVs southUvs = FaceBakery.defaultFaceUV(from, to, Direction.SOUTH);
                    // Expand coordinates to match the shrunk UVs of the front/back face on a standard generated model
                    from.x = Mth.clamp(Mth.lerp(expand, from.x, 8F), 0F, 16F);
                    from.y = Mth.clamp(Mth.lerp(expand, from.y, 8F), 0F, 16F);
                    to.x = Mth.clamp(Mth.lerp(expand, to.x, 8F), 0F, 16F);
                    to.y = Mth.clamp(Mth.lerp(expand, to.y, 8F), 0F, 16F);
                    // Counteract sprite expansion to ensure pixel alignment
                    northUvs = expandUVs(northUvs, expand);
                    southUvs = expandUVs(southUvs, expand);
                    // Create faces
                    Map<Direction, BlockElementFace> faces = Map.of(
                        Direction.NORTH, new BlockElementFace(null, layerIndex, "layer" + layerIndex, northUvs, Quadrant.R0),
                        Direction.SOUTH, new BlockElementFace(null, layerIndex, "layer" + layerIndex, southUvs, Quadrant.R0));
                    // Create element
                    elements.add(new BlockElement(from, to, faces, null, true, 0));

                    // Reset xStart
                    xStart = -1;
                }
            }
        }
        return elements;
    }

    private static BlockElementFace.UVs expandUVs(BlockElementFace.UVs uvs, float expand) {
        float centerU = (uvs.minU() + uvs.minU() + uvs.maxU() + uvs.maxU()) / 4.0F;
        float centerV = (uvs.minV() + uvs.minV() + uvs.maxV() + uvs.maxV()) / 4.0F;
        return new BlockElementFace.UVs(
            Mth.clamp(Mth.lerp(expand, uvs.minU(), centerU), 0F, 16F),
            Mth.clamp(Mth.lerp(expand, uvs.minV(), centerV), 0F, 16F),
            Mth.clamp(Mth.lerp(expand, uvs.maxU(), centerU), 0F, 16F),
            Mth.clamp(Mth.lerp(expand, uvs.maxV(), centerV), 0F, 16F));
    }

    /**
     * Bakes a list of {@linkplain BlockElement block elements} and feeds the baked quads to a {@linkplain QuadCollection.Builder quad collection builder}.
     */
    public static void bakeElements(QuadCollection.Builder builder, List<BlockElement> elements, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState) {
        for (BlockElement element : elements) {
            element.faces().forEach((side, face) -> {
                var sprite = spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.parse(face.texture())));
                BakedQuad quad = SimpleUnbakedGeometry.bakeFace(element, face, sprite, side, modelState);
                if (face.cullForDirection() == null)
                    builder.addUnculledFace(quad);
                else
                    builder.addCulledFace(Direction.rotate(modelState.transformation().getMatrix(), face.cullForDirection()), quad);
            });
        }
    }

    /**
     * Bakes a list of {@linkplain BlockElement block elements} and returns the list of baked quads.
     */
    public static List<BakedQuad> bakeElements(List<BlockElement> elements, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState) {
        if (elements.isEmpty())
            return List.of();
        var builder = new QuadCollection.Builder();
        bakeElements(builder, elements, spriteGetter, modelState);
        return builder.build().getAll();
    }

    /**
     * Modify the position and UVs of the edge quads of generated item models to account for sprite expansion of the
     * front and back quad. Fixes <a href="https://bugs.mojang.com/browse/MC-73186">MC-73186</a> on generated item models.
     *
     * @param elements The generated elements, may include the front and back face
     * @param sprite   The texture from which the elements were generated
     * @return the original elements list
     */
    public static List<BlockElement> fixItemModelSeams(List<BlockElement> elements, TextureAtlasSprite sprite) {
        float expand = -sprite.uvShrinkRatio();
        elements.replaceAll(element -> {
            // Edge elements are guaranteed to have exactly one face, anything else is either invalid or the front/back
            if (element.faces().size() != 1) return element;

            var faceEntry = element.faces().entrySet().iterator().next();
            if (faceEntry.getKey().getAxis() == Direction.Axis.Z) return element;

            // Move edge quads to account for sprite expansion of the front and back quads
            Vector3f from = new Vector3f(
                Mth.clamp(Mth.lerp(expand, element.from().x(), 8F), 0F, 16F),
                Mth.clamp(Mth.lerp(expand, element.from().y(), 8F), 0F, 16F),
                element.from().z());
            Vector3f to = new Vector3f(
                Mth.clamp(Mth.lerp(expand, element.to().x(), 8F), 0F, 16F),
                Mth.clamp(Mth.lerp(expand, element.to().y(), 8F), 0F, 16F),
                element.to().z());

            BlockElementFace face = faceEntry.getValue();
            BlockElementFace.UVs uvs = face.uvs();
            if (uvs == null) {
                uvs = FaceBakery.defaultFaceUV(element.from(), element.to(), faceEntry.getKey());
            }
            float minU = uvs.minU();
            float minV = uvs.minV();
            float maxU = uvs.maxU();
            float maxV = uvs.maxV();
            // Counteract sprite expansion on edge quads to ensure alignment with pixels on the front and back quads
            if (faceEntry.getKey().getAxis() == Direction.Axis.Y) {
                float centerU = (minU + minU + maxU + maxU) / 4.0F;
                minU = Mth.clamp(Mth.lerp(expand, minU, centerU), 0F, 16F);
                maxU = Mth.clamp(Mth.lerp(expand, maxU, centerU), 0F, 16F);
            } else {
                float centerV = (minV + minV + maxV + maxV) / 4.0F;
                minV = Mth.clamp(Mth.lerp(expand, minV, centerV), 0F, 16F);
                maxV = Mth.clamp(Mth.lerp(expand, maxV, centerV), 0F, 16F);
            }
            uvs = new BlockElementFace.UVs(minU, minV, maxU, maxV);
            face = new BlockElementFace(face.cullForDirection(), face.tintIndex(), face.texture(), uvs, face.rotation());

            return new BlockElement(from, to, Map.of(faceEntry.getKey(), face), element.rotation(), element.shade(), element.lightEmission());
        });
        return elements;
    }
}
