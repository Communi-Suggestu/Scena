package com.communi.suggestu.scena.forge.platform.client.key;

import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import org.jetbrains.annotations.NotNull;

public class PlatformKeyConflictContextForgeDelegate implements IKeyConflictContext
{
    private final com.communi.suggestu.scena.core.client.key.IKeyConflictContext delegate;

    public PlatformKeyConflictContextForgeDelegate(final com.communi.suggestu.scena.core.client.key.IKeyConflictContext delegate) {this.delegate = delegate;}

    @Override
    public boolean isActive()
    {
        return delegate.isActive();
    }

    @Override
    public boolean conflicts(final @NotNull IKeyConflictContext other)
    {
        return delegate.conflicts(new ForgeKeyConflictContextPlatformDelegate(other));
    }
}
