package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.api.CCashApi;
import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.object.Bid;
import whosalbercik.ccashexchange.utils.Utils;

public class PlaceBidCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack, CommandBuildContext ctx) {
        stack.register(Commands.literal("bid")
                .then(Commands.argument("price", LongArgumentType.longArg(0))
                        .then(Commands.argument("item", ItemArgument.item(ctx))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("password", StringArgumentType.word()).executes(PlaceBidCommand::placeBid))))));
    }

    private static int placeBid(CommandContext<CommandSourceStack> ctx){
        ServerPlayer p = ctx.getSource().getPlayer();

        if (p == null) {
            ctx.getSource().sendFailure(Component.literal("Command can only be executed by player!"));
            return 0;
        }

        ItemStack stack = new ItemStack(ctx.getArgument("item", ItemInput.class).getItem(), ctx.getArgument("count", int.class));
        String account = p.getPersistentData().getString("ccash.account");
        long price = ctx.getArgument("price", long.class);

        if (price == 0)  {
            ctx.getSource().sendFailure(Component.literal("Price cannot be 0!"));
            return 0;

        }

        if (ctx.getArgument("count", int.class) > stack.getMaxStackSize()) {
            ctx.getSource().sendFailure(Component.literal("Count of item is above max stack size!"));
            return 0;
        }

        if (account.equals("")) {
            ctx.getSource().sendFailure(Component.literal("Account not set up! Please use ").append("/config account").withStyle(ChatFormatting.AQUA).append("to register").withStyle(ChatFormatting.RED));
            return 0;
        }

        if (!CCashApi.containsAccount(account)) {
            ctx.getSource().sendFailure(Component.literal("Account can not be found on server!"));
            ctx.getSource().sendFailure(Component.literal("Set up account again using ")
                    .append("/config account").withStyle(ChatFormatting.AQUA)
                    .append(" and try again").withStyle(ChatFormatting.RED));
            return 0;
        }

        if (!CCashApi.containsAccount(ServerConfig.MARKET_ACCOUNT.get())) CCashApi.addUser(ServerConfig.MARKET_ACCOUNT.get(), ServerConfig.MARKET_PASS.get());

        if (CCashApi.getBalance(account).get() < price) {
            ctx.getSource().sendFailure(Component.literal("You do not have enough money to place this bid"));
            return 0;
        }

        ListTag playerTransactions = p.getPersistentData().getList("ccash.transactions", 10);

        // check for max transactions
        if (ServerConfig.TRANSACTIONS_PER_PLAYER.get() != -1) {
            int max = ServerConfig.TRANSACTIONS_PER_PLAYER.get();

            if (playerTransactions.size() > max) {
                ctx.getSource().sendFailure(Component.literal("Max transactions for player has been reached!"));
                return 0;
            }
        }


        CCashApi.sendFunds(account, ctx.getArgument("password", String.class), "market", price);




        Bid bid = new Bid(p.getUUID(), stack, price);
        CCashSavedData saved = CCashSavedData.get(p.getServer().overworld());
        saved.saveTransaction(bid);

        playerTransactions.add(Utils.getTransactionNBT(bid));
        p.getPersistentData().put("ccash.transactions", playerTransactions);

        ctx.getSource().sendSuccess(Component.literal("Successfully set bid x" + String.valueOf(stack.getCount()) + " " + stack.getItem().getName(stack).getString() + " for " + String.valueOf(price) + "$").withStyle(ChatFormatting.AQUA), false);


        return 0;
    }
}
