package whosalbercik.ccashexchange.core;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import whosalbercik.ccashexchange.CCashExchange;
import whosalbercik.ccashexchange.gui.ItemMarketMenu;
import whosalbercik.ccashexchange.gui.MarketMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CCashExchange.MODID);

    public static final RegistryObject<MenuType<MarketMenu>> MARKET_MENU = MENUS.register("quest_menu", () -> new MenuType<MarketMenu>(MarketMenu::new));
    public static final RegistryObject<MenuType<ItemMarketMenu>> ITEM_MARKET_MENU = MENUS.register("item_market_menu", () -> new MenuType<ItemMarketMenu>(ItemMarketMenu::new));


}
