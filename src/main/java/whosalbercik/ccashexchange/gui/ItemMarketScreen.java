package whosalbercik.ccashexchange.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.AirItem;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.ccashexchange.core.ModPacketHandler;
import whosalbercik.ccashexchange.networking.AskAcceptCs2Packet;
import whosalbercik.ccashexchange.networking.BidAcceptC2SPacket;
import whosalbercik.ccashexchange.networking.OpenItemMarketC2SPacket;

public class ItemMarketScreen extends AbstractContainerScreen<ItemMarketMenu> implements MenuAccess<ItemMarketMenu> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
    private final int containerRows;

    public ItemMarketScreen(ItemMarketMenu p_98409_, Inventory p_98410_, Component p_98411_) {
        super(p_98409_, p_98410_, p_98411_);
        this.containerRows = p_98409_.getRowCount();
        this.passEvents = false;
        int i = 222;
        int j = 114;
        this.imageHeight = 114 + this.containerRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(PoseStack p_98418_, int p_98419_, int p_98420_, float p_98421_) {
        this.renderBackground(p_98418_);
        super.render(p_98418_, p_98419_, p_98420_, p_98421_);
        this.renderTooltip(p_98418_, p_98419_, p_98420_);
    }
    @Override
    protected void renderBg(PoseStack p_98413_, float p_98414_, int p_98415_, int p_98416_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(p_98413_, i, j, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
        this.blit(p_98413_, i, j + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }


    @Override
    protected void slotClicked(Slot slot, int index, int partialTick, ClickType clickType) {
        if (slot == null || slot.getItem().getItem() instanceof AirItem || !slot.getItem().getOrCreateTag().contains("ccash.gui")) return;

        // change page
        if (slot.getItem().getOrCreateTag().contains("ccash.pageup")) {
            slot.getItem().getOrCreateTag().put("ccash.page", IntTag.valueOf(slot.getItem().getOrCreateTag().getInt("ccash.page") + 1));

            ModPacketHandler.sendToServer(new OpenItemMarketC2SPacket(ForgeRegistries.ITEMS.getValue(new ResourceLocation(slot.getItem().getOrCreateTag().getString("ccash.item"))), slot.getItem().getOrCreateTag().getInt("ccash.page")));
            return;

        } else if (slot.getItem().getOrCreateTag().contains("ccash.pagedown")) {
            slot.getItem().getOrCreateTag().put("ccash.page", IntTag.valueOf(slot.getItem().getOrCreateTag().getInt("ccash.page") == 1 ? 1 : slot.getItem().getOrCreateTag().getInt("ccash.page") - 1));

            ModPacketHandler.sendToServer(new OpenItemMarketC2SPacket(ForgeRegistries.ITEMS.getValue(new ResourceLocation(slot.getItem().getOrCreateTag().getString("ccash.item"))), slot.getItem().getOrCreateTag().getInt("ccash.page")));
            return;
        }

        if (slot.getItem().getTag().getString("ccash.type").equals("bid")) {
            ModPacketHandler.sendToServer(new BidAcceptC2SPacket(((IntTag) slot.getItem().getTag().get("ccash.id")).getAsInt()));
        } else {
            ModPacketHandler.sendToServer(new AskAcceptCs2Packet(((IntTag) slot.getItem().getTag().get("ccash.id")).getAsInt()));

        }
    }
}
