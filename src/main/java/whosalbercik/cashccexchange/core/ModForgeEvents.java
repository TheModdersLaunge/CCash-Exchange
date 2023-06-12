package whosalbercik.cashccexchange.core;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import whosalbercik.cashccexchange.CCashExchange;


@Mod.EventBusSubscriber(modid = CCashExchange.MODID)
public class ModForgeEvents {

    @SubscribeEvent
    public static void playerTick(PlayerEvent.PlayerLoggedInEvent event) {

    }
}
