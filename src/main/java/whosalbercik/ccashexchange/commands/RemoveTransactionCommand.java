package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.object.Transaction;
import whosalbercik.ccashexchange.utils.Utils;

public class RemoveTransactionCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("remove")
                .then(Commands.argument("id", IntegerArgumentType.integer(0))
                        .executes(RemoveTransactionCommand::remove)));
    }

    private static int remove(CommandContext<CommandSourceStack> ctx) {
        Transaction transaction = CCashSavedData.get(ctx.getSource().getLevel()).getTransaction(ctx.getArgument("id", int.class));
        ServerPlayer p = ctx.getSource().getPlayer();

        if (transaction == null) {
            ctx.getSource().sendFailure(Component.literal("Transaction not found!"));
            return 0;
        }

        if (p == null) {
            ctx.getSource().sendFailure(Component.literal("Command can only be executed by player!"));
            return 0;
        }

        if (!p.getPersistentData().getList("ccash.transactions", 10).contains(Utils.getTransactionNBT(transaction))) {
            ctx.getSource().sendFailure(Component.literal("Transaction was not created by you!"));
            return 0;
        }

        ListTag tag = p.getPersistentData().getList("ccash.transactions", 10);
        tag.remove(Utils.getTransactionNBT(transaction));
        p.getPersistentData().put("ccash.transactions", tag);

        CCashSavedData.get(ctx.getSource().getLevel()).removeTransaction(transaction);

        ctx.getSource().sendSuccess(Component.literal("Successfully removed offer!"), false);
        return 0;
    }
}
