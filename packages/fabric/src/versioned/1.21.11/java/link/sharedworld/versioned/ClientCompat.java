package link.sharedworld.versioned;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;

/** Version-specific client entry points whose location moved across Minecraft versions. */
public final class ClientCompat {
    private ClientCompat() {
    }

    public static MinecraftSessionService sessionService(Minecraft minecraft) {
        return minecraft.services().sessionService();
    }

    public static void disconnectFromWorld(Minecraft minecraft) {
        minecraft.disconnectFromWorld(null);
    }

    public static void drawDeferredSubtitles(Minecraft minecraft) {
        minecraft.gui.renderDeferredSubtitles();
    }

    public static java.util.UUID profileId(com.mojang.authlib.GameProfile profile) {
        return profile.id();
    }

    public static String profileName(com.mojang.authlib.GameProfile profile) {
        return profile.name();
    }

    public static void openWorld(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows flows, String levelId, Runnable callback) {
        flows.openWorld(levelId, callback);
    }

    public static void setTabActiveState(net.minecraft.client.gui.components.tabs.TabNavigationBar bar, int index, boolean active) {
        bar.setTabActiveState(index, active);
    }

    public static void clearScreenFocus(net.minecraft.client.gui.screens.Screen screen) {
        screen.clearFocus();
    }

    public static java.nio.file.Path getLevelPath(net.minecraft.world.level.storage.LevelStorageSource source, String levelId) {
        return source.getLevelPath(levelId);
    }

    public static net.minecraft.client.multiplayer.ServerData newServerData(String name, String ip, boolean lan) {
        return new net.minecraft.client.multiplayer.ServerData(name, ip, net.minecraft.client.multiplayer.ServerData.Type.OTHER);
    }

    public static void startConnecting(
            net.minecraft.client.gui.screens.Screen parent,
            net.minecraft.client.Minecraft minecraft,
            net.minecraft.client.multiplayer.resolver.ServerAddress address,
            net.minecraft.client.multiplayer.ServerData serverData,
            boolean quickPlay,
            Object transferState
    ) {
        net.minecraft.client.gui.screens.ConnectScreen.startConnecting(
                parent,
                minecraft,
                address,
                serverData,
                quickPlay,
                (net.minecraft.client.multiplayer.TransferState) transferState
        );
    }

    public static void renderIndeterminate(
            net.minecraft.client.gui.GuiGraphics guiGraphics,
            net.minecraft.client.gui.Font font,
            int centerX,
            int indicatorTop,
            int progressBarHeight,
            int dotsYOffset,
            float partialTick
    ) {
        net.minecraft.client.gui.components.LoadingDotsWidget dotsWidget = new net.minecraft.client.gui.components.LoadingDotsWidget(font, net.minecraft.network.chat.Component.empty());
        dotsWidget.setX(centerX - (dotsWidget.getWidth() / 2));
        int indicatorCenterY = indicatorTop + (progressBarHeight / 2);
        dotsWidget.setY(indicatorCenterY - (dotsWidget.getHeight() / 2) + dotsYOffset);
        dotsWidget.render(guiGraphics, 0, 0, partialTick);
    }

    public static void visitSelectionList(
            java.util.function.Consumer<net.minecraft.client.gui.components.AbstractWidget> consumer,
            Object selectionList
    ) {
        if (selectionList instanceof net.minecraft.client.gui.components.AbstractWidget widget) {
            consumer.accept(widget);
        }
    }

    public static java.util.UUID getProfileId(net.minecraft.network.protocol.login.ServerboundHelloPacket packet) {
        return packet.profileId();
    }

    public static void joinServer(
            com.mojang.authlib.minecraft.MinecraftSessionService service,
            java.util.UUID profileId,
            String profileName,
            String accessToken,
            String serverId
    ) throws com.mojang.authlib.exceptions.AuthenticationException {
        service.joinServer(profileId, accessToken, serverId);
    }

    public static String getCopyToClipboardValue(net.minecraft.network.chat.ClickEvent clickEvent) {
        if (clickEvent instanceof net.minecraft.network.chat.ClickEvent.CopyToClipboard copyToClipboard) {
            return copyToClipboard.value();
        }
        return null;
    }

    public static net.minecraft.network.protocol.login.ServerboundHelloPacket newServerboundHelloPacket(String name, java.util.UUID profileId) {
        return new net.minecraft.network.protocol.login.ServerboundHelloPacket(name, profileId);
    }
}
