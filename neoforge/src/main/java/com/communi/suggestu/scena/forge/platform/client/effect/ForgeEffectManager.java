package com.communi.suggestu.scena.forge.platform.client.effect;

import com.communi.suggestu.scena.core.client.effect.IEffectManager;
import com.communi.suggestu.scena.forge.platform.client.ForgeClientBlockExtensionManager;

import java.util.function.Consumer;

public class ForgeEffectManager implements IEffectManager
{
    private static final ForgeEffectManager INSTANCE = new ForgeEffectManager();

    public static ForgeEffectManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void setupHitEffects(final Consumer<IHitEffectRegistrar> setter)
    {
        ForgeClientBlockExtensionManager.getInstance()
            .setupHitEffectRegistrar(setter);
    }

    @Override
    public void setupDestroyEffects(final Consumer<IDestroyEffectRegistrar> setter)
    {
        ForgeClientBlockExtensionManager.getInstance()
            .setupDestroyEffectRegistrar(setter);
    }
}
