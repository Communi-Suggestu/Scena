package com.communi.suggestu.scena.forge.platform.client.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelState, @NotNull ItemOverrides overrides, @NotNull ResourceLocation modelLocation) {
        final ForgeModelBakingContextDelegate contextDelegate = new ForgeModelBakingContextDelegate(baker::getModel, baker, context);

        return new ForgeBakedModelDelegate(delegate.bake(
                contextDelegate, baker, spriteGetter, modelState, modelLocation
        ));
    }
}
