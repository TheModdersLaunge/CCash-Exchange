package whosalbercik.ccashexchange.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.ccashexchange.object.AskTransaction;
import whosalbercik.ccashexchange.object.BidTransaction;
import whosalbercik.ccashexchange.object.Transaction;
import whosalbercik.ccashexchange.object.UnCompletedTransaction;

import java.util.ArrayList;

public class UnCompletedQueue extends SavedData {
    private final ArrayList<UnCompletedTransaction> transactions = new ArrayList<UnCompletedTransaction>();


    public static UnCompletedQueue get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(UnCompletedQueue::new, UnCompletedQueue::new, "ccash.unCompleted");
    }

    public void saveTransaction(UnCompletedTransaction transaction) {
        transactions.add(transaction);
        this.setDirty();
    }

    public void removeTransaction(UnCompletedTransaction transaction) {
        transactions.remove(transaction);
        this.setDirty();
    }

    public ArrayList<UnCompletedTransaction> getTransactions() {
        return transactions;
    }


    public UnCompletedQueue() {
    }


    // adds to list
    public UnCompletedQueue(CompoundTag tag) {
        ListTag list = tag.getList("ccash.uncompleted", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag transactionTag = (CompoundTag) t;

            Transaction action;

            if (transactionTag.getString("ccash.type").equals("bid")) {
                action = new BidTransaction(transactionTag.getUUID("ccash.creator"),
                        new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(transactionTag.getString("ccash.item"))), transactionTag.getInt("ccash.count")),
                        transactionTag.getLong("ccash.price"),
                        transactionTag.getInt("ccash.id"));
            }
            else {
                action = new AskTransaction(transactionTag.getUUID("ccash.creator"),
                        new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(transactionTag.getString("ccash.item"))), transactionTag.getInt("ccash.count")),
                        transactionTag.getLong("ccash.price"),
                        transactionTag.getInt("ccash.id"));
            }

            UnCompletedTransaction unCompletedTransaction;

           if (transactionTag.getString("ccash.unCompletionType") == "item") {
               unCompletedTransaction = new UnCompletedTransaction(action, ForgeRegistries.ITEMS.getValue(new ResourceLocation(transactionTag.getString("ccash.item"))).getDefaultInstance());
           } else {
               unCompletedTransaction = new UnCompletedTransaction(action, transactionTag.getLong("ccash.price"));
           }

            transactions.add(unCompletedTransaction);
        }
    }


    // reads from list
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        transactions.forEach((unCompleted) -> {
            CompoundTag unCompletedTag = Utils.getTransactionNBT(unCompleted.getTransaction());
            unCompletedTag.putString("ccash.unCompletionType", unCompleted.getType() == UnCompletedTransaction.UnCompletionType.ITEM ? "item" : "money");

            list.add(unCompletedTag);
        });
        tag.put("ccash.uncompleted", list);
        return tag;
    }


}
