package link.sharedworld.mixin;

import net.minecraft.world.level.chunk.storage.EntityStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityStorage.class)
public interface EntityStorageAccessor {
    @Accessor("entityDeserializerQueue")
    Object sharedworld$getEntityDeserializerQueue();
}
