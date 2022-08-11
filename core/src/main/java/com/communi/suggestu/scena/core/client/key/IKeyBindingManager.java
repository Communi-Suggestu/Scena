package com.communi.suggestu.scena.core.client.key;

import com.communi.suggestu.scena.core.client.IClientManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

/**
 * Platform manager for registering new keybindings on the system
 */
public interface IKeyBindingManager
{
    /**
     * The key binding manager for this platform.
     * Allows for the registration of new key bindings.
     *
     * @return The key binding manager.
     */
    static IKeyBindingManager getInstance() {
        return IClientManager.getInstance().getKeyBindingManager();
    }

    /**
     * Registers a new key mapping to the system.
     *
     * @param mapping The new key mapping to register.
     */
    void register(KeyMapping mapping);

    /**
     * The conflict context which detects when a GUI is open.
     *
     * @return The conflict context.
     */
    IKeyConflictContext getGuiKeyConflictContext();

    /**
     * Creates a new key mapping with the given properties.
     * If the system supports it a conflict context is registered as well.
     *
     * @param translationKey The translation key for the key mapping.
     * @param keyConflictContext The optional key conflict context to apply.
     * @param inputType The input type for the key mapping.
     * @param key The default configured key.
     * @param groupTranslationKey The translation key for the group that the key belongs to.
     * @return The new key mapping.
     */
    KeyMapping createNew(String translationKey, IKeyConflictContext keyConflictContext, InputConstants.Type inputType, int key, String groupTranslationKey);

    /**
     * Creates a new key mapping with the given properties.
     * If the system supports it a conflict context is registered as well.
     *
     * @param translationKey The translation key for the key mapping.
     * @param keyConflictContext The optional key conflict context to apply.
     * @param keyModifier The key modifier for the default key.
     * @param inputType The input type for the key mapping.
     * @param key The default configured key.
     * @param groupTranslationKey The translation key for the group that the key belongs to.
     * @return The new key mapping.
     */
    KeyMapping createNew(String translationKey, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, InputConstants.Type inputType, int key, String groupTranslationKey);

    /**
     * Checks if the key conflict context of the key mapping is active or not.
     *
     * @param keybinding The key mapping to check.
     * @return True when the conflict context is active, false when not.
     */
    boolean isKeyConflictOfActive(KeyMapping keybinding);

    /**
     * Checks if the key modifier of the key mapping is active or not.
     *
     * @param keybinding The key mapping to check.
     * @return True when the modifier is active, false when not.
     */
    boolean isKeyModifierActive(KeyMapping keybinding);
}
