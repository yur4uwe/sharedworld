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

    private void syncBounds() {
        super.setX(this.list.getX());
        super.setY(this.list.getY());
        this.width = this.list.getWidth();
        this.height = this.list.getHeight();
    }

    @Override
    public int getX() {
        this.syncBounds();
        return super.getX();
    }

    @Override
    public int getY() {
        this.syncBounds();
        return super.getY();
    }

    @Override
    public int getWidth() {
        this.syncBounds();
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        this.syncBounds();
        return super.getHeight();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.list.updateBounds(x, this.list.getY(), this.list.getWidth(), this.list.getHeight());
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.list.updateBounds(this.list.getX(), y, this.list.getWidth(), this.list.getHeight());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.syncBounds();
        this.list.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.syncBounds();
        return this.list.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.syncBounds();
        return this.list.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.syncBounds();
        return this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.syncBounds();
        return this.list.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.syncBounds();
        return this.list.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.syncBounds();
        return this.list.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        this.syncBounds();
        return this.list.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        this.syncBounds();
        return this.list.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean focused) {
        this.syncBounds();
        super.setFocused(focused);
        this.list.setFocused(focused);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.syncBounds();
        this.list.updateNarration(output);
    }
}
