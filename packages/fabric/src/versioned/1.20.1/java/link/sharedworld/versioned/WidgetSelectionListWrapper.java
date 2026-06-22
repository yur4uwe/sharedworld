package link.sharedworld.versioned;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class WidgetSelectionListWrapper extends AbstractWidget {
    private final VersionedObjectSelectionList<?> list;

    public WidgetSelectionListWrapper(VersionedObjectSelectionList<?> list) {
        super(list.getX(), list.getY(), list.getWidth(), list.getHeight(), Component.empty());
        this.list = list;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.list.updateBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        this.list.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.list.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.list.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.list.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.list.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.list.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.list.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.list.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        this.list.setFocused(focused);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.list.updateNarration(output);
    }
}
