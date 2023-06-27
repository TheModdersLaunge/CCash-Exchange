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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.api.CCashApi;
import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.object.Ask;

public class PlaceAskCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack, CommandBuildContext ctx) {
        stack.register(Commands.literal("ask")
                .then(Commands.argument("price", LongArgumentType.longArg(0))
                        .then(Commands.argument("item", ItemArgument.item(ctx))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("password", StringArgumentType.word()).executes(PlaceAskCommand::placeAsk))))));
    }

    private static int placeAsk(CommandContext<CommandSourceStack> ctx){
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

        if (!p.getInventory().contains(stack)) {
            ctx.getSource().sendFailure(Component.literal("You do not have the required items!"));
            return 0;
        }

        // remove items
        p.getInventory().clearOrCountMatchingItems((invStack) -> stack.getItem().equals(stack.getItem()), stack.getCount(), p.getInventory());

        Ask ask = new Ask(p.getUUID(), stack, price);
        CCashSavedData saved = CCashSavedData.get(p.getServer().overworld());
        saved.saveTransaction(ask);

        ctx.getSource().sendSuccess(Component.literal("Successfully set ask x" + String.valueOf(stack.getCount()) + " " + stack.getItem().getName(stack).getString() + " for " + String.valueOf(price) + "$").withStyle(ChatFormatting.AQUA), false);

        return 0;
    }
}