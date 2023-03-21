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
import com.google.gson.JsonSyntaxException;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class FabricExtendedBlockModelDeserializer extends BlockModel.Deserializer
{

    private final String name;
    private final IModelSpecificationLoader<?> delegate;

    public FabricExtendedBlockModelDeserializer(final String name, final IModelSpecificationLoader<?> delegate) {this.name = name;
        this.delegate = delegate;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Nullable
    public BlockModel deserialize(JsonElement element, Type targetType, JsonDeserializationContext deserializationContext) throws JsonParseException
    {
        if (!element.isJsonObject())
            throw new JsonSyntaxException("Model needs to be object");

        final JsonObject jsonobject = element.getAsJsonObject();
        if (!jsonobject.has("loader"))
            return null;

        if (!jsonobject.get("loader").getAsString().equals(name))
            return null;

        @Nullable final BlockModel model = super.deserialize(element, targetType, deserializationContext);
        if (model == null)
            return null;

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
