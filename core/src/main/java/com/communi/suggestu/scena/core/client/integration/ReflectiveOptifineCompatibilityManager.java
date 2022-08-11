package com.communi.suggestu.scena.core.client.integration;

public final class ReflectiveOptifineCompatibilityManager implements IOptifineCompatibilityManager
{
    private static final ReflectiveOptifineCompatibilityManager INSTANCE = new ReflectiveOptifineCompatibilityManager();

    public static ReflectiveOptifineCompatibilityManager getInstance()
    {
        return INSTANCE;
    }

    private boolean initialized = false;
    private boolean isInstalled = false;

    private ReflectiveOptifineCompatibilityManager()
    {
    }

    private void initialize() {
        if (initialized)
            return;

        initialized = true;

        try
        {
            Class.forName("net.optifine.Config");
            isInstalled = true;
        }
        catch (ClassNotFoundException e)
        {
            isInstalled = false;
        }
    }

    @Override
    public boolean isInstalled()
    {
        initialize();
        return isInstalled;
    }
}
