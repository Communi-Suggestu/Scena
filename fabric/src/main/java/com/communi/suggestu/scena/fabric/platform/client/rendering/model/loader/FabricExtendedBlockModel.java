package com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.IBlockModelAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class FabricExtendedBlockModel extends BlockModel
{
    private final IModelSpecification<?> specification;

    public FabricExtendedBlockModel(final IBlockModelAccessor blockModel, final IModelSpecification<?> specification)
    {
        super(blockModel.getParentLocation(), Collections.emptyList(), blockModel.getTextureMap(), blockModel.isHasAmbientOcclusion(), blockModel.getGuiLight(), blockModel.getTransforms(), blockModel.getOverrides());
        this.specification = specification;
    }

    @Override
    public BakedModel bake(ModelBakery modelBakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
        final FabricModelBakingContextDelegate context = new FabricModelBakingContextDelegate(this);

        final BakedModel bakedModel = specification.bake(context, modelBakery, spriteGetter, transform, location);
        return new FabricBakedModelDelegate(bakedModel);
    }

    @Override
    public Collection<Material> getMaterials(final Function<ResourceLocation, UnbakedModel> modelGetter, final Set<Pair<String, String>> missingTextureErrors)
    {
        final Set<Material> resultingMaterials = new HashSet<>(super.getMaterials(modelGetter, missingTextureErrors));
        resultingMaterials.addAll(specification.getTextures(new FabricModelBakingContextDelegate(this), modelGetter, missingTextureErrors));

        return resultingMaterials;
    }
}
