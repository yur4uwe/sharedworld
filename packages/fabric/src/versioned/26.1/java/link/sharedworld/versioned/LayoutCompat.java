package link.sharedworld.versioned;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public final class LayoutCompat {
    private LayoutCompat() {
    }

    public static LinearLayout horizontal(int spacing) {
        return LinearLayout.horizontal().spacing(spacing);
    }

    public static LinearLayout vertical(int spacing) {
        return LinearLayout.vertical().spacing(spacing);
    }

    public static void alignCenter(LinearLayout layout) {
        layout.defaultCellSetting().alignHorizontallyCenter();
    }

    public static void addTitleHeader(HeaderAndFooterLayout layout, Component title, Font font) {
        layout.addTitleHeader(title, font);
    }

    public static int getContentHeight(HeaderAndFooterLayout layout) {
        return layout.getContentHeight();
    }
}
