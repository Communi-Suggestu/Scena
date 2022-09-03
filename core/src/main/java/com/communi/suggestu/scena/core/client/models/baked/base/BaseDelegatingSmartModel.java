package com.communi.suggestu.scena.core.client.models.baked.base;

import com.communi.suggestu.scena.core.client.models.baked.IDelegatingBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.NotNull;

public abstract class BaseDelegatingSmartModel extends BaseSmartModel implements IDelegatingBakedModel
{

    private final BakedModel delegate;

    protected BaseDelegatingSmartModel(final BakedModel delegate) {this.delegate = delegate;}

    @Override
    public BakedModel getDelegate()
    {
        return delegate;
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return getDelegate().useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return getDelegate().isGui3d();
    }

    @Override
    public boolean isCustomRenderer()
    {
        return getDelegate().isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon()
    {
        return getDelegate().getParticleIcon();
    }

    @Override
    public @NotNull ItemTransforms getTransforms()
    {
        return getDelegate().getTransforms();
    }

    @Override
    public boolean usesBlockLight()
    {
        return getDelegate().usesBlockLight();
    }
}
