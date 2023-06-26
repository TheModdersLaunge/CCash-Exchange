package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.gui.MarketMenu;
import whosalbercik.ccashexchange.object.Bid;

import java.util.ArrayList;

public class OpenMarketCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("market")
                        .executes(OpenMarketCommand::market));
    }

    private static int market(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();

        assert p != null;


        MarketMenu menu = new MarketMenu(1, p.getInventory());
        ArrayList<Bid> bids = CCashSavedData.get(p.getServer().overworld()).getBids();

        if (bids.isEmpty()) {
            p.sendSystemMessage(Component.literal("No offers available!").withStyle(ChatFormatting.AQUA));
            return 0;
        }

        menu.putBids(bids);

        MenuConstructor constructor = (par1, par2, par3) -> menu;
        p.openMenu(new SimpleMenuProvider(constructor, Component.literal("Market")));

        return 0;
    }
}
