package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import whosalbercik.ccashexchange.api.CCashApi;

public class ConfigCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("config")
                .then(Commands.literal("account")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("password", StringArgumentType.word())
                                        .executes(ConfigCommand::account)))));


    }



    private static int account(CommandContext<CommandSourceStack> stack) {

        if (stack.getArgument("name", String.class).length() > 32 || stack.getArgument("name", String.class).length() < 3) {
            stack.getSource().sendFailure(Component.literal("Account name is too long/short!"));
            return 0;
        }

        // registered already
        if (CCashApi.containsAccount(stack.getArgument("name", String.class))) {

            stack.getSource().sendSuccess(Component.literal("Account with such name already registered on server").withStyle(ChatFormatting.AQUA), false);

            if (CCashApi.verifyPassword(stack.getArgument("name", String.class), stack.getArgument("password", String.class))) {
                stack.getSource().sendSuccess(Component.literal("Logged in successfully!").withStyle(ChatFormatting.GREEN), false);

            } else {
                stack.getSource().sendFailure(Component.literal("Incorrect password!").withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        if (stack.getSource().getPlayer() == null) {
            stack.getSource().sendFailure(Component.literal("Command must be sent by player!"));
            return 0;
        }



        ServerPlayer p = stack.getSource().getPlayer();
        p.getPersistentData().put("ccash.account", StringTag.valueOf(stack.getArgument("name", String.class)));
        CCashApi.addUser(stack.getArgument("name", String.class), stack.getArgument("password", String.class));

        stack.getSource().sendSuccess(Component.literal("Successfully updated Account info!").withStyle(ChatFormatting.GREEN), false);
        return 0;
    }



}
