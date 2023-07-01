package whosalbercik.ccashexchange.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.ccashexchange.object.BidTransaction;
import whosalbercik.ccashexchange.object.Transaction;

public class Utils {
    public static CompoundTag getTransactionNBT(Transaction transaction) {

        CompoundTag transactionTag = new CompoundTag();
        transactionTag.put("ccash.id", IntTag.valueOf(transaction.getId()));
        transactionTag.putUUID("ccash.creator", transaction.getCreator());
        transactionTag.put("ccash.item", StringTag.valueOf(ForgeRegistries.ITEMS.getResourceKey(transaction.getItemstack().getItem()).get().location().toString()));
        transactionTag.put("ccash.count", IntTag.valueOf(transaction.getItemstack().getCount()));
        transactionTag.put("ccash.price", LongTag.valueOf(transaction.getPrice()));
        transactionTag.put("ccash.type", StringTag.valueOf(transaction instanceof BidTransaction ? "bid" : "ask"));
        return transactionTag;
    }
}
