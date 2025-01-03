package com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.IBlockModelAccessor;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Function;

public class FabricExtendedBlockModel extends BlockModel
{
    private final IModelSpecification<?> specification;

    public FabricExtendedBlockModel(final IBlockModelAccessor blockModel, final IModelSpecification<?> specification)
    {
        super(blockModel.parentLocation(), Collections.emptyList(), blockModel.textureMap(), blockModel.usesAmbientOcclusion(), blockModel.guiLight(), blockModel.transforms(), blockModel.overrides());
        this.specification = specification;
    }

    @Override
    public @NotNull BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
        final FabricModelBakingContextDelegate context = new FabricModelBakingContextDelegate(this);

        final BakedModel bakedModel = specification.bake(context, baker, spriteGetter, state);
        return new FabricBakedModelDelegate(bakedModel);
    }

    @Override
    public @NotNull BakedModel bake(ModelBaker baker, BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, boolean guiLight3d) {
        final FabricModelBakingContextDelegate context = new FabricModelBakingContextDelegate(this);

        final BakedModel bakedModel = specification.bake(context, baker, spriteGetter, state);
        return new FabricBakedModelDelegate(bakedModel);
    }
}
