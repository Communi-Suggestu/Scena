package com.communi.suggestu.scena.forge.platform.client.key;

import com.communi.suggestu.scena.core.client.key.IKeyBindingManager;
import com.communi.suggestu.scena.core.client.key.IKeyConflictContext;
import com.communi.suggestu.scena.core.client.key.KeyModifier;
import com.mojang.blaze3d.platform.InputConstants;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.minecraft.client.KeyMapping;
import com.google.common.collect.Lists;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeKeyBindingManager implements IKeyBindingManager
{
    private static final ForgeKeyBindingManager INSTANCE = new ForgeKeyBindingManager();

    public static ForgeKeyBindingManager getInstance()
    {
        return INSTANCE;
    }

    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final List<KeyMapping> mappingsToRegister = Lists.newArrayList();

    private ForgeKeyBindingManager()
    {
    }

    @Override
    public void register(final KeyMapping mapping)
    {
        if (isInitialized.get())
            throw new IllegalStateException("Tried to register keymappings to late!");

        mappingsToRegister.add(mapping);
    }

    @SubscribeEvent
    public static void handleKeyMappingRegistration(final RegisterKeyMappingsEvent event) {
        ForgeKeyBindingManager.getInstance().isInitialized.set(true);
        ForgeKeyBindingManager.getInstance().mappingsToRegister.forEach(event::register);
    }

    @Override
    public IKeyConflictContext getGuiKeyConflictContext()
    {
        return new ForgeKeyConflictContextPlatformDelegate(KeyConflictContext.GUI);
    }

    @Override
    public KeyMapping createNew(
      final String translationKey, final IKeyConflictContext keyConflictContext, final InputConstants.Type inputType, final int key, final String groupTranslationKey)
    {
        return new KeyMapping(
          translationKey,
          new PlatformKeyConflictContextForgeDelegate(keyConflictContext),
          inputType,
          key,
          groupTranslationKey
        );
    }

    @Override
    public KeyMapping createNew(
      final String translationKey,
      final IKeyConflictContext keyConflictContext,
      final KeyModifier keyModifier,
      final InputConstants.Type inputType,
      final int key,
      final String groupTranslationKey)
    {
        return new KeyMapping(
          translationKey,
          new PlatformKeyConflictContextForgeDelegate(keyConflictContext),
          makePlatformSpecific(keyModifier),
          inputType,
          key,
          groupTranslationKey
        );
    }

    @Override
    public boolean isKeyConflictOfActive(final KeyMapping keybinding)
    {
        return keybinding.getKeyConflictContext().isActive();
    }

    @Override
    public boolean isKeyModifierActive(final KeyMapping keybinding)
    {
        return keybinding.getKeyModifier().isActive(keybinding.getKeyConflictContext());
    }

    private static net.neoforged.neoforge.client.settings.KeyModifier makePlatformSpecific(final KeyModifier keyModifier) {
        return switch (keyModifier)
                 {
                     case CONTROL ->  net.neoforged.neoforge.client.settings.KeyModifier.CONTROL;
                     case SHIFT ->  net.neoforged.neoforge.client.settings.KeyModifier.SHIFT;
                     case ALT ->  net.neoforged.neoforge.client.settings.KeyModifier.ALT;
                 };
    }
}
