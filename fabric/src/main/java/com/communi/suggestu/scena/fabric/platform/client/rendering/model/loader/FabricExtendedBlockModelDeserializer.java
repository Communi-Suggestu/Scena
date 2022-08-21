package com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.communi.suggestu.scena.core.util.TransformationUtils;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.IBlockModelAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class FabricExtendedBlockModelDeserializer extends BlockModel.Deserializer
{
    private final Gson GSON = (new GsonBuilder())
                                                .registerTypeAdapter(BlockModel.class, this)
                                                .registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer() {})
                                                .registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer() {})
                                                .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer() {})
                                                .registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer() {})
                                                .registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer() {})
                                                .registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer() {})
                                                .registerTypeAdapter(Transformation.class, new TransformationUtils.Deserializer())
                                                .create();

    private final String name;
    private final IModelSpecificationLoader<?> delegate;

    public FabricExtendedBlockModelDeserializer(final String name, final IModelSpecificationLoader<?> delegate) {this.name = name;
        this.delegate = delegate;
    }


    public BlockModel deserialize(JsonElement element, Type targetType, JsonDeserializationContext deserializationContext) throws JsonParseException
    {
        final BlockModel model = super.deserialize(element, targetType, deserializationContext);
        if (model == null)
            return null;

        final JsonObject jsonobject = element.getAsJsonObject();
        final IModelSpecification<?> geometry = deserializeGeometry(deserializationContext, jsonobject);

        if (geometry != null)
        {
            return new FabricExtendedBlockModel(
                    (IBlockModelAccessor) model,
                    geometry
            );
        }

        return null;
    }

    @Nullable
    public IModelSpecification<?> deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) throws JsonParseException
    {
        if (!object.has("loader"))
            return null;

        if (!object.get("loader").getAsString().equals(name.toString()))
            return null;

        return delegate.read(deserializationContext, object);
    }
}
