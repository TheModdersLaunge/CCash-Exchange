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
import whosalbercik.ccashexchange.object.Ask;

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
            Ask ask = (Ask) CCashSavedData.get(ctx.getSender().level).getTransaction(askId);
            ServerPlayer p = ctx.getSender();

            Player author = ask.getCreator(ctx.getSender().level);

            String account = p.getPersistentData().getString("ccash.account");
            String tempPass = p.getPersistentData().getString("ccash.holdPass");
            p.getPersistentData().remove("ccash.holdPass");

            if (author == null) {
                p.sendSystemMessage(Component.literal("Author is not online, please wait until player joins").withStyle(ChatFormatting.RED));
                p.closeContainer();
                return;
            }

            String authorAccount = author.getPersistentData().getString("ccash.account");

            if (authorAccount.equals("") || !CCashApi.containsAccount(authorAccount)) {

                author.sendSystemMessage(Component.literal("Someone tried accepted your ask, but your account is not set up, or not found on server").withStyle(ChatFormatting.RED));
                author.sendSystemMessage(Component.literal("Set up account again using ").withStyle(ChatFormatting.RED)
                        .append("/config account").withStyle(ChatFormatting.AQUA)
                        .append(" and try again").withStyle(ChatFormatting.RED));

                p.sendSystemMessage(Component.literal("You tried accepting an ask, but the authors account is not set up or has not been found on the server").withStyle(ChatFormatting.RED));
                p.sendSystemMessage(Component.literal("The author has been informed about this fact").withStyle(ChatFormatting.RED));
                return;

            }

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

            if (CCashApi.getBalance(account).get() < ask.getPrice()) {
                p.sendSystemMessage(Component.literal("You do not have enough money!"));
                p.closeContainer();

                return;
            }


            // give items to player
            p.getInventory().add(new ItemStack(ask.getItemstack().getItem(), ask.getItemstack().getCount()));

            p.closeContainer();
            p.sendSystemMessage(Component.literal("Successfully accepted Ask!").withStyle(ChatFormatting.GREEN));

            // give money to author
            author.sendSystemMessage(Component.literal("Your ask for ").withStyle(ChatFormatting.GREEN)
                    .append(String.format("x%s %s", ask.getItemstack().copy().getCount(), ask.getItemstack().copy().getItem().getName(ask.getItemstack().copy()).getString())).withStyle(ChatFormatting.AQUA)
                    .append(" Has been accepted!").withStyle(ChatFormatting.GREEN));

            CCashApi.sendFunds(account, tempPass, authorAccount, ask.getPrice());


            CCashSavedData.get(ctx.getSender().level).removeTransaction(ask);

        });

    }
}
