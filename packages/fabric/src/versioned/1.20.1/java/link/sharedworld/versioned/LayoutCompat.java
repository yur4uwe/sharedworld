package link.sharedworld.versioned;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

public final class LayoutCompat {
    private LayoutCompat() {
    }

    public static LinearLayout horizontal(int spacing) {
        return new SpacedLinearLayout(LinearLayout.Orientation.HORIZONTAL, spacing);
    }

    public static LinearLayout vertical(int spacing) {
        return new SpacedLinearLayout(LinearLayout.Orientation.VERTICAL, spacing);
    }

    public static void alignCenter(LinearLayout layout) {
        layout.defaultChildLayoutSetting().alignHorizontallyCenter();
    }

    public static void addTitleHeader(HeaderAndFooterLayout layout, Component title, Font font) {
        layout.addToHeader(new net.minecraft.client.gui.components.StringWidget(title, font));
    }

    public static int getContentHeight(HeaderAndFooterLayout layout) {
        return layout.getHeight() - layout.getHeaderHeight() - layout.getFooterHeight();
    }

    private static class SpacedLinearLayout extends LinearLayout {
        private final int spacing;
        private final LinearLayout.Orientation layoutOrientation;
        private boolean first = true;

        public SpacedLinearLayout(LinearLayout.Orientation orientation, int spacing) {
            super(0, 0, orientation);
            this.spacing = spacing;
            this.layoutOrientation = orientation;
        }

        @Override
        public <T extends net.minecraft.client.gui.layouts.LayoutElement> T addChild(T child, LayoutSettings settings) {
            LayoutSettings spacingSettings = settings.copy();
            if (first) {
                first = false;
            } else {
                if (this.layoutOrientation == LinearLayout.Orientation.HORIZONTAL) {
                    spacingSettings.paddingLeft(spacing);
                } else {
                    spacingSettings.paddingTop(spacing);
                }
            }
            return super.addChild(child, spacingSettings);
        }

        @Override
        public <T extends net.minecraft.client.gui.layouts.LayoutElement> T addChild(T child) {
            LayoutSettings spacingSettings = this.newChildLayoutSettings();
            if (first) {
                first = false;
            } else {
                if (this.layoutOrientation == LinearLayout.Orientation.HORIZONTAL) {
                    spacingSettings.paddingLeft(spacing);
                } else {
                    spacingSettings.paddingTop(spacing);
                }
            }
            return super.addChild(child, spacingSettings);
        }
    }
}
