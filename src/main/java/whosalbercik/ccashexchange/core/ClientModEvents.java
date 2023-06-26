package whosalbercik.ccashexchange.core;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import whosalbercik.ccashexchange.CCashExchange;
import whosalbercik.ccashexchange.gui.ItemMarketScreen;
import whosalbercik.ccashexchange.gui.MarketScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = CCashExchange.MODID)
public class ClientModEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.MARKET_MENU.get(), MarketScreen::new);
            MenuScreens.register(ModMenus.ITEM_MARKET_MENU.get(), ItemMarketScreen::new);
        });


    }

}
