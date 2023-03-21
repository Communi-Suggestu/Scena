package com.communi.suggestu.scena.fabric.platform.client.rendering.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public interface IBlockModelAccessor
{

    List<BlockElement> elements();

    BlockModel.GuiLight guiLight();

    boolean usesAmbientOcclusion();

    ItemTransforms transforms();

    List<ItemOverride> overrides();

    String name();

    Map<String, Either<Material, String>> textureMap();

    BlockModel parent();

    ResourceLocation parentLocation();

    ItemOverrides overrides(ModelBaker modelBakery, BlockModel model);
}
