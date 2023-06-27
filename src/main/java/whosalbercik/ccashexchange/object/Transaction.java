package whosalbercik.ccashexchange.object;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class Transaction {
    protected final int id;
    protected final UUID creator;
    protected final ItemStack itemstack;
    protected final long price;

    public Transaction(int id, UUID creator, ItemStack itemstack, long price) {
        this.id = id;
        this.creator = creator;
        this.itemstack = itemstack.copy();
        this.price = price;
    }

    public Transaction(UUID creator, ItemStack itemstack, long price) {
        this.id = this.hashCode();
        this.creator = creator;
        this.itemstack = itemstack;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public UUID getCreator() {
        return creator;
    }

    public ItemStack getItemstack() {
        return itemstack;
    }

    public Player getCreator(Level level) {
        return level.getPlayerByUUID(creator);
    }


    public long getPrice() {
        return price;
    }
}
