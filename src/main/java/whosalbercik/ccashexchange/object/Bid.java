package whosalbercik.ccashexchange.object;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class Bid {
    private final int id;
    private final UUID creator;
    private final ItemStack itemstack;
    private final long price;



    public Bid(UUID creator, ItemStack stack, long price) {
        this.creator = creator;
        this.price = price;
        this.id = this.hashCode();

        itemstack = stack.copy();
        itemstack.getOrCreateTag().put("ccash.id", IntTag.valueOf(id));
        itemstack.getOrCreateTag().put("ccash.creator", StringTag.valueOf(creator.toString()));
        itemstack.getOrCreateTag().put("ccash.price", LongTag.valueOf(price));
        itemstack.setHoverName(Component.literal("[BID] $" + String.valueOf(price)).withStyle(ChatFormatting.BLUE));
    }

    public Bid(UUID creator,ItemStack stack, long price, int id) {
        this.creator = creator;

        this.price = price;
        this.id = id;

        itemstack = stack.copy();
        itemstack.getOrCreateTag().put("ccash.id", IntTag.valueOf(id));
        itemstack.getOrCreateTag().put("ccash.creator", StringTag.valueOf(creator.toString()));
        itemstack.getOrCreateTag().put("ccash.price", LongTag.valueOf(price));
        itemstack.setHoverName(Component.literal("[BID] $" + String.valueOf(price)).withStyle(ChatFormatting.BLUE));
    }

    public UUID getCreator() {
        return creator;
    }

    public Player getCreator(Level level) {
        return level.getPlayerByUUID(creator);
    }

    public ItemStack getItemstack() {
        return itemstack;
    }

    public long getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }
}
