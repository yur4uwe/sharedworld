import type { StorageProviderType } from "@shared/index.ts";

import type { Env } from "./env.ts";
import type { SharedWorldRepository } from "./repository.ts";
import { R2StorageProvider } from "./providers/r2.ts";
import { GoogleDriveStorageProvider } from "./providers/google-drive.ts";
import { LocalDiskProvider } from "./providers/local-disk.ts";

export { R2StorageProvider, GoogleDriveStorageProvider, LocalDiskProvider };

export interface StorageBinding {
  provider: StorageProviderType;
  storageAccountId: string | null;
}

export interface StoredBlob {
  body: ReadableStream | null;
  contentType: string;
  size: number;
  arrayBuffer(): Promise<ArrayBuffer>;
}

export interface StorageQuota {
  usedBytes: number | null;
  totalBytes: number | null;
}

export interface StorageProvider {
  readonly provider: StorageProviderType;
  exists(binding: StorageBinding, storageKey: string): Promise<boolean>;
  put(binding: StorageBinding, storageKey: string, body: ReadableStream | ArrayBuffer | Uint8Array | string, contentType: string): Promise<void>;
  get(binding: StorageBinding, storageKey: string): Promise<StoredBlob | null>;
  delete(binding: StorageBinding, storageKey: string): Promise<void>;
  quota(binding: StorageBinding): Promise<StorageQuota>;
}

export function createStorageProvider(env: Env, repository: SharedWorldRepository): StorageProvider {
  const active = env.ACTIVE_STORAGE_PROVIDER;
  switch (active) {
    case "r2":
      return new R2StorageProvider(env);
    case "local-disk":
      return new LocalDiskProvider(env);
    default:
      return new GoogleDriveStorageProvider(env, repository);
  }
}
