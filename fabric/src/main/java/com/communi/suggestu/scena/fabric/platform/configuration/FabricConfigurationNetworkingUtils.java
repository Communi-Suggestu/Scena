package com.communi.suggestu.scena.fabric.platform.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class FabricConfigurationNetworkingUtils
{

    private FabricConfigurationNetworkingUtils()
    {
        throw new IllegalStateException("Can not instantiate an instance of: FabricConfigurationNetworkingUtils. This is a utility class");
    }

    public static void registerNetworkingChannel(final ResourceLocation channelName, final Gson gson, Supplier<Map<String, FabricConfigurationSpec>> syncedSourcesProvider) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, (minecraft, clientPacketListener, friendlyByteBuf, packetSender) -> {
            final JsonElement jsonElement = gson.fromJson(friendlyByteBuf.readUtf(Integer.MAX_VALUE / 4), JsonElement.class);
            if (!jsonElement.isJsonObject())
                throw new JsonParseException("The synced configs must be send in an object!");

            final JsonObject jsonObject = jsonElement.getAsJsonObject();

            syncedSourcesProvider.get().forEach((key, spec) -> {
                spec.reset();
                if (jsonObject.has(key)) {
                    final JsonElement specData = jsonObject.get(key);
                    if (!specData.isJsonObject())
                        throw new JsonParseException("A single synced config must be send in an object!");

                    spec.loadFrom(specData.getAsJsonObject());
                }
            });
        });
    }
}
