package whosalbercik.ccashexchange;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.core.ModMenus;
import whosalbercik.ccashexchange.core.ModPacketHandler;

@Mod(CCashExchange.MODID)
public class CCashExchange {

    // Define mod id
    public static final String MODID = "ccashexchange";
    // reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CCashExchange() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModPacketHandler.register();
        ModMenus.MENUS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "ccash-common.toml");

    }
}
