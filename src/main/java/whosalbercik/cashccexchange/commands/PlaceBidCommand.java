package whosalbercik.cashccexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PlaceBidCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("bid")
                .then(Commands.argument("price", IntegerArgumentType.integer(0)).executes(PlaceBidCommand::placeBid)));
    }

    private static int placeBid(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {

        return 0;
    }
}
