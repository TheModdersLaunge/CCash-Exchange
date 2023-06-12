package whosalbercik.cashccexchange.object;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class Bid {
    private final UUID creator;
    private final ItemStack itemstack;
    private final long price;



    public Bid(UUID creator,ItemStack itemstack, long price) {
        this.creator = creator;
        this.itemstack = itemstack;
        this.price = price;
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
}
