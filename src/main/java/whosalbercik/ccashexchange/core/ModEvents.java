package whosalbercik.ccashexchange.core;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import whosalbercik.ccashexchange.CCashExchange;

@Mod.EventBusSubscriber(modid = CCashExchange.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void commonConfig(FMLConstructModEvent event) {

    }

}
