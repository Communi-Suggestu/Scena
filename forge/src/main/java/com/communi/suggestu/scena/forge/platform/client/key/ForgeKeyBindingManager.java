package com.communi.suggestu.scena.forge.platform.client.key;

import com.communi.suggestu.scena.core.client.key.IKeyBindingManager;
import com.communi.suggestu.scena.core.client.key.IKeyConflictContext;
import com.communi.suggestu.scena.core.client.key.KeyModifier;
import com.mojang.blaze3d.platform.InputConstants;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.compress.utils.Lists;

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

    private static net.minecraftforge.client.settings.KeyModifier makePlatformSpecific(final KeyModifier keyModifier) {
        return switch (keyModifier)
                 {
                     case CONTROL -> net.minecraftforge.client.settings.KeyModifier.CONTROL;
                     case SHIFT -> net.minecraftforge.client.settings.KeyModifier.SHIFT;
                     case ALT -> net.minecraftforge.client.settings.KeyModifier.ALT;
                 };
    }
}
