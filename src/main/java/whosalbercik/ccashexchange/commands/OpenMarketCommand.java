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
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.gui.MarketMenu;
import whosalbercik.ccashexchange.object.Transaction;

import java.util.ArrayList;

public class OpenMarketCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("market")
                        .then(Commands.argument("password", StringArgumentType.word())
                                .executes(OpenMarketCommand::market)));
    }

    private static int market(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();

        p.getPersistentData().put("ccash.holdPass", StringTag.valueOf(ctx.getArgument("password", String.class)));

        assert p != null;


        MarketMenu menu = new MarketMenu(1, p.getInventory());
        ArrayList<Transaction> transactions = CCashSavedData.get(p.getServer().overworld()).getTransactions();

        if (transactions.isEmpty()) {
            p.sendSystemMessage(Component.literal("No offers available!").withStyle(ChatFormatting.AQUA));
            return 0;
        }

        menu.putTransactions(transactions);

        MenuConstructor constructor = (par1, par2, par3) -> menu;
        p.openMenu(new SimpleMenuProvider(constructor, Component.literal("Market")));

        return 0;
    }
}
