package whosalbercik.ccashexchange.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

public class MyOffersCommand {
    public static void register(CommandDispatcher<CommandSourceStack> stack) {
        stack.register(Commands.literal("myoffers")
                .executes(MyOffersCommand::myoffers));
    }

    private static int myoffers(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();

        if (p == null) {
            ctx.getSource().sendFailure(Component.literal("Command must be sent by a player!"));
            return 0;
        }

        ListTag tags = p.getPersistentData().getList("ccash.transactions", 10);

        for (Tag tagg: tags) {
            CompoundTag tag = (CompoundTag) tagg;
            ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString("ccash.item"))));

            p.sendSystemMessage(Component.literal(String.format("(%s) [%s] %sx %s", tag.getInt("ccash.id"),
                    tag.getString("ccash.type").toUpperCase(Locale.ROOT),
                    tag.getInt("ccash.count"),
                    stack.getItem().getName(stack).getString()
                    )));
        }

        if (tags.isEmpty()) {
            ctx.getSource().sendSuccess(Component.literal("No Offers found!").withStyle(ChatFormatting.AQUA), false);
        }
        return 0;
    }
}
