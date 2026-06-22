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
        minecraft.disconnect(new JoinMultiplayerScreen(new TitleScreen()));
    }

    public static void drawDeferredSubtitles(Minecraft minecraft) {
    }

    public static java.util.UUID profileId(com.mojang.authlib.GameProfile profile) {
        return profile.getId();
    }

    public static String profileName(com.mojang.authlib.GameProfile profile) {
        return profile.getName();
    }
}
