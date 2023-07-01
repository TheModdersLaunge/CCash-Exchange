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
import whosalbercik.ccashexchange.object.AskTransaction;
import whosalbercik.ccashexchange.object.UnCompletedTransaction;
import whosalbercik.ccashexchange.utils.UnCompletedQueue;
import whosalbercik.ccashexchange.utils.Utils;

import java.util.function.Supplier;

public class AskAcceptCs2Packet {
    int askId;

    public AskAcceptCs2Packet(int askId) {
        this.askId = askId;
    }

    public static AskAcceptCs2Packet decode(FriendlyByteBuf buf) {
        int id = buf.readInt();

        return new AskAcceptCs2Packet(id);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(askId);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            AskTransaction ask = (AskTransaction) CCashSavedData.get(ctx.getSender().level).getTransaction(askId);
            ServerPlayer p = ctx.getSender();

            Player author = ask.getCreator(ctx.getSender().level);

            String account = p.getPersistentData().getString("ccash.account");
            String tempPass = p.getPersistentData().getString("ccash.holdPass");
            p.getPersistentData().remove("ccash.holdPass");


            if (!CCashApi.verifyPassword(account, tempPass)) {
                p.sendSystemMessage(Component.literal("Incorrect password!").withStyle(ChatFormatting.RED));
                return;
            }

            // not found on server
            if (!CCashApi.containsAccount(account)) {
                p.sendSystemMessage(Component.literal("Account not found on server!").withStyle(ChatFormatting.RED));
                p.sendSystemMessage(Component.literal("Set up account again using ").withStyle(ChatFormatting.RED)
                        .append("/config account").withStyle(ChatFormatting.AQUA)
                        .append(" and try again").withStyle(ChatFormatting.RED));
                p.closeContainer();

                return;
            }

            // not enough money
            if (CCashApi.getBalance(account).get() < ask.getPrice() * ask.getItemstack().getCount()) {
                p.sendSystemMessage(Component.literal("You do not have enough money!"));
                p.closeContainer();

                return;
            }

            // not set up
            if (account.equals("")) {
                p.sendSystemMessage(Component.literal("Account not set up! Please use ").append("/config account").withStyle(ChatFormatting.AQUA).append("to register").withStyle(ChatFormatting.RED));
                p.closeContainer();

                return;
            }

            // author online
            if (author != null) {

                String authorAccount = author.getPersistentData().getString("ccash.account");

                // author does not have account set up/not found on server
                if (authorAccount.equals("") || !CCashApi.containsAccount(authorAccount)) {

                    author.sendSystemMessage(Component.literal("Someone tried accepted your ask, but your account is not set up, or not found on server").withStyle(ChatFormatting.RED));
                    author.sendSystemMessage(Component.literal("Set up account again using ").withStyle(ChatFormatting.RED)
                            .append("/config account").withStyle(ChatFormatting.AQUA)
                            .append(" and try again").withStyle(ChatFormatting.RED));

                    p.sendSystemMessage(Component.literal("You tried accepting an ask, but the authors account is not set up or has not been found on the server").withStyle(ChatFormatting.RED));
                    p.sendSystemMessage(Component.literal("The author has been informed about this fact").withStyle(ChatFormatting.RED));
                    return;

                }

                // give money to author
                author.sendSystemMessage(Component.literal("Your ask for ").withStyle(ChatFormatting.GREEN)
                        .append(String.format("x%s %s", ask.getItemstack().copy().getCount(), ask.getItemstack().copy().getItem().getName(ask.getItemstack().copy()).getString())).withStyle(ChatFormatting.AQUA)
                        .append(" Has been accepted!").withStyle(ChatFormatting.GREEN));

                CCashApi.sendFunds(account, tempPass, authorAccount, ask.getPrice());


                ListTag tag = author.getPersistentData().getList("ccash.transactions", 10);
                tag.remove(Utils.getTransactionNBT(ask));
                author.getPersistentData().put("ccash.transactions", tag);
            }
            // if author is offline, send money to market, and later the money from market will be transferred to author
            else {
                UnCompletedQueue.get(p.getServer().overworld()).saveTransaction(new UnCompletedTransaction(ask, ask.getPrice()));
                CCashApi.sendFunds(account, tempPass, "market", ask.getPrice() * ask.getItemstack().getCount());

            }


            // give items to player
            if (p.getInventory().getFreeSlot() == -1 && p.getInventory().getSlotWithRemainingSpace(ask.getItemstack()) == -1) {
                p.drop(new ItemStack(ask.getItemstack().getItem(), ask.getItemstack().getCount()), false);
            }
            else {
                p.getInventory().add(new ItemStack(ask.getItemstack().getItem(), ask.getItemstack().getCount()));
            }




            p.closeContainer();
            p.sendSystemMessage(Component.literal("Successfully accepted Ask!").withStyle(ChatFormatting.GREEN));

            CCashSavedData.get(ctx.getSender().level).removeTransaction(ask);

        });

    }
}
