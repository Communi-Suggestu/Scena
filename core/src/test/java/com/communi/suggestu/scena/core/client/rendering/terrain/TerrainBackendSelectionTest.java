package com.communi.suggestu.scena.core.client.rendering.terrain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TerrainBackendSelectionTest
{
    @Test
    void neverEnablesVanillaHookWhenSodiumIsDetected()
    {
        assertFalse(TerrainBackendSelection.enableVanillaHook(true, true));
    }

    @Test
    void leavesHookDisabledUnlessExplicitlyRequested()
    {
        assertFalse(TerrainBackendSelection.enableVanillaHook(false, false));
    }

    @Test
    void enablesRequestedHookOnlyWhenSodiumIsAbsent()
    {
        assertTrue(TerrainBackendSelection.enableVanillaHook(true, false));
    }
}
