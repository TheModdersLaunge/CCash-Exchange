package whosalbercik.ccashexchange.object;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import whosalbercik.ccashexchange.api.CCashApi;
import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.utils.Utils;

import java.util.UUID;

// Used when the author that was supposed to get items from a transaction, was not online and now the transaction is waiting to be payed out
public class UnCompletedTransaction {

    private UUID shouldReceive;
    private ItemStack item;
    private long money;
    private Transaction transaction;

    private UnCompletionType type;
    public UnCompletedTransaction(Transaction transaction, ItemStack item) {
      item = item.copy();
      shouldReceive = transaction.getCreator();
      this.transaction = transaction;
      this.money = 0;
      this.type = UnCompletionType.ITEM;
    }

    public UnCompletedTransaction(Transaction transaction, long money) {
        this.shouldReceive = transaction.getCreator();
        this.item = ItemStack.EMPTY;
        this.transaction = transaction;
        this.money = money;
        this.type = UnCompletionType.MONEY;

    }

    public boolean tryCompleting(Player author) {
        if (author.getUUID().equals(shouldReceive) && type == UnCompletionType.ITEM) {

            if (author.getInventory().getFreeSlot() == -1 && author.getInventory().getSlotWithRemainingSpace(transaction.getItemstack()) == -1) {
                author.drop(new ItemStack(transaction.getItemstack().getItem(), transaction.getItemstack().getCount()), false);
                return false;
            }
            else {
                author.getInventory().add(new ItemStack(transaction.getItemstack().getItem(), transaction.getItemstack().getCount()));
            }

            author.sendSystemMessage(Component.literal(String.format("While you were offline your transaction nr %s, [BID] %sx %s for $%s has been fulfilled!",
                    transaction.getId(),
                    transaction.getItemstack().getCount(),
                    transaction.getItemstack().getItem().getName(transaction.getItemstack()).getString(),
                    transaction.getPrice()
                    )).withStyle(ChatFormatting.AQUA));

            author.sendSystemMessage(Component.literal("Items have been put into your inventory").withStyle(ChatFormatting.AQUA));

            return true;

        } else if (author.getUUID().equals(shouldReceive) && type == UnCompletionType.MONEY) {

            String account = author.getPersistentData().getString("ccash.account");

            author.sendSystemMessage(Component.literal(String.format("While you were offline your transaction nr %s, [ASK] %sx %s for $%s has been fulfilled!",
                    transaction.getId(),
                    transaction.getItemstack().getCount(),
                    transaction.getItemstack().getItem().getName(transaction.getItemstack()).getString(),
                    transaction.getPrice()
            )).withStyle(ChatFormatting.AQUA));

            if (account.equals("") || !CCashApi.containsAccount(account)) {
                author.sendSystemMessage(Component.literal("Account not set up or not found on server! Please use ").append("/config account").withStyle(ChatFormatting.AQUA).append(" to register").withStyle(ChatFormatting.RED));
                author.sendSystemMessage(Component.literal("Re login to try again"));
                return false;
            }

            ListTag tag = author.getPersistentData().getList("ccash.transactions", 10);
            tag.remove(Utils.getTransactionNBT(transaction));
            author.getPersistentData().put("ccash.transactions", tag);

            CCashApi.sendFunds(ServerConfig.MARKET_ACCOUNT.get(), ServerConfig.MARKET_PASS.get(), account, transaction.price * transaction.getItemstack().getCount());

            author.sendSystemMessage(Component.literal("$" + transaction.price + " has been transferred to your account").withStyle(ChatFormatting.GREEN));

            return true;
        }
        return false;
    }


    public UUID getShouldReceive() {
        return shouldReceive;
    }

    public ItemStack getItem() {
        return item;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public long getMoney() {
        return money;
    }

    public UnCompletionType getType() {
        return type;
    }

    public enum UnCompletionType {
        ITEM,
        MONEY
    }
}
