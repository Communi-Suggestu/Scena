package com.communi.suggestu.scena.fabric.platform.configuration;

import com.communi.suggestu.scena.core.network.INetworkChannel;
import com.communi.suggestu.scena.core.network.INetworkChannelManager;
import com.google.gson.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class FabricConfigurationNetworkingUtils
{
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private FabricConfigurationNetworkingUtils()
    {
        throw new IllegalStateException("Can not instantiate an instance of: FabricConfigurationNetworkingUtils. This is a utility class");
    }

    public static void registerNetworkingChannel(final Gson gson, Supplier<Map<String, FabricConfigurationSpec>> syncedSourcesProvider) {
        ClientPlayNetworking.registerGlobalReceiver(SyncedConfiguration.TYPE, (payload, context) -> {
            final JsonElement jsonElement = gson.fromJson(payload.specs(), JsonElement.class);
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


    public record SyncedConfiguration(String specs) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<SyncedConfiguration> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation("scena", "synced_config"));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
