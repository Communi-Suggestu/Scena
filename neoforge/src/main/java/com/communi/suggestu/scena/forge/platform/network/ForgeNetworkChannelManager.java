package com.communi.suggestu.scena.forge.platform.network;

import com.communi.suggestu.scena.core.network.INetworkChannel;
import com.communi.suggestu.scena.core.network.INetworkChannelManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeNetworkChannelManager implements INetworkChannelManager
{
    private static final ForgeNetworkChannelManager INSTANCE = new ForgeNetworkChannelManager();

    public static ForgeNetworkChannelManager getInstance()
    {
        return INSTANCE;
    }

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final ConcurrentMap<ResourceLocation, ForgeSimpleChannelPlatformDelegate> channels = new ConcurrentHashMap<>();

    private ForgeNetworkChannelManager()
    {
    }

    @SubscribeEvent
    public static void onRegisterNetworkChannels(final RegisterPayloadHandlerEvent event)
    {
        getInstance().initialized.set(true);
        getInstance().channels.forEach((name, channel) -> {
            final IPayloadRegistrar registrar = event.registrar(name.getNamespace());
            registrar.versioned(channel.getVersion()).common(
                    name,
                    channel::read,
                    channel
            );
        });
    }

    @Override
    public INetworkChannel create(
      final ResourceLocation name, final Supplier<String> networkProtocolVersion, final Predicate<String> clientAcceptedVersions, final Predicate<String> serverAcceptedVersions)
    {
        if (initialized.get())
        {
            throw new IllegalStateException("Can not create a new network channel after the network channel manager has been initialized");
        }

        final ForgeSimpleChannelPlatformDelegate delegate = new ForgeSimpleChannelPlatformDelegate(name, networkProtocolVersion.get());
        channels.put(name, delegate);
        return delegate;
    }
}
