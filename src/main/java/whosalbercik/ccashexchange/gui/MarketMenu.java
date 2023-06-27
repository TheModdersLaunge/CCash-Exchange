package whosalbercik.ccashexchange.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import whosalbercik.ccashexchange.core.ModMenus;
import whosalbercik.ccashexchange.object.Transaction;

import java.util.ArrayList;
import java.util.HashMap;

public class MarketMenu extends ChestMenu {

    public MarketMenu(int i, Inventory inventory) {
        super(ModMenus.MARKET_MENU.get(), i, inventory, new SimpleContainer(54), 6);
    }


    public void putTransactions(ArrayList<Transaction> transactions) {
        SimpleContainer container = (SimpleContainer) this.getContainer();
        decor();

        HashMap<Transaction,Item> icons = new HashMap<>();


        for (Transaction transaction: transactions) {
            if (!icons.containsValue(transaction.getItemstack().getItem())) {
                icons.put(transaction, transaction.getItemstack().getItem());
            }
        }

        ArrayList<Item> iconArray = new ArrayList<>(icons.values());

        for (Transaction transaction: icons.keySet()) {
            ItemStack icona = new ItemStack(icons.get(transaction));

            icona.setHoverName(Component.literal("Click to view Bids and Asks").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.AQUA));
            icona.getOrCreateTag().put("ccash.gui", StringTag.valueOf("true"));

            container.setItem(10 + (iconArray.indexOf(icons.get(transaction)) == 7 ? 8 : iconArray.indexOf(icons.get(transaction))), icona);
        }

    }

    private void decor() {
        SimpleContainer container = (SimpleContainer) this.getContainer();
        // decor
        container.setItem(0, new ItemStack(Items.PINK_STAINED_GLASS_PANE));
        container.setItem(1, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(7, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(8, new ItemStack(Items.PINK_STAINED_GLASS_PANE));

        container.setItem(9, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(17, new ItemStack(Items.RED_STAINED_GLASS_PANE));

        container.setItem(36, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(44, new ItemStack(Items.RED_STAINED_GLASS_PANE));

        container.setItem(45, new ItemStack(Items.PINK_STAINED_GLASS_PANE));
        container.setItem(46, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(52, new ItemStack(Items.RED_STAINED_GLASS_PANE));
        container.setItem(53, new ItemStack(Items.PINK_STAINED_GLASS_PANE));
    }
}
