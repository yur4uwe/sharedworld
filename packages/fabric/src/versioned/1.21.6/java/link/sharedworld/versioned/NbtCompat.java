package link.sharedworld.versioned;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;

public final class NbtCompat {
    private NbtCompat() {
    }

    public static NbtAccounter unlimitedHeap() {
        return NbtAccounter.unlimitedHeap();
    }

    public static CompoundTag getCompoundOrEmpty(CompoundTag tag, String key) {
        return tag.getCompoundOrEmpty(key);
    }

    public static CompoundTag readCompressed(java.nio.file.Path path, NbtAccounter accounter) throws java.io.IOException {
        return net.minecraft.nbt.NbtIo.readCompressed(path, accounter);
    }

    public static void writeCompressed(CompoundTag tag, java.nio.file.Path path) throws java.io.IOException {
        net.minecraft.nbt.NbtIo.writeCompressed(tag, path);
    }
}
