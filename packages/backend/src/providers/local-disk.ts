import { existsSync, mkdirSync } from "node:fs";
import { stat, unlink, readFile, writeFile, mkdir } from "node:fs/promises";
import { join, dirname } from "node:path";
import type { StorageProvider, StorageBinding, StoredBlob, StorageQuota } from "@src/storage.ts";
import type { Env } from "@src/env.ts";

export class LocalDiskProvider implements StorageProvider {
  readonly provider = "local-disk" as const;
  private readonly storageDir: string;

  constructor(_env: Env) {
    const dataDir = process.env.DATA_DIR || "./data";
    this.storageDir = process.env.BLOBS_DIR || join(dataDir, "blobs");
    if (!existsSync(this.storageDir)) {
      mkdirSync(this.storageDir, { recursive: true });
    }
  }

  private getFilePath(storageKey: string): string {
    return join(this.storageDir, storageKey);
  }

  async exists(_binding: StorageBinding, storageKey: string): Promise<boolean> {
    const filePath = this.getFilePath(storageKey);
    return existsSync(filePath);
  }

  async put(
    _binding: StorageBinding,
    storageKey: string,
    body: ReadableStream | ArrayBuffer | Uint8Array | string,
    contentType: string
  ): Promise<void> {
    const filePath = this.getFilePath(storageKey);
    await mkdir(dirname(filePath), { recursive: true });

    if (body instanceof ReadableStream) {
      const { pipeline } = await import("node:stream/promises");
      const { createWriteStream } = await import("node:fs");
      const { Readable } = await import("node:stream");
      await pipeline(Readable.fromWeb(body as any), createWriteStream(filePath));
    } else {
      await writeFile(filePath, body as any);
    }

    const metaPath = filePath + ".meta";
    await writeFile(metaPath, JSON.stringify({ contentType }), "utf8");
  }

  async get(_binding: StorageBinding, storageKey: string): Promise<StoredBlob | null> {
    const filePath = this.getFilePath(storageKey);
    if (!existsSync(filePath)) {
      return null;
    }
    try {
      const stats = await stat(filePath);
      let contentType = "application/octet-stream";

      const metaPath = filePath + ".meta";
      if (existsSync(metaPath)) {
        try {
          const metaContent = await readFile(metaPath, "utf8");
          const meta = JSON.parse(metaContent);
          if (meta.contentType) {
            contentType = meta.contentType;
          }
        } catch {
          // ignore
        }
      }

      const { createReadStream } = await import("node:fs");
      const { Readable } = await import("node:stream");
      const stream = Readable.toWeb(createReadStream(filePath)) as unknown as ReadableStream;

      return {
        body: stream,
        contentType,
        size: stats.size,
        async arrayBuffer() {
          const buffer = await readFile(filePath);
          return buffer.buffer.slice(buffer.byteOffset, buffer.byteOffset + buffer.byteLength);
        }
      };
    } catch {
      return null;
    }
  }

  async delete(_binding: StorageBinding, storageKey: string): Promise<void> {
    const filePath = this.getFilePath(storageKey);
    const metaPath = filePath + ".meta";
    try {
      if (existsSync(filePath)) {
        await unlink(filePath);
      }
      if (existsSync(metaPath)) {
        await unlink(metaPath);
      }
    } catch (e) {
      console.warn("LocalDiskStorageProvider cleanup failed for", storageKey, e);
    }
  }

  async quota(): Promise<StorageQuota> {
    return {
      usedBytes: null,
      totalBytes: null
    };
  }
}
