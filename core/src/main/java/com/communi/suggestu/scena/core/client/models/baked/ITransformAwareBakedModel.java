package com.communi.suggestu.scena.core.client.models.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Defines a baked model which is aware of transform data and can apply this data dynamically.
 */
public interface ITransformAwareBakedModel extends BakedModel {

    /**
     * Invoked by the rendering engine to dynamically apply the perspective changes.
     *
     * @param cameraTransformType The camera transforms to apply.
     * @param mat The matrix stack for posing of the model.
     * @return The model to render with the transforms applied to the stack.
     */
    BakedModel handlePerspective(ItemDisplayContext cameraTransformType, PoseStack mat);
}
