package com.communi.suggestu.scena.fabric.platform.client.keys;

import com.communi.suggestu.scena.core.client.key.IKeyBindingManager;
import com.communi.suggestu.scena.core.client.key.IKeyConflictContext;
import com.communi.suggestu.scena.core.client.key.KeyModifier;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class FabricKeyBindingManager implements IKeyBindingManager
{
    private static final FabricKeyBindingManager INSTANCE = new FabricKeyBindingManager();

    public static FabricKeyBindingManager getInstance()
    {
        return INSTANCE;
    }

    private FabricKeyBindingManager()
    {
    }

    @Override
    public void register(final KeyMapping mapping)
    {
        KeyBindingHelper.registerKeyBinding(mapping);
    }

    @Override
    public IKeyConflictContext getGuiKeyConflictContext()
    {
        return FabricGuiKeyConflictContext.INSTANCE;
    }

    @Override
    public KeyMapping createNew(
      final String translationKey, final IKeyConflictContext keyConflictContext, final InputConstants.Type inputType, final int key, final String groupTranslationKey)
    {
        return new KeyMapping(
          translationKey,
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
        return new ModifiedKeyMapping(translationKey, inputType, key, groupTranslationKey, keyConflictContext, keyModifier);
    }

    @Override
    public boolean isKeyConflictOfActive(final KeyMapping keybinding)
    {
        if (keybinding instanceof ModifiedKeyMapping modifiedKeyMapping) {
            return modifiedKeyMapping.context.isActive();
        }

        return true;
    }

    @Override
    public boolean isKeyModifierActive(final KeyMapping keybinding)
    {
        if (keybinding instanceof ModifiedKeyMapping modifiedKeyMapping) {
            return modifiedKeyMapping.isKeyModifierActive();
        }

        return true;
    }

    private static final class FabricGuiKeyConflictContext implements IKeyConflictContext {

        private static final FabricGuiKeyConflictContext INSTANCE = new FabricGuiKeyConflictContext();

        private FabricGuiKeyConflictContext()
        {
        }

        @Override
        public boolean isActive()
        {
            return Minecraft.getInstance().screen != null;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return this == other;
        }
    }

    private static class ModifiedKeyMapping extends KeyMapping
    {
        private final IKeyConflictContext context;
        private final KeyModifier keyModifier;

        public ModifiedKeyMapping(
          final String translationKey,
          final InputConstants.Type inputType,
          final int key,
          final String groupTranslationKey,
          final IKeyConflictContext context,
          final KeyModifier keyModifier)
        {
            super(translationKey,
              inputType,
              key,
              groupTranslationKey);
            this.context = context;
            this.keyModifier = keyModifier;
        }

        @Override
        public boolean isDown()
        {
            return super.isDown() && isKeyModifierActive();
        }

        private boolean isKeyModifierActive() {
            return switch (keyModifier) {
                case CONTROL -> Screen.hasControlDown();
                case SHIFT -> Screen.hasShiftDown();
                case ALT -> Screen.hasAltDown();
            };
        }

        @Override
        public Component getTranslatedKeyMessage()
        {
            return getKeyModifierMessage().append(super.getTranslatedKeyMessage());
        }

        private MutableComponent getKeyModifierMessage()
        {
            return switch (keyModifier) {
                case CONTROL -> Component.literal("CTRL + ");
                case SHIFT -> Component.literal("SHIFT + ");
                case ALT -> Component.literal("ALT + ");
            };
        }
    }
}
