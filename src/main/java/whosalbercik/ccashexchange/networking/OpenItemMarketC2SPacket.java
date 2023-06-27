package whosalbercik.ccashexchange.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import whosalbercik.ccashexchange.CCashSavedData;
import whosalbercik.ccashexchange.gui.ItemMarketMenu;

import java.util.function.Supplier;

public class OpenItemMarketC2SPacket {

    private Item item;

    public OpenItemMarketC2SPacket(Item item) {
        this.item = item;
    }

    public static void encode(OpenItemMarketC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.item.getDefaultInstance());
    }

    public static OpenItemMarketC2SPacket decode(FriendlyByteBuf buf) {
        Item item = buf.readItem().getItem();

        return new OpenItemMarketC2SPacket(item);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer p = ctx.getSender();

            ItemMarketMenu menu = new ItemMarketMenu(2, p.getInventory());

            menu.addTransactions(CCashSavedData.get(p.getServer().overworld()).getTransactions(), item);

            MenuConstructor constructor = (par1, par2, par3) -> menu;

            p.openMenu(new SimpleMenuProvider(constructor, Component.literal("Bids/Asks for " + item.asItem().getName(item.getDefaultInstance()).getString())));
        });
        return true;
    }


}
