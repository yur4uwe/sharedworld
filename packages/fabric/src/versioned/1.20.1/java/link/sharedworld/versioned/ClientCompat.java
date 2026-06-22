package link.sharedworld.versioned;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;

public final class ClientCompat {
    private ClientCompat() {
    }

    public static MinecraftSessionService sessionService(Minecraft minecraft) {
        return minecraft.getMinecraftSessionService();
    }

    public static void disconnectFromWorld(Minecraft minecraft) {
        minecraft.clearLevel(new JoinMultiplayerScreen(new TitleScreen()));
    }

    public static void drawDeferredSubtitles(Minecraft minecraft) {
    }

    public static java.util.UUID profileId(com.mojang.authlib.GameProfile profile) {
        return profile.getId();
    }

    public static String profileName(com.mojang.authlib.GameProfile profile) {
        return profile.getName();
    }

    public static void openWorld(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows flows, String levelId, Runnable callback) {
        flows.loadLevel(null, levelId);
    }

    public static void setTabActiveState(net.minecraft.client.gui.components.tabs.TabNavigationBar bar, int index, boolean active) {
        java.util.List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children = bar.children();
        if (index >= 0 && index < children.size()) {
            net.minecraft.client.gui.components.events.GuiEventListener listener = children.get(index);
            if (listener instanceof net.minecraft.client.gui.components.AbstractWidget widget) {
                widget.active = active;
            }
        }
    }

    public static void clearScreenFocus(net.minecraft.client.gui.screens.Screen screen) {
        screen.setFocused(null);
    }

    public static java.nio.file.Path getLevelPath(net.minecraft.world.level.storage.LevelStorageSource source, String levelId) {
        return source.getBaseDir().resolve(levelId);
    }

    public static net.minecraft.client.multiplayer.ServerData newServerData(String name, String ip, boolean lan) {
        return new net.minecraft.client.multiplayer.ServerData(name, ip, lan);
    }

    public static void startConnecting(
            net.minecraft.client.gui.screens.Screen parent,
            net.minecraft.client.Minecraft minecraft,
            net.minecraft.client.multiplayer.resolver.ServerAddress address,
            net.minecraft.client.multiplayer.ServerData serverData,
            boolean quickPlay,
            Object transferState
    ) {
        net.minecraft.client.gui.screens.ConnectScreen.startConnecting(parent, minecraft, address, serverData, quickPlay);
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
        long time = System.currentTimeMillis();
        int indicatorCenterY = indicatorTop + (progressBarHeight / 2) + dotsYOffset;
        for (int i = 0; i < 3; i++) {
            double angle = (time / 150.0) - (i * 0.8);
            int yNudge = (int) (Math.sin(angle) * 3.0);
            int x = centerX - 10 + (i * 10);
            int y = indicatorCenterY + yNudge;
            guiGraphics.fill(x - 2, y - 2, x + 2, y + 2, 0xFFFFFFFF);
        }
    }

    public static void visitSelectionList(
            java.util.function.Consumer<net.minecraft.client.gui.components.AbstractWidget> consumer,
            Object selectionList
    ) {
        if (selectionList instanceof VersionedObjectSelectionList<?> list) {
            consumer.accept(new WidgetSelectionListWrapper(list));
        }
    }

    public static java.util.UUID getProfileId(net.minecraft.network.protocol.login.ServerboundHelloPacket packet) {
        return packet.profileId().orElse(null);
    }

    public static void joinServer(
            com.mojang.authlib.minecraft.MinecraftSessionService service,
            java.util.UUID profileId,
            String profileName,
            String accessToken,
            String serverId
    ) throws com.mojang.authlib.exceptions.AuthenticationException {
        com.mojang.authlib.GameProfile profile = new com.mojang.authlib.GameProfile(profileId, profileName);
        service.joinServer(profile, accessToken, serverId);
    }

    public static String getCopyToClipboardValue(net.minecraft.network.chat.ClickEvent clickEvent) {
        if (clickEvent != null && clickEvent.getAction() == net.minecraft.network.chat.ClickEvent.Action.COPY_TO_CLIPBOARD) {
            return clickEvent.getValue();
        }
        return null;
    }

    public static net.minecraft.network.protocol.login.ServerboundHelloPacket newServerboundHelloPacket(String name, java.util.UUID profileId) {
        return new net.minecraft.network.protocol.login.ServerboundHelloPacket(name, java.util.Optional.ofNullable(profileId));
    }
}
