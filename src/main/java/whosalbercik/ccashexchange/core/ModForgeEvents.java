package whosalbercik.ccashexchange.core;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import whosalbercik.ccashexchange.CCashExchange;
import whosalbercik.ccashexchange.api.CCashApi;
import whosalbercik.ccashexchange.commands.*;
import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.object.UnCompletedTransaction;
import whosalbercik.ccashexchange.utils.UnCompletedQueue;

import java.util.ArrayList;


@Mod.EventBusSubscriber(modid = CCashExchange.MODID)
public class ModForgeEvents {

    @SubscribeEvent
    public static void playerJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player && !event.getLevel().isClientSide) {
            ArrayList<UnCompletedTransaction> toRemove = new ArrayList<>();

            for (UnCompletedTransaction transaction : UnCompletedQueue.get(event.getEntity().getServer().overworld()).getTransactions()) {
                if (transaction.tryCompleting((Player) event.getEntity())) {
                    toRemove.add(transaction);
                }
            }
            event.getEntity().sendSystemMessage(Component.literal("The taxation on this server is " + ServerConfig.TAXATION.get() * 100 + "%").withStyle(ChatFormatting.AQUA));
            toRemove.forEach((removed) -> UnCompletedQueue.get(event.getEntity().getServer().overworld()).removeTransaction(removed));

            if (!CCashApi.isOnline()) {
                event.getEntity().sendSystemMessage(Component.literal("Cannot connect to server! Mod will not work properly").withStyle(ChatFormatting.RED));
                return;
            }
        }
    }

    @SubscribeEvent
    public static void serverStart(ServerStartedEvent event) {
        if (!CCashApi.isOnline()) {
            CCashExchange.LOGGER.error("CANNOT CONNECT TO SERVER!");
            CCashExchange.LOGGER.error("UNWANTED ERRORS WILL HAPPEN");

        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        PlaceBidCommand.register(event.getDispatcher(), event.getBuildContext());
        OpenMarketCommand.register(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
        BalanceCommand.register(event.getDispatcher());
        TransferCommand.register(event.getDispatcher());
        PlaceAskCommand.register(event.getDispatcher(), event.getBuildContext());
        MyOffersCommand.register(event.getDispatcher());
        RemoveTransactionCommand.register(event.getDispatcher());
    }
}
