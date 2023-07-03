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
import java.util.List;

public class MarketMenu extends ChestMenu {

    public MarketMenu(int i, Inventory inventory) {
        super(ModMenus.MARKET_MENU.get(), i, inventory, new SimpleContainer(54), 6);
    }


    public boolean putTransactions(ArrayList<Transaction> allTransactions, int page) {
        SimpleContainer container = (SimpleContainer) this.getContainer();
        decor();

        HashMap<Transaction,Item> icons = new HashMap<>();



        for (Transaction transaction: allTransactions) {
            if (!icons.containsValue(transaction.getItemstack().getItem())) {
                icons.put(transaction, transaction.getItemstack().getItem());
            }
        }

        ArrayList<Item> iconArray = new ArrayList<>(icons.values());

        iconArray.add(0, Items.AIR);
        iconArray.add(Math.min(8, iconArray.size()), Items.AIR);
        iconArray.add((Math.min(9, iconArray.size())), Items.AIR);
        iconArray.add((Math.min(17, iconArray.size())), Items.AIR);
        iconArray.add((Math.min(18, iconArray.size())), Items.AIR);
        iconArray.add((Math.min(26, iconArray.size())), Items.AIR);
        iconArray.add((Math.min(27, iconArray.size())), Items.AIR);
        iconArray.add((Math.min(35, iconArray.size())), Items.AIR);
        iconArray.add((Math.min(36, iconArray.size())), Items.AIR);
        iconArray.add((Math.min(53, iconArray.size())), Items.AIR);


        // second page: 36 * 1 = 36, starts from 36. transaction
        if (iconArray.size() < 36 * (page - 1)) {
            return false;
        }

        List<Item> pagedIconArray = iconArray.subList(36 * (page - 1), Math.min(iconArray.size(), 36 * page));


        for (Item icon: pagedIconArray) {
            ItemStack icona = new ItemStack(icon);


            icona.setHoverName(Component.literal("Click to view Bids and Asks").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.AQUA));
            icona.getOrCreateTag().put("ccash.gui", StringTag.valueOf("true"));


            container.setItem(9 + pagedIconArray.indexOf(icon), icona);
        }

        return true;
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
