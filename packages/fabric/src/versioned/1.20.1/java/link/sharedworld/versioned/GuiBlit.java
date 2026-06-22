package link.sharedworld.versioned;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public final class GuiBlit {
    private static final ResourceLocation SERVER_SELECTION = new ResourceLocation("minecraft", "textures/gui/server_selection.png");
    private static final ResourceLocation ICONS = new ResourceLocation("minecraft", "textures/gui/icons.png");
    private static final java.util.Map<String, ResourceLocation> CACHED_SPRITES = new java.util.concurrent.ConcurrentHashMap<>();

    private GuiBlit() {
    }

    public static void sprite(GuiGraphics guiGraphics, String spriteId, int x, int y, int width, int height) {
        if (spriteId.startsWith("minecraft:server_list/")) {
            switch (spriteId) {
                case "minecraft:server_list/join": guiGraphics.blit(SERVER_SELECTION, x, y, 0, 0, width, height, 256, 256); break;
                case "minecraft:server_list/join_highlighted": guiGraphics.blit(SERVER_SELECTION, x, y, 0, 32, width, height, 256, 256); break;
                case "minecraft:server_list/move_up": guiGraphics.blit(SERVER_SELECTION, x, y, 96, 0, width, height, 256, 256); break;
                case "minecraft:server_list/move_up_highlighted": guiGraphics.blit(SERVER_SELECTION, x, y, 96, 32, width, height, 256, 256); break;
                case "minecraft:server_list/move_down": guiGraphics.blit(SERVER_SELECTION, x, y, 64, 0, width, height, 256, 256); break;
                case "minecraft:server_list/move_down_highlighted": guiGraphics.blit(SERVER_SELECTION, x, y, 64, 32, width, height, 256, 256); break;
                case "minecraft:server_list/unreachable": guiGraphics.blit(ICONS, x, y, 80, 16, 10, 8, 256, 256); break;
                case "minecraft:server_list/ping_5": guiGraphics.blit(ICONS, x, y, 0, 16, 10, 8, 256, 256); break;
                case "minecraft:server_list/pinging_1": guiGraphics.blit(ICONS, x, y, 8, 16, 10, 8, 256, 256); break;
                case "minecraft:server_list/pinging_2": guiGraphics.blit(ICONS, x, y, 16, 16, 10, 8, 256, 256); break;
                case "minecraft:server_list/pinging_3": guiGraphics.blit(ICONS, x, y, 24, 16, 10, 8, 256, 256); break;
                case "minecraft:server_list/pinging_4": guiGraphics.blit(ICONS, x, y, 32, 16, 10, 8, 256, 256); break;
                case "minecraft:server_list/pinging_5": guiGraphics.blit(ICONS, x, y, 40, 16, 10, 8, 256, 256); break;
                default: break;
            }
        } else if (spriteId.startsWith("sharedworld:")) {
            ResourceLocation loc = CACHED_SPRITES.computeIfAbsent(spriteId, id -> {
                String path = id.substring("sharedworld:".length());
                return new ResourceLocation("sharedworld", "textures/gui/" + path + ".png");
            });
            guiGraphics.blit(loc, x, y, 0, 0, width, height, width, height);
        } else if (spriteId.equals("minecraft:widget/scroller_background") || spriteId.equals("minecraft:widget/scroller")) {
            guiGraphics.fill(x, y, x + width, y + height, spriteId.contains("background") ? 0xFF000000 : 0xFF808080);
        }
    }

    public static void favicon(GuiGraphics guiGraphics, FaviconTexture texture, int x, int y, int size) {
        guiGraphics.blit(texture.textureLocation(), x, y, 0.0F, 0.0F, size, size, size, size);
    }

    public static void footerSeparator(GuiGraphics guiGraphics, int y, int width) {
        guiGraphics.fill(0, y, width, y + 2, 0xFF484848);
    }

    public static void setTooltip(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component tooltip, int mouseX, int mouseY) {
        guiGraphics.renderTooltip(font, tooltip, mouseX, mouseY);
    }

    public static void setTooltip(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, java.util.List<net.minecraft.util.FormattedCharSequence> tooltipLines, int mouseX, int mouseY) {
        guiGraphics.renderTooltip(font, tooltipLines, mouseX, mouseY);
    }
}
