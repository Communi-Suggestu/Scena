package com.communi.suggestu.scena.fabric.mixin.platform.client;

import com.communi.suggestu.scena.fabric.platform.client.rendering.model.IBlockModelAccessor;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(BlockModel.class)
public abstract class BlockModelAccessorMixin implements IBlockModelAccessor
{
    @Shadow @Final private List<BlockElement> elements;
    @Shadow @Final @Nullable
    private BlockModel.GuiLight guiLight;
    @Shadow @Final public boolean hasAmbientOcclusion;
    @Shadow @Final private ItemTransforms transforms;
    @Shadow @Final private List<ItemOverride> overrides;
    @Shadow public String name;
    @Shadow @Final public Map<String, Either<Material, String>> textureMap;
    @Shadow @Nullable public BlockModel parent;
    @Shadow @Nullable protected ResourceLocation parentLocation;
    @Shadow protected abstract ItemOverrides getItemOverrides(ModelBakery modelBakery, BlockModel model);

    @Override
    public List<BlockElement> getElements()
    {
        return elements;
    }

    @Override
    public BlockModel.GuiLight getGuiLight()
    {
        return guiLight;
    }

    @Override
    public boolean isHasAmbientOcclusion()
    {
        return hasAmbientOcclusion;
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return transforms;
    }

    @Override
    public List<ItemOverride> getOverrides()
    {
        return overrides;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Map<String, Either<Material, String>> getTextureMap()
    {
        return textureMap;
    }

    @Override
    public BlockModel getParent()
    {
        return parent;
    }

    @Override
    public ResourceLocation getParentLocation()
    {
        return parentLocation;
    }

    @Override
    public ItemOverrides getOverrides(final ModelBakery modelBakery, final BlockModel model)
    {
        return this.getItemOverrides(modelBakery, model);
    }
}
