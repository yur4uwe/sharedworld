package link.sharedworld.versioned;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Version-specific GUI draw calls for the Minecraft 26.1 generation, where the GUI render
 * pass became a state-extraction pass ({@code GuiGraphicsExtractor}). Sprite and texture
 * identifiers cross this seam as plain "namespace:path" strings so shared screen code never
 * names version-specific classes.
 */
public final class GuiBlit {
    private static final Map<String, Identifier> IDENTIFIERS = new ConcurrentHashMap<>();

    private GuiBlit() {
    }

    public static void sprite(GuiGraphicsExtractor guiGraphics, String spriteId, int x, int y, int width, int height) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier(spriteId), x, y, width, height);
    }

    public static void favicon(GuiGraphicsExtractor guiGraphics, FaviconTexture texture, int x, int y, int size) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture.textureLocation(), x, y, 0.0F, 0.0F, size, size, size, size);
    }

    public static void footerSeparator(GuiGraphicsExtractor guiGraphics, int y, int width) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, y, 0.0F, 0.0F, width, 2, 32, 2);
    }

    private static Identifier identifier(String id) {
        return IDENTIFIERS.computeIfAbsent(id, Identifier::parse);
    }

    public static void setTooltip(GuiGraphicsExtractor guiGraphics, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component tooltip, int mouseX, int mouseY) {
        guiGraphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
    }

    public static void setTooltip(GuiGraphicsExtractor guiGraphics, net.minecraft.client.gui.Font font, java.util.List<net.minecraft.util.FormattedCharSequence> tooltipLines, int mouseX, int mouseY) {
        guiGraphics.setTooltipForNextFrame(tooltipLines, mouseX, mouseY);
    }
}
