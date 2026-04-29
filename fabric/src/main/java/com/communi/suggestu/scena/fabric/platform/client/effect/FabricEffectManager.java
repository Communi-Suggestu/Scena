package com.communi.suggestu.scena.fabric.platform.client.effect;

import com.communi.suggestu.scena.core.client.effect.IEffectManager;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FabricEffectManager implements IEffectManager
{
    private static final FabricEffectManager INSTANCE = new FabricEffectManager();

    private final Map<Block, IHitEffectHandler>     hitEffectHandlerMap     = new HashMap<>();
    private final Map<Block, IDestroyEffectHandler> destroyEffectHandlerMap = new HashMap<>();

    public static FabricEffectManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void setupHitEffects(final Consumer<IHitEffectRegistrar> setter)
    {
        setter.accept((handler, blocks) -> Arrays.stream(blocks).forEach(block -> hitEffectHandlerMap.put(block, handler)));
    }

    @Override
    public void setupDestroyEffects(final Consumer<IDestroyEffectRegistrar> setter)
    {
        setter.accept((handler, blocks) -> Arrays.stream(blocks).forEach(block -> destroyEffectHandlerMap.put(block, handler)));
    }

    public Map<Block, IHitEffectHandler> getHitEffectHandlerMap()
    {
        return hitEffectHandlerMap;
    }

    public Map<Block, IDestroyEffectHandler> getDestroyEffectHandlerMap()
    {
        return destroyEffectHandlerMap;
    }
}
