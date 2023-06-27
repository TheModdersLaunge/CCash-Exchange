package whosalbercik.ccashexchange.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import whosalbercik.ccashexchange.core.ModMenus;
import whosalbercik.ccashexchange.object.Bid;
import whosalbercik.ccashexchange.object.Transaction;

import java.util.ArrayList;

public class ItemMarketMenu extends ChestMenu {

    public ItemMarketMenu(int i, Inventory inventory) {
        super(ModMenus.ITEM_MARKET_MENU.get(), i, inventory,new SimpleContainer(54),  6);
    }


    public void addTransactions(ArrayList<Transaction> transactions, Item item) {
        decor();

        for (Transaction transaction: transactions) {
            if (transaction.getItemstack().getItem().equals(item)) {
                SimpleContainer container = (SimpleContainer) this.getContainer();

                ItemStack icona = transaction.getItemstack().copy();
                icona.setHoverName(Component.literal((transaction instanceof Bid ? "[BID] $" : "[ASK] $") + transaction.getPrice()).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GREEN));
                icona.getOrCreateTag().put("ccash.gui", StringTag.valueOf("true"));
                icona.getTag().put("ccash.id", IntTag.valueOf(transaction.getId()));
                icona.getTag().put("ccash.type", StringTag.valueOf(transaction instanceof Bid ? "bid" : "ask"));

                container.setItem(10 + (transactions.indexOf(transaction) == 7 ? 8 : transactions.indexOf(transaction)), icona);
            }
        }
    }




    private void decor() {
        SimpleContainer container = (SimpleContainer) this.getContainer();
        // decor
        container.setItem(0, new ItemStack(Items.PINK_STAINED_GLASS_PANE));
        container.setItem(8, new ItemStack(Items.PINK_STAINED_GLASS_PANE));



        container.setItem(45, new ItemStack(Items.PINK_STAINED_GLASS_PANE));
        container.setItem(53, new ItemStack(Items.PINK_STAINED_GLASS_PANE));
    }
}
