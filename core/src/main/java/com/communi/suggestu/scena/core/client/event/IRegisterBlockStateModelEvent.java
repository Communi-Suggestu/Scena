package com.communi.suggestu.scena.core.client.event;

import com.communi.suggestu.scena.core.event.IEvent;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.resources.ResourceLocation;

public interface IRegisterBlockStateModelEvent extends IEvent
{

    public void handle(Registrar registrar);

    public interface Registrar {
        <T extends BlockStateModel.Unbaked> void registerModel(ResourceLocation location, MapCodec<T> codec);
    }
}
