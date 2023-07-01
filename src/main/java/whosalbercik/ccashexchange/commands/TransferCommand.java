package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import whosalbercik.ccashexchange.api.CCashApi;

public class TransferCommand {

    public static void register(CommandDispatcher<CommandSourceStack> ctx) {
        ctx.register(Commands.literal("transfer")
                .then(Commands.argument("receiver", StringArgumentType.word())
                        .then(Commands.argument("amount", LongArgumentType.longArg(0))
                                .then(Commands.argument("password", StringArgumentType.word())
                                        .executes(TransferCommand::transfer)))));
    }

    private static int transfer(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();

        if (p == null) {
            ctx.getSource().sendFailure(Component.literal("Command must be sent by a player!"));
            return 0;
        }

        String account = p.getPersistentData().getString("ccash.account");

        if (account.equals("")) {
            p.sendSystemMessage(Component.literal("Account not set up! Please use ").append("/config account").withStyle(ChatFormatting.AQUA).append(" to register").withStyle(ChatFormatting.RED));
            return 0;
        }

        if (!CCashApi.containsAccount(account)) {
            p.sendSystemMessage(Component.literal("Account not found on server!").withStyle(ChatFormatting.RED));
            p.sendSystemMessage(Component.literal("Set up account again using ").withStyle(ChatFormatting.RED)
                    .append("/config account").withStyle(ChatFormatting.AQUA)
                    .append(" and try again").withStyle(ChatFormatting.RED));
            return 0;
        }

        if (!CCashApi.containsAccount(ctx.getArgument("receiver", String.class))) {
            ctx.getSource().sendFailure(Component.literal("Receiver account does not exist!"));
            return 0;
        }

        if (ctx.getArgument("amount", long.class) > CCashApi.getBalance(account).get()) {
            ctx.getSource().sendFailure(Component.literal("Your account does not have enough money!"));
            return  0;
        }



        if (CCashApi.sendFunds(account, ctx.getArgument("password", String.class), ctx.getArgument("receiver", String.class), ctx.getArgument("amount", long.class)).isValue2()) {
            ctx.getSource().sendFailure(Component.literal("Invalid password!"));
            return 0;
        }

        ctx.getSource().sendSuccess(Component.literal("Transfer has been completed successfully!").withStyle(ChatFormatting.GREEN), false);

        return 0;
    }
}

