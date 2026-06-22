package link.sharedworld.mixin;

import link.sharedworld.SharedWorldClient;
import link.sharedworld.SharedWorldDisconnectFlow;
import link.sharedworld.SharedWorldPlaySessionTracker;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftDisconnectMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("sharedworld-disconnect");

    @Inject(method = "disconnectFromWorld", at = @At("HEAD"), require = 0)
    private void sharedworld$markUserInitiatedDisconnectFromWorld(Component message, CallbackInfo callbackInfo) {
        this.sharedworld$handleDisconnect();
    }

    @Inject(method = "disconnect", at = @At("HEAD"), require = 0)
    private void sharedworld$markUserInitiatedDisconnect(CallbackInfo callbackInfo) {
        this.sharedworld$handleDisconnect();
    }

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"), require = 0)
    private void sharedworld$markUserInitiatedClearLevel(net.minecraft.client.gui.screens.Screen screen, CallbackInfo callbackInfo) {
        this.sharedworld$handleDisconnect();
    }

    @Inject(method = "clearLevel()V", at = @At("HEAD"), require = 0)
    private void sharedworld$markUserInitiatedClearLevelNoArgs(CallbackInfo callbackInfo) {
        this.sharedworld$handleDisconnect();
    }

    private void sharedworld$handleDisconnect() {
        Minecraft minecraft = Minecraft.getInstance();
        SharedWorldPlaySessionTracker.ActiveWorldSession session = SharedWorldClient.playSessionTracker().currentSession();
        SharedWorldDisconnectFlow.DisconnectAction action = SharedWorldDisconnectFlow.decide(
                SharedWorldClient.releaseCoordinator().consumeDisconnectPassThrough(),
                minecraft.isLocalServer(),
                SharedWorldClient.hostingManager().activeHostSession() != null,
                session
        );
        switch (action) {
            case IGNORE_PASS_THROUGH -> LOGGER.info("Skipping SharedWorld disconnect detection because release pass-through is armed.");
            case GUEST_ONLY -> {
                LOGGER.info("Observed SharedWorld guest disconnect; marking the session as user-initiated.");
                SharedWorldClient.playSessionTracker().markUserInitiatedDisconnect();
            }
            case HOST_GRACEFUL_RELEASE -> {
                LOGGER.info("Observed SharedWorld host disconnect on a local server; starting graceful release.");
                SharedWorldClient.playSessionTracker().markUserInitiatedDisconnect();
                SharedWorldClient.releaseCoordinator().beginGracefulDisconnect(minecraft);
            }
            case NO_SHAREDWORLD_ACTION -> LOGGER.debug("Observed disconnect without an active SharedWorld host session; no graceful release needed.");
        }
    }
}
