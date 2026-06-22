package link.sharedworld.versioned;

import link.sharedworld.mixin.versioned.ChunkStorageWorkerAccessor;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.CompletableFuture;

/**
 * Version-specific chunk persistence drain for Minecraft 1.21.9 and 1.21.10, where
 * {@code ChunkMap} extends the old {@code ChunkStorage}. Its only public flush,
 * {@code flushWorker()}, blocks the calling thread, so SharedWorld reaches the underlying
 * {@code IOWorker} through an accessor to keep the drain asynchronous like newer versions.
 */
public final class WorldFlushCompat {
    private WorldFlushCompat() {
    }

    public static CompletableFuture<?> synchronizeChunks(ServerLevel level) {
        return ((ChunkStorageWorkerAccessor) level.getChunkSource().chunkMap).sharedworld$getWorker().synchronize(false);
    }

    public static boolean isAutoSave(IntegratedServer server) {
        return server.isAutoSave();
    }

    public static void setAutoSave(IntegratedServer server, boolean enabled) {
        server.setAutoSave(enabled);
    }

    public static Runnable getEntityDeserializerQueue(net.minecraft.world.level.entity.EntityPersistentStorage<?> permanentStorage) {
        if (permanentStorage instanceof net.minecraft.world.level.chunk.storage.EntityStorage entityStorage) {
            Object queue = ((link.sharedworld.mixin.versioned.EntityStorageAccessor) entityStorage).sharedworld$getEntityDeserializerQueue();
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

    public static java.util.concurrent.CompletableFuture<?> getPendingWriteFuture(net.minecraft.world.level.storage.DimensionDataStorage dataStorage) {
        return ((link.sharedworld.mixin.DimensionDataStorageAccessor) dataStorage).sharedworld$getPendingWriteFuture();
    }
}
