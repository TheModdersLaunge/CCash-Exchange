package whosalbercik.cashccexchange;

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
import whosalbercik.cashccexchange.object.Bid;

import java.util.ArrayList;

public class CCashSavedData extends SavedData {

    private final ArrayList<Bid> bids = new ArrayList<>();


    public static CCashSavedData get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(CCashSavedData::new, CCashSavedData::new, "ccash");
    }

    public void saveBid(Bid bid) {
        bids.add(bid);
        this.setDirty();
    }

    public void removeBid(Bid bid) {
        bids.remove(bid);
        this.setDirty();
    }

    public CCashSavedData() {
    }


    // adds to list
    public CCashSavedData(CompoundTag tag) {
        ListTag list = tag.getList("ccash.bids", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag bidTag = (CompoundTag) t;

            Bid bid = new Bid(bidTag.getUUID("ccash.creator"),
                    new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(bidTag.getString("ccash.item"))), bidTag.getInt("ccash.count")),
                    bidTag.getLong("ccash.price"));

            bids.add(bid);
        }
    }


    // reads from list
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        bids.forEach((bid) -> {
            CompoundTag bidTag = new CompoundTag();
            bidTag.putUUID("ccash.creator", bid.getCreator());
            bidTag.putString("ccash.item", ForgeRegistries.ITEMS.getResourceKey(bid.getItemstack().getItem()).get().location().getNamespace());
            bidTag.putLong("ccash.price", bid.getPrice());
            bidTag.putInt("ccash.count", bid.getItemstack().getCount());

            list.add(bidTag);
        });
        tag.put("ccash.bids", list);
        return tag;
    }

}
