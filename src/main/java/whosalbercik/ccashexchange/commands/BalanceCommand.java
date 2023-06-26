package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import whosalbercik.ccashexchange.api.CCashApi;

public class BalanceCommand {

    public static void register(CommandDispatcher<CommandSourceStack> ctx) {
        ctx.register(Commands.literal("balance").executes(BalanceCommand::balance));
    }

    private static int balance(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getPlayer() == null) {
            ctx.getSource().sendFailure(Component.literal("Command must be sent by player!"));
            return 0;
        }

        ServerPlayer p = ctx.getSource().getPlayer();

        String account = p.getPersistentData().getString("ccash.account");

        if (account.equals("")) {
            p.sendSystemMessage(Component.literal("Account not set up! Please use ").append("/config account").withStyle(ChatFormatting.AQUA).append("to register").withStyle(ChatFormatting.RED));
            p.closeContainer();

            return 0;
        }

        if (!CCashApi.containsAccount(account)) {
            p.sendSystemMessage(Component.literal("Account not found on server!").withStyle(ChatFormatting.RED));
            p.sendSystemMessage(Component.literal("Set up account again using ").withStyle(ChatFormatting.RED)
                    .append("/config account").withStyle(ChatFormatting.AQUA)
                    .append(" and try again").withStyle(ChatFormatting.RED));
            p.closeContainer();
            return 0;
        }

        p.sendSystemMessage(Component.literal(String.format("Balance of account \"%s\": ", account)).withStyle(ChatFormatting.GREEN)
                .append(String.format("$%s", CCashApi.getBalance(account).get())).withStyle(ChatFormatting.AQUA));
        return 0;
    }
}
