package com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.context.IModelBakingContext;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.IBlockModelAccessor;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.IModelBakeryAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class FabricModelBakingContextDelegate implements IModelBakingContext
{

    private final FabricExtendedBlockModel source;
    private final IBlockModelAccessor sourceAccessor;

    public FabricModelBakingContextDelegate(final FabricExtendedBlockModel source) {
        this.source = source;
        this.sourceAccessor = (IBlockModelAccessor) source;
    }

    @Override
    public UnbakedModel getUnbakedModel(final ResourceLocation unbakedModel)
    {
        final IModelBakeryAccessor modelBakeryAccessor = (IModelBakeryAccessor) Minecraft.getInstance().getModelManager();
        return modelBakeryAccessor.getModelBakery().getModel(unbakedModel);
    }

    @Override
    public Optional<Material> getMaterial(final String name)
    {
        if (source.hasTexture(name)) {
            return Optional.ofNullable(source.getMaterial(name));
        }

        return Optional.empty();
    }

    @Override
    public boolean isGui3d()
    {
        return sourceAccessor.getGuiLight().lightLikeBlock();
    }

    @Override
    public boolean useBlockLight()
    {
        return sourceAccessor.getGuiLight().lightLikeBlock();
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return sourceAccessor.isHasAmbientOcclusion();
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return sourceAccessor.getTransforms();
    }

    @Override
    public ItemOverrides getItemOverrides()
    {
        final IModelBakeryAccessor modelBakeryAccessor = (IModelBakeryAccessor) Minecraft.getInstance().getModelManager();
        return sourceAccessor.getOverrides(modelBakeryAccessor.getModelBakery(), source);
    }
}
