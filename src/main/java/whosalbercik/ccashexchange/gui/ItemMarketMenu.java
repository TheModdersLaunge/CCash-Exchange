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
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.ccashexchange.core.ModMenus;
import whosalbercik.ccashexchange.object.BidTransaction;
import whosalbercik.ccashexchange.object.Transaction;

import java.util.ArrayList;
        import java.util.List;

public class ItemMarketMenu extends ChestMenu {

    private Item item;

    public ItemMarketMenu(int i, Inventory inventory) {
        super(ModMenus.ITEM_MARKET_MENU.get(), i, inventory,new SimpleContainer(54),  6);
    }

    public Item getItem() {
        return item;
    }

    public ItemMarketMenu(int i, Inventory inventory, Item item) {
        super(ModMenus.ITEM_MARKET_MENU.get(), i, inventory,new SimpleContainer(54),  6);
        this.item = item;
    }


    public boolean addTransactions(ArrayList<Transaction> transactions, int page) {
        decor();

        // page buttons
        SimpleContainer container = (SimpleContainer) this.getContainer();
        ItemStack pageDown = new ItemStack(Items.RED_WOOL);
        pageDown.getOrCreateTag().put("ccash.pagedown", StringTag.valueOf("true"));
        pageDown.getOrCreateTag().put("ccash.page", IntTag.valueOf(page));
        pageDown.getOrCreateTag().put("ccash.gui", StringTag.valueOf("true"));
        pageDown.setHoverName(Component.literal("PREVIOUS PAGE").withStyle(ChatFormatting.RED));
        pageDown.getOrCreateTag().put("ccash.item", StringTag.valueOf(ForgeRegistries.ITEMS.getResourceKey(item).get().location().toString()));


        ItemStack pageUp = new ItemStack(Items.GREEN_WOOL);
        pageUp.getOrCreateTag().put("ccash.pageup", StringTag.valueOf("true"));
        pageUp.getOrCreateTag().put("ccash.page", IntTag.valueOf(page));
        pageUp.getOrCreateTag().put("ccash.gui", StringTag.valueOf("true"));
        pageUp.setHoverName(Component.literal("NEXT PAGE").withStyle(ChatFormatting.GREEN));
        pageUp.getOrCreateTag().put("ccash.item", StringTag.valueOf(ForgeRegistries.ITEMS.getResourceKey(item).get().location().toString()));

        ItemStack pageIndicator = new ItemStack(Items.BLUE_STAINED_GLASS);
        pageIndicator.setHoverName(Component.literal("PAGE NR. " + String.valueOf(page)).withStyle(ChatFormatting.DARK_AQUA));

        container.setItem(4, pageIndicator);
        container.setItem(1, pageDown);
        container.setItem(7, pageUp);


        // transactions of the correct item
        ArrayList<Transaction>  correctTransactions = new ArrayList<Transaction>();

        transactions.forEach((transaction) -> {if (transaction.getItemstack().getItem().equals(item))  correctTransactions.add(transaction);});

        // no transactions in this page
        if (correctTransactions.size() <= 36 * (page - 1)) {
            return false;
        }

        // transactions that should be on the specified page
        List<Transaction> pagedTransactions = correctTransactions.subList(36 * (page - 1), Math.min(correctTransactions.size(), 36 * page));


        for (Transaction transaction: pagedTransactions) {

            ItemStack icona = new ItemStack(transaction.getItemstack().getItem());
            icona.setHoverName(Component.literal((String.format("[%s] %sx for $%s (total $%s)",
                    transaction instanceof BidTransaction ? "BID" : "ASK",
                    transaction.getItemstack().getCount(),
                    transaction.getPrice(),
                    transaction.getPrice() * transaction.getItemstack().getCount())))
                    .withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.GREEN));

            icona.getOrCreateTag().put("ccash.gui", StringTag.valueOf("true"));
            icona.getTag().put("ccash.id", IntTag.valueOf(transaction.getId()));
            icona.getTag().put("ccash.type", StringTag.valueOf(transaction instanceof BidTransaction ? "bid" : "ask"));



            container.setItem(9 + pagedTransactions.indexOf(transaction), icona.copy());
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
