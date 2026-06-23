package link.sharedworld.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor("minecraft")
    Minecraft sharedworld$getMinecraft();

    @Accessor("minecraft")
    void sharedworld$setMinecraft(Minecraft minecraft);
}
