package whosalbercik.ccashexchange.networking;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
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
import whosalbercik.ccashexchange.utils.Utils;

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
            Bid bid = (Bid) CCashSavedData.get(ctx.getSender().level).getTransaction(bidId);
            ServerPlayer p = ctx.getSender();
            Player author = bid.getCreator(ctx.getSender().level);

            if (author == null) {
                p.sendSystemMessage(Component.literal("Author is not online, please wait until player joins").withStyle(ChatFormatting.RED));
                return;
            }

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



            // remove items from player
            p.getInventory().clearOrCountMatchingItems((stack) -> stack.getItem().equals(bid.getItemstack().getItem()), bid.getItemstack().getCount(), p.getInventory());

            // give money to accepter
            CCashApi.sendFunds(ServerConfig.MARKET_ACCOUNT.get(), ServerConfig.MARKET_PASS.get(), account, bid.getPrice());

            p.closeContainer();
            p.sendSystemMessage(Component.literal("Successfully accepted Bid!").withStyle(ChatFormatting.GREEN));

            // give items to author
            author.sendSystemMessage(Component.literal("Your bid for ").withStyle(ChatFormatting.GREEN)
                    .append(String.format("x%s %s", bid.getItemstack().getCount(), bid.getItemstack().getItem().getName(bid.getItemstack()).getString())).withStyle(ChatFormatting.AQUA)
                    .append(" Has been accepted!").withStyle(ChatFormatting.GREEN));


            // give items to author
            if (author.getInventory().getSlotWithRemainingSpace(bid.getItemstack()) == -1) {
                p.drop(new ItemStack(bid.getItemstack().getItem(), bid.getItemstack().getCount()), false);
                return;
            }
            else {
                p.getInventory().add(new ItemStack(bid.getItemstack().getItem(), bid.getItemstack().getCount()));
            }


            ListTag tag = author.getPersistentData().getList("ccash.transactions", 10);
            tag.remove(Utils.getTransactionNBT(bid));
            p.getPersistentData().put("ccash.transactions", tag);

            CCashSavedData.get(ctx.getSender().level).removeTransaction(bid);

        });

    }
}
