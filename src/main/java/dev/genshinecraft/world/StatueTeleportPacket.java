package dev.genshinecraft.world;

import dev.architectury.networking.NetworkManager;
import dev.genshinecraft.Genshinecraft;
import net.minecraft.server.level.ServerPlayer;
//? if =1.20.1 {
/*import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
//?}

/** Empty C2S command; all inventory checks and destination resolution occur on the server. */
public final class StatueTeleportPacket {
    private StatueTeleportPacket() {}

    //? if =1.20.1 {
    /*private static final ResourceLocation ID = new ResourceLocation(Genshinecraft.MOD_ID, "teleport");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ID, (buffer, context) -> {
            if (context.getPlayer() instanceof ServerPlayer player) {
                context.queue(() -> StatueTeleport.execute(player));
            }
        });
    }

    public static void send() {
        NetworkManager.sendToServer(ID, new FriendlyByteBuf(Unpooled.buffer()));
    }
    *///?} else {
    //? if >=26.3 {
    /*private static final CustomPacketPayload.Type<Request> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "teleport"));
    *///?} else {
    private static final CustomPacketPayload.Type<Request> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, "teleport"));
    //?}
    private record Request() implements CustomPacketPayload {
        @Override
        public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    private static final Request REQUEST = new Request();
    private static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.unit(REQUEST);

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, TYPE, CODEC, (request, context) -> {
            if (context.getPlayer() instanceof ServerPlayer player) {
                context.queue(() -> StatueTeleport.execute(player));
            }
        });
    }

    public static void send() {
        NetworkManager.sendToServer(REQUEST);
    }
    //?}
}
