package whosalbercik.ccashexchange;

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
import whosalbercik.ccashexchange.object.Ask;
import whosalbercik.ccashexchange.object.Bid;
import whosalbercik.ccashexchange.object.Transaction;
import whosalbercik.ccashexchange.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class CCashSavedData extends SavedData {

    private final HashMap<Integer, Transaction> transactions = new HashMap<Integer, Transaction>();


    public static CCashSavedData get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(CCashSavedData::new, CCashSavedData::new, "ccash");
    }

    public void saveTransaction(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        this.setDirty();
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction.getId());
        this.setDirty();
    }

    public ArrayList<Transaction> getTransactions() {
        ArrayList<Transaction> transactionsa = new ArrayList<>();
        transactions.forEach((id, transaction) -> transactionsa.add(transaction));

        return transactionsa;
    }

    public Transaction getTransaction(int id) {
        return transactions.get(id);
    }

    public CCashSavedData() {
    }


    // adds to list
    public CCashSavedData(CompoundTag tag) {
        ListTag list = tag.getList("ccash.transactions", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag transactionTag = (CompoundTag) t;

            Transaction action;

            if (transactionTag.getString("ccash.type").equals("bid")) {
                action = new Bid(transactionTag.getUUID("ccash.creator"),
                        new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(transactionTag.getString("ccash.item"))), transactionTag.getInt("ccash.count")),
                        transactionTag.getLong("ccash.price"),
                        transactionTag.getInt("ccash.id"));
            }
            else {
                action = new Ask(transactionTag.getUUID("ccash.creator"),
                        new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(transactionTag.getString("ccash.item"))), transactionTag.getInt("ccash.count")),
                        transactionTag.getLong("ccash.price"),
                        transactionTag.getInt("ccash.id"));
            }

            transactions.put(action.getId(), action);
        }
    }


    // reads from list
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        transactions.forEach((id, transaction) -> {
            list.add(Utils.getTransactionNBT(transaction));
        });
        tag.put("ccash.transactions", list);
        return tag;
    }

}
