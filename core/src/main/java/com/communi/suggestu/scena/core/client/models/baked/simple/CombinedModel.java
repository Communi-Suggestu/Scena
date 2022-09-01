package com.communi.suggestu.scena.core.client.models.baked.simple;

import com.communi.suggestu.scena.core.client.models.baked.base.BaseBakedBlockModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class CombinedModel extends BaseBakedBlockModel
{

    private static final RandomSource COMBINED_RANDOM_MODEL = RandomSource.create();

    BakedModel[] merged;

    List<BakedQuad>[] face;
    List<BakedQuad>   generic;

    boolean isSideLit;

    @SuppressWarnings( "unchecked" )
    public CombinedModel(
      final BakedModel... args )
    {
        face = new ArrayList[Direction.values().length];

        generic = new ArrayList<>();
        for ( final Direction f : Direction.values() )
        {
            face[f.ordinal()] = new ArrayList<>();
        }

        merged = args;

        for ( final BakedModel m : merged )
        {
            generic.addAll( m.getQuads( null, null, COMBINED_RANDOM_MODEL ) );
            for ( final Direction f : Direction.values() )
            {
                face[f.ordinal()].addAll( m.getQuads( null, f, COMBINED_RANDOM_MODEL ) );
            }
        }

        isSideLit = Arrays.stream(args).anyMatch(BakedModel::usesBlockLight);
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        for ( final BakedModel a : merged )
        {
            return a.getParticleIcon();
        }

        return Minecraft.getInstance().getTextureAtlas(
          InventoryMenu.BLOCK_ATLAS
        ).apply(MissingTextureAtlasSprite.getLocation());
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction side, @NotNull final RandomSource rand)
    {
        if ( side != null )
        {
            return face[side.ordinal()];
        }

        return generic;
    }

    @Override
    public boolean usesBlockLight()
    {
        return isSideLit;
    }
}