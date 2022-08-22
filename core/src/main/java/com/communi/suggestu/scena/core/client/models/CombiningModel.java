package com.communi.suggestu.scena.core.client.models;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.models.data.IModelDataKey;
import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.communi.suggestu.scena.core.client.models.loaders.context.IModelBakingContext;
import com.communi.suggestu.scena.core.client.rendering.type.IRenderTypeManager;
import com.communi.suggestu.scena.core.util.ConcatenatedListView;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CombiningModel implements IModelSpecification<CombiningModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final ImmutableMap<String, BlockModel> children;
    private final boolean logWarning;

    public CombiningModel(ImmutableMap<String, BlockModel> children)
    {
        this(children, false);
    }

    private CombiningModel(ImmutableMap<String, BlockModel> children, boolean logWarning)
    {
        this.children = children;
        this.logWarning = logWarning;
    }

    @Override
    public BakedModel bake(IModelBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation)
    {
        if (logWarning)
            LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated \"parts\" field in its composite model instead of \"children\". This field will be removed in 1.20.");

        Material particleLocation = context.getMaterial("particle").orElse(null);
        TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

        var bakedPartsBuilder = ImmutableMap.<String, BakedModel>builder();
        for (var entry : children.entrySet())
        {
            var name = entry.getKey();
            var model = entry.getValue();
            bakedPartsBuilder.put(name, model.bake(bakery, model, spriteGetter, modelTransform, modelLocation, true));
        }
        var bakedParts = bakedPartsBuilder.build();

        return new Baked(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, context.getTransforms(), context.getItemOverrides(), bakedParts);
    }

    @Override
    public Collection<Material> getTextures(IModelBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> textures = new HashSet<>();
        final Optional<Material> particleTexture = context.getMaterial("particle");
        particleTexture.ifPresent(textures::add);
        for (BlockModel part : children.values())
        {
            textures.addAll(part.getMaterials(modelGetter, missingTextureErrors));
        }
        return textures;
    }

    public static class Baked implements IDataAwareBakedModel
    {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final ItemTransforms transforms;
        private final ImmutableMap<String, BakedModel> children;

        public Baked(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, ImmutableMap<String, BakedModel> children)
        {
            this.children = children;
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.transforms = transforms;
        }

        @NotNull
        private static Collection<RenderType> getRenderTypes(final BakedModel model, final BlockState state, final RandomSource rand, final IBlockModelData data)
        {
            if (model instanceof IDataAwareBakedModel dataAwareBakedModel)
            {
                return dataAwareBakedModel.getSupportedRenderTypes(state, rand, data);
            }

            if (model instanceof IDelegatingBakedModel delegatingBakedModel)
            {
                return getRenderTypes(delegatingBakedModel.getDelegate(), state, rand, data);
            }

            return IRenderTypeManager.getInstance().getRenderTypesFor(model, state, rand, data);
        }

        public static Builder builder(IModelBakingContext owner, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms)
        {
            return builder(owner.useAmbientOcclusion(), owner.isGui3d(), owner.useBlockLight(), particle, overrides, cameraTransforms);
        }

        public static Builder builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms)
        {
            return new Builder(isAmbientOcclusion, isGui3d, isSideLit, particle, overrides, cameraTransforms);
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction side, @NotNull final RandomSource rand, @NotNull final IBlockModelData extraData, @Nullable RenderType renderType)
        {
            List<List<BakedQuad>> quadLists = new ArrayList<>();
            for (Map.Entry<String, BakedModel> entry : children.entrySet())
            {
                if (renderType == null || (state != null && getRenderTypes(entry.getValue(), state, rand, extraData).contains(renderType)))
                {
                    if (entry.getValue() instanceof IDataAwareBakedModel dataAwareBakedModel)
                    {
                        quadLists.add(dataAwareBakedModel.getQuads(state, side, rand, CombiningModel.Data.resolve(extraData, entry.getKey()), renderType));
                    } else
                    {
                        quadLists.add(entry.getValue().getQuads(state, side, rand));
                    }
                }
            }
            return ConcatenatedListView.of(quadLists);
        }

        @Override
        public boolean useAmbientOcclusion()
        {
            return isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return isGui3d;
        }

        @Override
        public boolean usesBlockLight()
        {
            return isSideLit;
        }

        @Override
        public boolean isCustomRenderer()
        {
            return false;
        }

        @Override
        public @NotNull TextureAtlasSprite getParticleIcon()
        {
            return particle;
        }

        @Override
        public @NotNull ItemOverrides getOverrides()
        {
            return overrides;
        }

        @Override
        public @NotNull ItemTransforms getTransforms()
        {
            return transforms;
        }

        @Override
        public @NotNull Collection<RenderType> getSupportedRenderTypes(final BlockState state, final RandomSource rand, final IBlockModelData data)
        {
            var sets = new ArrayList<Collection<RenderType>>();
            for (Map.Entry<String, BakedModel> entry : children.entrySet())
            {
                if (entry.getValue() instanceof IDataAwareBakedModel dataAwareBakedModel)
                {
                    sets.add(dataAwareBakedModel.getSupportedRenderTypes(state, rand, CombiningModel.Data.resolve(data, entry.getKey())));
                }
            }
            return sets.stream().flatMap(Collection::stream).collect(Collectors.toSet());
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable final BlockState pState, @Nullable final Direction pDirection, final @NotNull RandomSource pRandom)
        {
            return getQuads(pState, pDirection, pRandom, IBlockModelData.empty(), null);
        }

        @Nullable
        public BakedModel getPart(String name)
        {
            return children.get(name);
        }

        public static class Builder implements IModelBuilder<Builder>
        {
            private final boolean isAmbientOcclusion;
            private final boolean isGui3d;
            private final boolean isSideLit;
            private final List<BakedModel> children = new ArrayList<>();
            private final List<BakedQuad> quads = new ArrayList<>();
            private final ItemOverrides overrides;
            private final ItemTransforms transforms;
            private TextureAtlasSprite particle;
            private RenderTypeGroup lastRenderTypes = RenderTypeGroup.EMPTY;

            private Builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms transforms)
            {
                this.isAmbientOcclusion = isAmbientOcclusion;
                this.isGui3d = isGui3d;
                this.isSideLit = isSideLit;
                this.particle = particle;
                this.overrides = overrides;
                this.transforms = transforms;
            }

            public void addLayer(BakedModel model)
            {
                flushQuads(null);
                children.add(model);
            }

            private void addLayer(RenderTypeGroup renderTypes, List<BakedQuad> quads)
            {
                var modelBuilder = IModelBuilder.of(isAmbientOcclusion, isSideLit, isGui3d, transforms, overrides, particle, renderTypes);
                quads.forEach(q -> modelBuilder.addUnculledFace(renderTypes, q));
                children.add(modelBuilder.build());
            }

            private void flushQuads(RenderTypeGroup renderTypes)
            {
                if (!Objects.equals(renderTypes, lastRenderTypes))
                {
                    if (quads.size() > 0)
                    {
                        addLayer(lastRenderTypes, quads);
                        quads.clear();
                    }
                    lastRenderTypes = renderTypes;
                }
            }

            public Builder setParticle(TextureAtlasSprite particleSprite)
            {
                this.particle = particleSprite;
                return this;
            }

            @Override
            public Builder addCulledFace(final RenderTypeGroup group, final Direction facing, final BakedQuad quad)
            {
                flushQuads(group);
                quads.add(quad);
                return this;
            }

            @Override
            public Builder addUnculledFace(final RenderTypeGroup group, final BakedQuad quad)
            {
                flushQuads(null);
                quads.add(quad);
                return this;
            }

            public BakedModel build()
            {
                if (quads.size() > 0)
                {
                    addLayer(lastRenderTypes, quads);
                }
                var childrenBuilder = ImmutableMap.<String, BakedModel>builder();
                var itemPassesBuilder = ImmutableList.<BakedModel>builder();
                int i = 0;
                for (var model : this.children)
                {
                    childrenBuilder.put("model_" + (i++), model);
                    itemPassesBuilder.add(model);
                }
                return new Baked(isGui3d, isSideLit, isAmbientOcclusion, particle, transforms, overrides, childrenBuilder.build());
            }
        }

    }

    /**
     * A model data container which stores data for child components.
     */
    public static class Data
    {
        public static final IModelDataKey<Data> PROPERTY = IModelDataKey.create();

        private final Map<String, IBlockModelData> partData;

        private Data(Map<String, IBlockModelData> partData)
        {
            this.partData = partData;
        }

        /**
         * Helper to get the data from a {@link IBlockModelData} instance.
         *
         * @param modelData The object to get data from
         * @param name      The name of the part to get data for
         * @return The data for the part, or the one passed in if not found
         */
        public static IBlockModelData resolve(IBlockModelData modelData, String name)
        {
            var compositeData = modelData.getData(PROPERTY);
            if (compositeData == null)
                return modelData;
            var partData = compositeData.get(name);
            return partData != null ? partData : modelData;
        }

        public static Builder builder()
        {
            return new Builder();
        }

        @Nullable
        public IBlockModelData get(String name)
        {
            return partData.get(name);
        }

        public static final class Builder
        {
            private final Map<String, IBlockModelData> partData = new IdentityHashMap<>();

            public Builder with(String name, IBlockModelData data)
            {
                partData.put(name, data);
                return this;
            }

            public Data build()
            {
                return new Data(partData);
            }
        }
    }

    public static final class Loader implements IModelSpecificationLoader<CombiningModel>
    {
        public static final Loader INSTANCE = new Loader();

        private Loader()
        {
        }

        @Override
        public CombiningModel read(JsonDeserializationContext deserializationContext, JsonObject jsonObject)
        {
            ImmutableMap.Builder<String, BlockModel> childrenBuilder = ImmutableMap.builder();
            readChildren(jsonObject, "children", deserializationContext, childrenBuilder, false);
            boolean logWarning = readChildren(jsonObject, "parts", deserializationContext, childrenBuilder, true);

            var children = childrenBuilder.build();
            if (children.isEmpty())
                throw new JsonParseException("Composite model requires a \"children\" element with at least one element.");

            return new CombiningModel(children, logWarning);
        }

        private boolean readChildren(JsonObject jsonObject, String name, JsonDeserializationContext deserializationContext, ImmutableMap.Builder<String, BlockModel> children, boolean logWarning)
        {
            if (!jsonObject.has(name))
                return false;

            var childrenJsonObject = jsonObject.getAsJsonObject(name);
            for (Map.Entry<String, JsonElement> entry : childrenJsonObject.entrySet())
            {
                children.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), BlockModel.class));
            }
            return logWarning;
        }
    }
}
