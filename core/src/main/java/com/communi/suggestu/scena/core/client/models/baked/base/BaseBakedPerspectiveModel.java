package com.communi.suggestu.scena.core.client.models.baked.base;

import com.communi.suggestu.scena.core.client.models.baked.ITransformAwareBakedModel;
import com.communi.suggestu.scena.core.client.utils.TransformationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.NotNull;

public abstract class BaseBakedPerspectiveModel implements BakedModel, ITransformAwareBakedModel
{
	private static final Transformation ground;
	private static final Transformation gui;
	private static final Transformation fixed;
	private static final Transformation firstPerson_righthand;
	private static final Transformation firstPerson_lefthand;
	private static final Transformation thirdPerson_righthand;
	private static final Transformation thirdPerson_lefthand;

	static
	{
		gui = getMatrix( 0, 0, 0, 30, 225, 0, 0.625f );
		ground = getMatrix( 0, 3 / 16.0f, 0, 0, 0, 0, 0.25f );
		fixed = getMatrix( 0, 0, 0, 0, 0, 0, 0.5f );
		thirdPerson_lefthand = thirdPerson_righthand = getMatrix( 0, 2.5f / 16.0f, 0, 75, 45, 0, 0.375f );
		firstPerson_righthand = firstPerson_lefthand = getMatrix( 0, 0, 0, 0, 45, 0, 0.40f );
	}

	private static Transformation getMatrix(
			final float transX,
			final float transY,
			final float transZ,
			final float rotX,
			final float rotY,
			final float rotZ,
			final float scaleXYZ )
	{
		final Vector3f translation = new Vector3f( transX, transY, transZ );
		final Vector3f scale = new Vector3f( scaleXYZ, scaleXYZ, scaleXYZ );
		final Quaternion rotation = new Quaternion(rotX, rotY, rotZ, true);

		return new Transformation(translation, rotation, scale, null);
	}

    @Override
    public @NotNull ItemTransforms getTransforms()
    {
        return new PerspectiveHandlingItemTransforms(this);
    }

    @Override
    public BakedModel handlePerspective(final ItemTransforms.TransformType cameraTransformType, final PoseStack mat)
    {
        doCameraTransformForType(cameraTransformType, mat, true);

        return this;
    }

    private void doCameraTransformForType(final ItemTransforms.TransformType cameraTransformType, final PoseStack mat, final boolean requiresStackPush)
    {
        switch (cameraTransformType)
        {
            case FIRST_PERSON_LEFT_HAND:
                TransformationUtils.push(mat, firstPerson_lefthand, requiresStackPush);
                break;
            case FIRST_PERSON_RIGHT_HAND:
                TransformationUtils.push(mat, firstPerson_righthand, requiresStackPush);
                break;
            case THIRD_PERSON_LEFT_HAND:
                TransformationUtils.push(mat, thirdPerson_lefthand, requiresStackPush);
                break;
            case THIRD_PERSON_RIGHT_HAND:
                TransformationUtils.push(mat, thirdPerson_righthand, requiresStackPush);
                break;
            case GROUND:
                TransformationUtils.push(mat, ground, requiresStackPush);
                break;
            case GUI:
                TransformationUtils.push(mat, gui, requiresStackPush);
                break;
            case FIXED:
            default:
                TransformationUtils.push(mat, fixed, requiresStackPush);
                break;
        }
    }

    private static final class PerspectiveHandlingItemTransforms extends ItemTransforms {

        private final BaseBakedPerspectiveModel transformAwareBakedModel;

        public PerspectiveHandlingItemTransforms(
          final BaseBakedPerspectiveModel transformAwareBakedModel
        ) {
            super(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
            this.transformAwareBakedModel = transformAwareBakedModel;
        }

        @Override
        public @NotNull ItemTransform getTransform(final @NotNull TransformType transformType)
        {
            return new ItemTransform(
              Vector3f.ZERO,
              Vector3f.ZERO,
              Vector3f.ZERO
            ) {

                @Override
                public void apply(final boolean isLeftHand, final @NotNull PoseStack poseStack)
                {
                    transformAwareBakedModel.doCameraTransformForType(transformType, poseStack, false);
                }
            };
        }
    }

}
