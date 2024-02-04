package com.communi.suggestu.scena.core.init;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.google.common.collect.Lists;;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class PlatformInitializationHandler
{
    private static final PlatformInitializationHandler INSTANCE = new PlatformInitializationHandler();

    public static PlatformInitializationHandler getInstance()
    {
        return INSTANCE;
    }

    private AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final Collection<Consumer<IScenaPlatform>> initializers = Collections.synchronizedList(Lists.newArrayList());

    private PlatformInitializationHandler()
    {
    }

    public void onInit(final Consumer<IScenaPlatform> initializeCallback) {
        if (isInitialized.get())
            initializeCallback.accept(IScenaPlatform.getInstance());
        else
            initializers.add(initializeCallback);
    }


    public void init(final IScenaPlatform platform) {
        IScenaPlatform.Holder.setInstance(platform);
        isInitialized.set(true);
        for (final Consumer<IScenaPlatform> initializer : initializers)
            initializer.accept(platform);
    }



}
