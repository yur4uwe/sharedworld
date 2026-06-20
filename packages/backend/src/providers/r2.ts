import { HttpError } from "@src/http";
import { StorageProvider, StorageBinding, StoredBlob, StorageQuota } from "@src/storage";
import { BlobData, Env } from "@src/env";

function toStoredBlob(object: BlobData): StoredBlob {
  return {
    body: object.body,
    contentType: object.httpMetadata?.contentType ?? "application/octet-stream",
    size: object.size,
    arrayBuffer() {
      return object.arrayBuffer();
    }
  };
}

export class R2StorageProvider implements StorageProvider {
  readonly provider = "r2" as const;

  constructor(private readonly env: Env) { }

  async exists(_binding: StorageBinding, storageKey: string): Promise<boolean> {
    return (await this.env.BLOBS?.head(storageKey)) != null;
  }

  async put(_binding: StorageBinding, storageKey: string, body: ReadableStream | ArrayBuffer | Uint8Array | string, contentType: string): Promise<void> {
    if (!this.env.BLOBS) {
      throw new HttpError(501, "missing_blob_bucket", "R2 binding is not configured.");
    }
    await this.env.BLOBS.put(storageKey, body, { httpMetadata: { contentType } });
  }

  async get(_binding: StorageBinding, storageKey: string): Promise<StoredBlob | null> {
    const object = await this.env.BLOBS?.get(storageKey);
    if (!object) {
      return null;
    }
    return toStoredBlob(object);
  }

  async delete(_binding: StorageBinding, storageKey: string): Promise<void> {
    await this.env.BLOBS?.delete(storageKey);
  }

  async quota(): Promise<StorageQuota> {
    return {
      usedBytes: null,
      totalBytes: null
    };
  }
}
