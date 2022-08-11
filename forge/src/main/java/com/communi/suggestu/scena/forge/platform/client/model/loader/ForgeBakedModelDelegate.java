package com.communi.suggestu.scena.forge.platform.client.model.loader;

import com.communi.suggestu.scena.core.client.models.IDelegatingBakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ForgeBakedModelDelegate implements BakedModel, IDelegatingBakedModel
{
    private final BakedModel delegate;

    public ForgeBakedModelDelegate(final BakedModel delegate) {this.delegate = delegate;}

    @Override
    public @NotNull List<BakedQuad> getQuads(
      @Nullable final BlockState p_119123_, @Nullable final Direction p_119124_, final @NotNull RandomSource p_119125_)
    {
        return delegate.getQuads(p_119123_, p_119124_, p_119125_);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction side, @NotNull final RandomSource rand, @NotNull final ModelData data, @Nullable final RenderType renderType)
    {
        return delegate.getQuads(state, side, rand, data, renderType);
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return delegate.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return delegate.isGui3d();
    }

    @Override
    public boolean usesBlockLight()
    {
        return delegate.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer()
    {
        return delegate.isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon()
    {
        return delegate.getParticleIcon();
    }

    @Override
    public @NotNull ItemOverrides getOverrides()
    {
        return delegate.getOverrides();
    }

    @Override
    public BakedModel getDelegate()
    {
        return delegate;
    }

    @Override
    public @NotNull ItemTransforms getTransforms()
    {
        return delegate.getTransforms();
    }

    @Override
    public boolean useAmbientOcclusion(final @NotNull BlockState state)
    {
        return delegate.useAmbientOcclusion(state);
    }

    @Override
    public boolean useAmbientOcclusion(final @NotNull BlockState state, final @NotNull RenderType renderType)
    {
        return delegate.useAmbientOcclusion(state, renderType);
    }

    @Override
    public @NotNull BakedModel applyTransform(final ItemTransforms.@NotNull TransformType transformType, final @NotNull PoseStack poseStack, final boolean applyLeftHandTransform)
    {
        return delegate.applyTransform(transformType, poseStack, applyLeftHandTransform);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull final BlockAndTintGetter level, @NotNull final BlockPos pos, @NotNull final BlockState state, @NotNull final ModelData modelData)
    {
        return delegate.getModelData(level, pos, state, modelData);
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(@NotNull final ModelData data)
    {
        return delegate.getParticleIcon(data);
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull final BlockState state, @NotNull final RandomSource rand, @NotNull final ModelData data)
    {
        return delegate.getRenderTypes(state, rand, data);
    }

    @Override
    public @NotNull List<RenderType> getRenderTypes(final @NotNull ItemStack itemStack, final boolean fabulous)
    {
        return delegate.getRenderTypes(itemStack, fabulous);
    }

    @Override
    public @NotNull List<BakedModel> getRenderPasses(final @NotNull ItemStack itemStack, final boolean fabulous)
    {
        return delegate.getRenderPasses(itemStack, fabulous);
    }
}
