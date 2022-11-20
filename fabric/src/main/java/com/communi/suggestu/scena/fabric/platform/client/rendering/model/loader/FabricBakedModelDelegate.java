package com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader;

import com.communi.suggestu.scena.core.client.models.baked.IDataAwareBakedModel;
import com.communi.suggestu.scena.core.client.models.baked.IDelegatingBakedModel;
import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.entity.block.IBlockEntityWithModelData;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class FabricBakedModelDelegate implements BakedModel, IDelegatingBakedModel, FabricBakedModel
{
    private final BakedModel delegate;

    public FabricBakedModelDelegate(final BakedModel delegate) {this.delegate = delegate;}

    @Override
    public List<BakedQuad> getQuads(
      @Nullable final BlockState state, @Nullable final Direction direction, final @NotNull RandomSource random)
    {
        return delegate.getQuads(state, direction, random);
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
    public TextureAtlasSprite getParticleIcon()
    {
        return delegate.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return delegate.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return delegate.getOverrides();
    }

    @Override
    public BakedModel getDelegate()
    {
        return delegate;
    }

    @Override
    public boolean isVanillaAdapter()
    {
        return false;
    }

    @Override
    public void emitBlockQuads(
      final BlockAndTintGetter blockAndTintGetter, final BlockState blockState, final BlockPos blockPos, final Supplier<RandomSource> supplier, final RenderContext renderContext)
    {
        Object attachmentData = null;
        if (blockAndTintGetter instanceof RenderAttachedBlockView renderAttachedBlockView) {
            attachmentData = renderAttachedBlockView.getBlockEntityRenderAttachment(blockPos);
        }

        IBlockModelData blockModelData;
        if (attachmentData instanceof IBlockModelData blockModelDataAttachment) {
            blockModelData = blockModelDataAttachment;
        }
        else {
            final BlockEntity blockEntity = blockAndTintGetter.getBlockEntity(blockPos);
            if (!(blockEntity instanceof IBlockEntityWithModelData) || !(getDelegate() instanceof IDataAwareBakedModel)) {
                renderContext.fallbackConsumer().accept(getDelegate());
                return;
            }

            blockModelData = ((IBlockEntityWithModelData) blockEntity).getBlockModelData();
        }

        final IDataAwareBakedModel dataAwareBakedModel = (IDataAwareBakedModel) getDelegate();
        emitBlockQuads(dataAwareBakedModel, blockModelData, blockState, blockPos, supplier, renderContext);
    }

    public void emitBlockQuads(
            final IDataAwareBakedModel dataAwareBakedModel, final IBlockModelData blockModelData, final BlockState blockState, final BlockPos blockPos, final Supplier<RandomSource> supplier, final RenderContext renderContext) {
        final Collection<RenderType> renderTypes = dataAwareBakedModel.getSupportedRenderTypes(blockState, supplier.get(), blockModelData);

        for (Direction direction : Direction.values()) {
            renderTypes.forEach(renderType -> emitBlockQuads(dataAwareBakedModel, blockModelData, blockState, blockPos, direction, supplier, renderContext, renderType));
        }
    }

    public void emitBlockQuads(
            final IDataAwareBakedModel dataAwareBakedModel, final IBlockModelData blockModelData, final BlockState blockState, final BlockPos blockPos, final Direction direction, final Supplier<RandomSource> supplier, final RenderContext renderContext, RenderType renderType) {
        final List<BakedQuad> quads = dataAwareBakedModel.getQuads(blockState, direction, supplier.get(), blockModelData, renderType);

        final RenderMaterial material = Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer()).materialFinder().blendMode(0, BlendMode.fromRenderLayer(renderType)).find();

        quads.forEach(quad -> {
            final MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
            meshBuilder.getEmitter().fromVanilla(quad, material, direction);
            renderContext.meshConsumer().accept(meshBuilder.build());
        });
    }

    @Override
    public void emitItemQuads(final ItemStack itemStack, final Supplier<RandomSource> supplier, final RenderContext renderContext)
    {
        final BakedModel itemModel = getDelegate().getOverrides().
          resolve(
            getDelegate(),
            itemStack,
            Minecraft.getInstance().level,
            Minecraft.getInstance().player,
            supplier.get().nextInt()
          );

        renderContext.fallbackConsumer().accept(itemModel);
    }

    @FunctionalInterface
    private interface QuadGetter {

        List<BakedQuad> getQuads(BlockState blockState, Direction side, RandomSource rand);
    }

    private static final class QuadDelegatingBakedModel extends ForwardingBakedModel
    {
        private final QuadGetter quadGetter;

        private QuadDelegatingBakedModel(
          final BakedModel delegate,
          final QuadGetter quadGetter) {
            this.quadGetter = quadGetter;
            this.wrapped = delegate;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable final BlockState blockState, @Nullable final Direction direction, final RandomSource random)
        {
            return quadGetter.getQuads(blockState, direction, random);
        }
    }
}
