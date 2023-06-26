package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.gui.MarketMenu;

public class OpenMarketCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("market").executes(OpenMarketCommand::market));
    }

    private static int market(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();

        assert p != null;


        MarketMenu menu = new MarketMenu(1, p.getInventory());
        menu.putBids(CCashSavedData.get(p.getServer().overworld()).getBids());

        MenuConstructor constructor = (par1, par2, par3) -> menu;
        p.openMenu(new SimpleMenuProvider(constructor, Component.literal("Market")));

        return 0;
    }
}
