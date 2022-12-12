package com.communi.suggestu.scena.forge.platform.client.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public final class ForgeModelGeometryToSpecificationPlatformDelegator<T extends IModelSpecification<T>>
        implements IUnbakedGeometry<ForgeModelGeometryToSpecificationPlatformDelegator<T>>
{

    private final T delegate;

    public ForgeModelGeometryToSpecificationPlatformDelegator(final T delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        final ForgeModelBakingContextDelegate contextDelegate = new ForgeModelBakingContextDelegate(baker::getModel, baker, context);

        return new ForgeBakedModelDelegate(delegate.bake(
                contextDelegate, baker, spriteGetter, modelState, modelLocation
        ));
    }
}
