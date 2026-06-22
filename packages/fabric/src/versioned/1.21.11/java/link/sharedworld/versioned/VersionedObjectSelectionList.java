package link.sharedworld.versioned;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class VersionedObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
    public VersionedObjectSelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    public void updateBounds(int x, int y, int width, int height) {
        this.setPosition(x, y);
        this.setWidth(width);
        this.setHeight(height);
    }
}
