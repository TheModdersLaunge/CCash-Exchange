package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ConfigCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("config")
                .then(Commands.literal("account")
                        .then(Commands.argument("name", StringArgumentType.word()).executes(ConfigCommand::account))));


    }



    private static int account(CommandContext<CommandSourceStack> stack) {
        if (stack.getSource().getPlayer() == null) {
            stack.getSource().sendFailure(Component.literal("Command must be sent by player!"));
            return 0;
        }

        if (stack.getArgument("name", String.class).length() > 32) {
            stack.getSource().sendFailure(Component.literal("Account name is too long!"));
            return 0;
        }

        ServerPlayer p = stack.getSource().getPlayer();
        p.getPersistentData().put("ccash.account", StringTag.valueOf(stack.getArgument("name", String.class)));

        stack.getSource().sendSuccess(Component.literal("Successfully updated Account info!"), false);
        return 0;
    }



}
