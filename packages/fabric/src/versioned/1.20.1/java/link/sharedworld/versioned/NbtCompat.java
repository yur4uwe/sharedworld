package link.sharedworld.versioned;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;

public final class NbtCompat {
    private NbtCompat() {
    }

    public static NbtAccounter unlimitedHeap() {
        return NbtAccounter.UNLIMITED;
    }

    public static CompoundTag getCompoundOrEmpty(CompoundTag tag, String key) {
        return tag.contains(key, 10) ? tag.getCompound(key) : new CompoundTag();
    }

    public static CompoundTag readCompressed(java.nio.file.Path path, NbtAccounter accounter) throws java.io.IOException {
        try (java.io.InputStream stream = java.nio.file.Files.newInputStream(path);
             java.io.DataInputStream dataStream = new java.io.DataInputStream(new java.util.zip.GZIPInputStream(stream))) {
            return net.minecraft.nbt.NbtIo.read(dataStream, accounter);
        }
    }

    public static void writeCompressed(CompoundTag tag, java.nio.file.Path path) throws java.io.IOException {
        net.minecraft.nbt.NbtIo.writeCompressed(tag, path.toFile());
    }
}
