package com.communi.suggestu.scena.fabric.platform.configuration;

import com.google.gson.*;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

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
        PayloadTypeRegistry.playS2C().register(SyncedConfiguration.TYPE, SyncedConfiguration.STREAM_CODEC);
        PayloadTypeRegistry.configurationS2C().register(SyncedConfiguration.TYPE, SyncedConfiguration.STREAM_CODEC);

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

        static StreamCodec<ByteBuf, SyncedConfiguration> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
                .map(SyncedConfiguration::new, SyncedConfiguration::specs);

        public static final CustomPacketPayload.Type<SyncedConfiguration> TYPE = new CustomPacketPayload.Type<>(FabricConfigurationManager.CONFIG_SYNC_CHANNEL_ID);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
