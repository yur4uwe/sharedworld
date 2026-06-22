package link.sharedworld.versioned;

import link.sharedworld.mixin.versioned.ChunkStorageWorkerAccessor;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.CompletableFuture;

/**
 * Version-specific chunk persistence controls for Minecraft 1.21.6-1.21.8, where
 * {@code ChunkMap} extends the old {@code ChunkStorage} (blocking {@code flushWorker()}
 * only, so the drain reaches the underlying {@code IOWorker} through an accessor) and
 * the integrated server has no autosave toggle: per-level {@code noSave} is the switch
 * vanilla itself used for save suppression in this era.
 */
public final class WorldFlushCompat {
    private WorldFlushCompat() {
    }

    public static CompletableFuture<?> synchronizeChunks(ServerLevel level) {
        return ((ChunkStorageWorkerAccessor) level.getChunkSource().chunkMap).sharedworld$getWorker().synchronize(false);
    }

    public static boolean isAutoSave(IntegratedServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            return !level.noSave;
        }
        return true;
    }

    public static void setAutoSave(IntegratedServer server, boolean enabled) {
        for (ServerLevel level : server.getAllLevels()) {
            level.noSave = !enabled;
        }
    }

    public static Runnable getEntityDeserializerQueue(net.minecraft.world.level.entity.EntityPersistentStorage<?> permanentStorage) {
        if (permanentStorage instanceof net.minecraft.world.level.chunk.storage.EntityStorage entityStorage) {
            Object queue = ((link.sharedworld.mixin.EntityStorageAccessor) entityStorage).sharedworld$getEntityDeserializerQueue();
            if (queue instanceof net.minecraft.util.thread.ConsecutiveExecutor executor) {
                return () -> executor.runAll();
            }
        }
        return null;
    }

    public static java.util.concurrent.CompletableFuture<?> synchronizeStorage(net.minecraft.world.level.entity.EntityPersistentStorage<?> permanentStorage) {
        if (permanentStorage instanceof net.minecraft.world.level.chunk.storage.EntityStorage entityStorage) {
            return ((link.sharedworld.mixin.versioned.EntityStorageSimpleRegionStorageAccessor) entityStorage).sharedworld$getSimpleRegionStorage().synchronize(false);
        }
        return java.util.concurrent.CompletableFuture.completedFuture(null);
    }
}
