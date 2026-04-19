package com.communi.suggestu.scena.forge.platform.client.model.unbaked;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.forge.platform.client.model.ForgeModelManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UnbakedCustomModelWrapper<T extends BlockStateModel.Unbaked>(
    MapCodec<T> mapCodec,
    @Nullable
    T model
) implements CustomUnbakedBlockStateModel
{
    public UnbakedCustomModelWrapper(final MapCodec<T> mapCodec)
    {
        this(mapCodec, null);
    }

    @Override
    public @NotNull MapCodec<UnbakedCustomModelWrapper<?>> codec()
    {
        final MapCodec<UnbakedCustomModelWrapper<?>> innerCodec = ForgeModelManager.getInstance().wrapUnbakedModelCodec(mapCodec());
        if (innerCodec == null)
            return mapCodec().xmap(
                unbaked -> new UnbakedCustomModelWrapper<>(mapCodec(), unbaked),
                customUnbakedBlockStateModel -> model()
            );

        return innerCodec;
    }

    @Override
    public @NotNull BlockStateModel bake(final @NotNull ModelBaker baker)
    {
        return model().bake(baker);
    }

    @Override
    public void resolveDependencies(final @NotNull Resolver resolver)
    {
        model().resolveDependencies(resolver);
    }
}
