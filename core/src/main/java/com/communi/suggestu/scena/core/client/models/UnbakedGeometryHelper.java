package com.communi.suggestu.scena.core.client.models;

import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("resource")
public class UnbakedGeometryHelper
{

    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();

    private UnbakedGeometryHelper()
    {
        throw new IllegalStateException("Can not instantiate an instance of: UnbakedGeometryHelper. This is a utility class");
    }

    /**
     * Creates a list of {@linkplain BlockElement block elements} in the shape of the specified sprite.
     * These can later be baked using the same, or another texture.
     * <p>
     * The {@link Direction#NORTH} and {@link Direction#SOUTH} faces take up the whole surface.
     */
    public static List<BlockElement> createUnbakedItemElements(int layerIndex, SpriteContents sprite)
    {
        return ITEM_MODEL_GENERATOR.processFrames(layerIndex, "layer" + layerIndex, sprite);
    }

    /**
     * Creates a list of {@linkplain BlockElement block elements} in the shape of the specified sprite.
     * These can later be baked using the same, or another texture.
     * <p>
     * The {@link Direction#NORTH} and {@link Direction#SOUTH} faces take up only the pixels the texture uses.
     */
    public static List<BlockElement> createUnbakedItemMaskElements(int layerIndex, TextureAtlasSprite sprite)
    {
        var elements = createUnbakedItemElements(layerIndex, sprite.contents());
        elements.remove(0); // Remove north and south faces

        int width = sprite.contents().width(), height = sprite.contents().height();
        var bits = new BitSet(width * height);

        // For every frame in the texture, mark all the opaque pixels (this is what vanilla does too)
        sprite.contents().getUniqueFrames().forEach(frame -> {
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    if (!sprite.contents().isTransparent(frame, x, y))
                        bits.set(x + y * width);
        });

        // Scan in search of opaque pixels
        for (int y = 0; y < height; y++)
        {
            int xStart = -1;
            for (int x = 0; x < width; x++)
            {
                var opaque = bits.get(x + y * width);
                if (opaque == (xStart == -1)) // (opaque && -1) || (!opaque && !-1)
                {
                    if (xStart == -1)
                    {
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

                    // Create element
                    elements.add(new BlockElement(
                            new Vector3f(16 * xStart / (float) width, 16 - 16 * yEnd / (float) height, 7.5F),
                            new Vector3f(16 * x / (float) width, 16 - 16 * y / (float) height, 8.5F),
                            Util.make(new HashMap<>(), map -> {
                                for (Direction direction : Direction.values())
                                    map.put(direction, new BlockElementFace(null, layerIndex, "layer" + layerIndex, new BlockFaceUV(null, 0)));
                            }),
                            null,
                            true
                    ));

                    // Reset xStart
                    xStart = -1;
                }
            }
        }
        return elements;
    }

    /**
     * Bakes a list of {@linkplain BlockElement block elements} and feeds the baked quads to a {@linkplain IModelBuilder model builder}.
     */
    public static void bakeElements(IModelBuilder<?> builder, List<BlockElement> elements, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation, RenderTypeGroup renderTypeGroup)
    {
        for (BlockElement element : elements)
        {
            element.faces.forEach((side, face) -> {
                var sprite = spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(face.texture)));
                var quad = bakeElementFace(element, face, sprite, side, modelState, modelLocation);
                //noinspection ConstantConditions This can be null!
                if (face.cullForDirection == null)
                    builder.addUnculledFace(renderTypeGroup, quad);
                else
                    builder.addCulledFace(renderTypeGroup, Direction.rotate(modelState.getRotation().getMatrix(), face.cullForDirection), quad);
            });
        }
    }

    /**
     * Turns a single {@link BlockElementFace} into a {@link BakedQuad}.
     */
    public static BakedQuad bakeElementFace(BlockElement element, BlockElementFace face, TextureAtlasSprite sprite, Direction direction, ModelState state, ResourceLocation modelLocation)
    {
        return FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, direction, state, element.rotation, element.shade, modelLocation);
    }
}
