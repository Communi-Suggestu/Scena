package com.communi.suggestu.scena.forge.platform.client.key;

import com.communi.suggestu.scena.core.client.key.IKeyConflictContext;

public class ForgeKeyConflictContextPlatformDelegate implements IKeyConflictContext
{

    private final net.minecraftforge.client.settings.IKeyConflictContext delegate;

    public ForgeKeyConflictContextPlatformDelegate(final net.minecraftforge.client.settings.IKeyConflictContext delegate) {this.delegate = delegate;}

    @Override
    public boolean isActive()
    {
        return delegate.isActive();
    }

    @Override
    public boolean conflicts(final IKeyConflictContext other)
    {
        if (!(other instanceof ForgeKeyConflictContextPlatformDelegate))
            throw new IllegalArgumentException("The given key conflict context is not compatible with the forge platform!");

        return delegate.conflicts(((ForgeKeyConflictContextPlatformDelegate) other).getDelegate());
    }

    public net.minecraftforge.client.settings.IKeyConflictContext getDelegate()
    {
        return delegate;
    }
}
