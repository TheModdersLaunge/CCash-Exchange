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
import whosalbercik.ccashexchange.object.BidTransaction;
import whosalbercik.ccashexchange.object.UnCompletedTransaction;
import whosalbercik.ccashexchange.utils.UnCompletedQueue;
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
            BidTransaction bid = (BidTransaction) CCashSavedData.get(ctx.getSender().level).getTransaction(bidId);
            ServerPlayer p = ctx.getSender();
            Player author = bid.getCreator(ctx.getSender().level);


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
            CCashApi.sendFunds(ServerConfig.MARKET_ACCOUNT.get(), ServerConfig.MARKET_PASS.get(), account, bid.getPrice() * bid.getItemstack().getCount());

            p.closeContainer();
            p.sendSystemMessage(Component.literal("Successfully accepted Bid!").withStyle(ChatFormatting.GREEN));



            if (author == null) {
                UnCompletedQueue.get(p.getServer().overworld()).saveTransaction(new UnCompletedTransaction(bid, new ItemStack(bid.getItemstack().getItem(), bid.getItemstack().getCount())));
            }
            // give items to author
            else if (author.getInventory().getFreeSlot() == -1 && author.getInventory().getSlotWithRemainingSpace(bid.getItemstack()) == -1) {

                author.sendSystemMessage(Component.literal("Your bid for ").withStyle(ChatFormatting.GREEN)
                        .append(String.format("x%s %s", bid.getItemstack().getCount(), bid.getItemstack().getItem().getName(bid.getItemstack()).getString())).withStyle(ChatFormatting.AQUA)
                        .append(" Has been accepted!").withStyle(ChatFormatting.GREEN));

                author.drop(new ItemStack(bid.getItemstack().getItem(), bid.getItemstack().getCount()), false);

                ListTag tag = author.getPersistentData().getList("ccash.transactions", 10);
                tag.remove(Utils.getTransactionNBT(bid));
                author.getPersistentData().put("ccash.transactions", tag);

            }
            else {
                author.sendSystemMessage(Component.literal("Your bid for ").withStyle(ChatFormatting.GREEN)
                        .append(String.format("x%s %s", bid.getItemstack().getCount(), bid.getItemstack().getItem().getName(bid.getItemstack()).getString())).withStyle(ChatFormatting.AQUA)
                        .append(" Has been accepted!").withStyle(ChatFormatting.GREEN));

                author.getInventory().add(new ItemStack(bid.getItemstack().getItem(), bid.getItemstack().getCount()));

                ListTag tag = author.getPersistentData().getList("ccash.transactions", 10);
                tag.remove(Utils.getTransactionNBT(bid));
                author.getPersistentData().put("ccash.transactions", tag);
            }



            CCashSavedData.get(ctx.getSender().level).removeTransaction(bid);

        });

    }
}
