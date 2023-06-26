package whosalbercik.ccashexchange.core;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import whosalbercik.ccashexchange.CCashExchange;
import whosalbercik.ccashexchange.commands.*;


@Mod.EventBusSubscriber(modid = CCashExchange.MODID)
public class ModForgeEvents {

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        return;
    }


    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        PlaceBidCommand.register(event.getDispatcher(), event.getBuildContext());
        OpenMarketCommand.register(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
        BalanceCommand.register(event.getDispatcher());
        TransferCommand.register(event.getDispatcher());
    }

}
