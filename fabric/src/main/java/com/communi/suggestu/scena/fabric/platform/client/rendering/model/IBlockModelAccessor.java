package com.communi.suggestu.scena.fabric.platform.client.rendering.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface IBlockModelAccessor
{

    List<BlockElement> getElements();

    BlockModel.GuiLight getGuiLight();

    boolean isHasAmbientOcclusion();

    ItemTransforms getTransforms();

    List<ItemOverride> getOverrides();

    String getName();

    Map<String, Either<Material, String>> getTextureMap();

    BlockModel getParent();

    ResourceLocation getParentLocation();

    ItemOverrides getOverrides(ModelBakery modelBakery, BlockModel model);
}
