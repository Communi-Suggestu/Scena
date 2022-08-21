package com.communi.suggestu.scena.core.client.models;

import com.communi.suggestu.scena.core.client.textures.UnitTextureAtlasSprite;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel;

import java.util.List;
import java.util.Map;

/**
 * A completely empty model with no quads or texture dependencies.
 * <p>
 * You can access it as a {@link BakedModel}.
 */
public class EmptyModel
{
    public static final BakedModel BAKED = new Baked();

    private EmptyModel()
    {
    }

    private static class Baked extends SimpleBakedModel
    {
        private static final Material MISSING_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

        public Baked()
        {
            super(List.of(), Map.of(), false, false, false, UnitTextureAtlasSprite.INSTANCE, ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY);
        }

        @Override
        public TextureAtlasSprite getParticleIcon()
        {
            return MISSING_TEXTURE.sprite();
        }
    }
}
