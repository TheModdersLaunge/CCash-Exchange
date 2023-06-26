package whosalbercik.ccashexchange.networking;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.api.CCashApi;
import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.object.Bid;
import java.util.function.Supplier;

public class BidAcceptC2SPacket {
    int bidId;

    public BidAcceptC2SPacket(int bidId) {
        this.bidId = bidId;
    }

    public static BidAcceptC2SPacket decode(FriendlyByteBuf buf) {
        int id = buf.readInt();

        return new BidAcceptC2SPacket(id);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(bidId);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Bid bid = CCashSavedData.get(ctx.getSender().level).getBid(bidId);
            ServerPlayer p = ctx.getSender();

            if (!p.getInventory().contains(bid.getItemstack())) {
                p.sendSystemMessage(Component.literal("You do not have the required items!").withStyle(ChatFormatting.RED));
                p.closeContainer();
                return;
            }

            String account = p.getPersistentData().getString("ccash.account");

            if (account.equals("")) {
                p.sendSystemMessage(Component.literal("Account not set up! Please use ").append("/config account").withStyle(ChatFormatting.AQUA).append("to register").withStyle(ChatFormatting.RED));
                p.closeContainer();

                return;
            }

            if (!CCashApi.containsAccount(account)) {
                p.sendSystemMessage(Component.literal("Account not found on server!").withStyle(ChatFormatting.RED));
                p.sendSystemMessage(Component.literal("Set up account again using ").withStyle(ChatFormatting.RED)
                        .append("/config account").withStyle(ChatFormatting.AQUA)
                        .append(" and try again").withStyle(ChatFormatting.RED));
                p.closeContainer();

                return;
            }

            if (!CCashApi.containsAccount(ServerConfig.MARKET_ACCOUNT.get())) CCashApi.addUser(ServerConfig.MARKET_ACCOUNT.get(), ServerConfig.MARKET_PASS.get());

            // remove input
            p.getInventory().clearOrCountMatchingItems((stack) -> stack.getItem().equals(bid.getItemstack().getItem()), bid.getItemstack().getCount(), p.getInventory());

            // transaction
            CCashApi.sendFunds(ServerConfig.MARKET_ACCOUNT.get(), ServerConfig.MARKET_PASS.get(), account, bid.getPrice());

            p.closeContainer();
            p.sendSystemMessage(Component.literal("Successfully accepted Bid!").withStyle(ChatFormatting.GREEN));

            // give items to author
            Player author = bid.getCreator(ctx.getSender().level);
            author.sendSystemMessage(Component.literal("Your bid for ").withStyle(ChatFormatting.GREEN)
                    .append(String.format("x%s %s", bid.getItemstack().getCount(), bid.getItemstack().getItem().getName(bid.getItemstack()).getString())).withStyle(ChatFormatting.AQUA)
                    .append(" Has been accepted!").withStyle(ChatFormatting.GREEN));


            p.getInventory().add(new ItemStack(bid.getItemstack().getItem(), bid.getItemstack().getCount()));

            CCashSavedData.get(ctx.getSender().level).removeBid(bid);

        });

    }
}
