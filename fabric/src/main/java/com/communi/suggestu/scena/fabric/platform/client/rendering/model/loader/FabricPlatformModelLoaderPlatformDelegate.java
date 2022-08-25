package com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.communi.suggestu.scena.core.util.TransformationUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.math.Transformation;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public final class FabricPlatformModelLoaderPlatformDelegate<L extends IModelSpecificationLoader<S>, S extends IModelSpecification<S>> implements ModelResourceProvider
{

    private final Gson gson;

    public FabricPlatformModelLoaderPlatformDelegate(final ResourceLocation name, final L delegate)
    {
        this.gson = (new GsonBuilder())
                            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
                            .registerTypeHierarchyAdapter(UnbakedModel.class, new FabricExtendedBlockModelDeserializer(name.toString(), delegate))
                            .registerTypeHierarchyAdapter(BlockModel.class, new FabricExtendedBlockModelDeserializer(name.toString(), delegate))
                            .registerTypeAdapter(BlockModel.class, this)
                            .registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer() {})
                            .registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer() {})
                            .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer() {})
                            .registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer() {})
                            .registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer() {})
                            .registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer() {})
                            .registerTypeAdapter(Transformation.class, new TransformationUtils.Deserializer())
                            .create();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable UnbakedModel loadModelResource(final ResourceLocation resourceLocation, final ModelProviderContext modelProviderContext) throws ModelProviderException
    {
        try
        {
            final ResourceLocation target = new ResourceLocation(resourceLocation.getNamespace(), "models/" + resourceLocation.getPath() + ".json");

            final Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(target);
            if (resource.isEmpty())
                return null;

            final InputStream inputStream = resource.get().open();
            final InputStreamReader streamReader = new InputStreamReader(inputStream);

            final UnbakedModel modelSpecification = gson.fromJson(streamReader, UnbakedModel.class);

            streamReader.close();
            inputStream.close();

            return modelSpecification;
        }
        catch (IOException e)
        {
            throw new ModelProviderException("Failed to find and read resource", e);
        }
    }
}
