package whosalbercik.ccashexchange.object;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BidTransaction extends Transaction{

    public BidTransaction(UUID creator, ItemStack stack, long price) {
        super(creator, stack, price);

        itemstack.getOrCreateTag().put("ccash.id", IntTag.valueOf(id));
        itemstack.getOrCreateTag().put("ccash.creator", StringTag.valueOf(creator.toString()));
        itemstack.getOrCreateTag().put("ccash.price", LongTag.valueOf(price));
        itemstack.getOrCreateTag().put("ccash.type", StringTag.valueOf("bid"));

        itemstack.setHoverName(Component.literal("[BID] $" + price).withStyle(ChatFormatting.BLUE));
    }

    public BidTransaction(UUID creator, ItemStack stack, long price, int id) {
        super(id, creator, stack, price);

        itemstack.getOrCreateTag().put("ccash.id", IntTag.valueOf(id));
        itemstack.getOrCreateTag().put("ccash.creator", StringTag.valueOf(creator.toString()));
        itemstack.getOrCreateTag().put("ccash.price", LongTag.valueOf(price));
        itemstack.setHoverName(Component.literal("[BID] $" + price).withStyle(ChatFormatting.BLUE));
    }

    @Override
    public int compareTo(@NotNull Transaction o) {
        int i = Long.compare(this.getPrice(), o.getPrice());
        return i * -1;
    }
}
