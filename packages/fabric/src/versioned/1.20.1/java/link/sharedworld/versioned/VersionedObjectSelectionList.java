package link.sharedworld.versioned;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class VersionedObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> implements net.minecraft.client.gui.layouts.LayoutElement {
    public VersionedObjectSelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, y + height, itemHeight);
    }

    public void updateBounds(int x, int y, int width, int height) {
        this.updateSize(width, height, y, y + height);
        this.setLeftPos(x);
    }

    @Override
    public int getY() {
        return this.y0;
    }

    @Override
    public int getX() {
        return this.x0;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setX(int x) {
        this.setLeftPos(x);
    }

    @Override
    public void setY(int y) {
        int diff = y - this.y0;
        this.y0 = y;
        this.y1 += diff;
    }

    @Override
    public void visitWidgets(java.util.function.Consumer<net.minecraft.client.gui.components.AbstractWidget> consumer) {
        consumer.accept(new WidgetSelectionListWrapper(this));
    }

    public void updateSize(int width, net.minecraft.client.gui.layouts.HeaderAndFooterLayout layout) {
        int yTop = layout.getHeaderHeight();
        int yBottom = layout.getHeight() - layout.getFooterHeight();
        this.updateSize(width, yBottom - yTop, yTop, yBottom);
    }
}
