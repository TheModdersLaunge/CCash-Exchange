package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.api.CCashApi;
import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.object.Bid;

public class PlaceBidCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack, CommandBuildContext ctx) {
        stack.register(Commands.literal("bid")
                .then(Commands.argument("price", LongArgumentType.longArg(0))
                        .then(Commands.argument("item", ItemArgument.item(ctx))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1)).executes(PlaceBidCommand::placeBid)))));
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


        if (account.equals("")) {
            ctx.getSource().sendFailure(Component.literal("Account not set up! Please use ").append("/config account").withStyle(ChatFormatting.AQUA).append("to register").withStyle(ChatFormatting.RED));
            return 0;
        }


        if (!CCashApi.containsAccount(ServerConfig.MARKET_ACCOUNT.get())) CCashApi.addUser(ServerConfig.MARKET_ACCOUNT.get(), ServerConfig.MARKET_PASS.get());

        if (CCashApi.getBalance(account).get() < price) {
            ctx.getSource().sendFailure(Component.literal("You do not have enough money to place this bid"));
        }

        CCashApi.sendFunds(account, ctx.getArgument("password", String.class), "market", price);

        Bid bid = new Bid(p.getUUID(), stack, price);
        CCashSavedData saved = CCashSavedData.get(p.getServer().overworld());
        saved.saveBid(bid);

        ctx.getSource().sendSuccess(Component.literal("Successfully set bid x" + String.valueOf(stack.getCount()) + " " + stack.getItem().getName(stack).getString() + " for " + String.valueOf(price) + "$").withStyle(ChatFormatting.AQUA), false);


        return 0;
    }
}
