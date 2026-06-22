package link.sharedworld.mixin.versioned;

import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.util.thread.ConsecutiveExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityStorage.class)
public interface EntityStorageAccessor {
    @Accessor("entityDeserializerQueue")
    ConsecutiveExecutor sharedworld$getEntityDeserializerQueue();
}
