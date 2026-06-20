import { type StorageProviderType } from "@shared/contracts";

export interface Env {
  DB?: SqlDatabase;
  BLOBS?: BlobBucket;
  ACTIVE_STORAGE_PROVIDER?: StorageProviderType;
  SESSION_TTL_HOURS?: string;
  PUBLIC_BASE_URL?: string;
  SIGNED_URL_TTL_SECONDS?: string;
  MOJANG_HAS_JOINED_ENDPOINT?: string;
  SIGNING_SECRET?: string;
  ALLOW_DEV_AUTH?: string;
  ALLOW_DEV_INSECURE_E4MC?: string;
  DEV_AUTH_SECRET?: string;
  ALLOW_DEV_GOOGLE_OAUTH?: string;
  GOOGLE_OAUTH_CLIENT_ID?: string;
  GOOGLE_OAUTH_CLIENT_SECRET?: string;
  GOOGLE_OAUTH_REDIRECT_URI?: string;
  GOOGLE_OAUTH_SCOPES?: string;
  GOOGLE_DRIVE_API_BASE?: string;
  DEV_GOOGLE_EMAIL?: string;
  DRIVE_MAX_PARALLEL_DOWNLOADS?: string;
  DRIVE_MAX_UPLOAD_PREPARATIONS?: string;
  DRIVE_MAX_CONCURRENT_UPLOADS?: string;
  DRIVE_MAX_UPLOAD_STARTS_PER_SECOND?: string;
  DRIVE_RETRY_BASE_DELAY_MS?: string;
  DRIVE_RETRY_MAX_DELAY_MS?: string;
}

export interface SqlResultRow {
  [key: string]: unknown;
}

export interface SqlPreparedStatement {
  bind(...values: unknown[]): SqlPreparedStatement;
  first<T = SqlResultRow>(): Promise<T | null>;
  all<T = SqlResultRow>(): Promise<{ results: T[] }>;
  run(): Promise<{ success: boolean; meta?: Record<string, unknown> }>;
}

export interface SqlDatabase {
  prepare(query: string): SqlPreparedStatement;
}

export interface BlobBucket {
  head(key: string): Promise<BlobMetadata | null>;
  get(key: string): Promise<BlobData | null>;
  delete(key: string): Promise<void>;
  put(
    key: string,
    value: ReadableStream | ArrayBuffer | ArrayBufferView | string | null,
    options?: { httpMetadata?: { contentType?: string } }
  ): Promise<void>;
}

export interface BlobMetadata {
  key: string;
  size: number;
}

export interface BlobData extends BlobMetadata {
  body: ReadableStream | null;
  arrayBuffer(): Promise<ArrayBuffer>;
  httpMetadata?: {
    contentType?: string;
  };
}
