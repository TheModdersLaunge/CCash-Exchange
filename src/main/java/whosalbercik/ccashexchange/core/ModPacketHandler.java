package whosalbercik.ccashexchange.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import whosalbercik.ccashexchange.CCashExchange;
import whosalbercik.ccashexchange.networking.AskAcceptCs2Packet;
import whosalbercik.ccashexchange.networking.BidAcceptC2SPacket;
import whosalbercik.ccashexchange.networking.OpenItemMarketC2SPacket;


public class ModPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;


    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(CCashExchange.MODID, "ccash"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        net.messageBuilder(OpenItemMarketC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenItemMarketC2SPacket::decode)
                .encoder(OpenItemMarketC2SPacket::encode)
                .consumerMainThread(OpenItemMarketC2SPacket::handle)
                .add();

        net.messageBuilder(BidAcceptC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(BidAcceptC2SPacket::decode)
                .encoder(BidAcceptC2SPacket::encode)
                .consumerMainThread(BidAcceptC2SPacket::handle)
                .add();

        net.messageBuilder(AskAcceptCs2Packet.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AskAcceptCs2Packet::decode)
                .encoder(AskAcceptCs2Packet::encode)
                .consumerMainThread(AskAcceptCs2Packet::handle)
                .add();

        INSTANCE = net;
    }


    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
