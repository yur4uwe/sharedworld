import { Database } from "bun:sqlite";
import { readFileSync, existsSync, mkdirSync } from "node:fs";
import { join } from "node:path";
import { createApp } from "./index.ts";
import type {
  Env, SqlDatabase,
  SqlPreparedStatement,
  SqlResultRow
} from "./env.ts";

const PORT = parseInt(process.env.PORT || "8787", 10);
const DATA_DIR = process.env.DATA_DIR || "./data";
const DB_PATH = join(DATA_DIR, "sharedworld.db");

// 1. Ensure directories exist
if (!existsSync(DATA_DIR)) {
  mkdirSync(DATA_DIR, { recursive: true });
}

// 2. Initialize Bun SQLite DB
const isNewDb = !existsSync(DB_PATH);
const db = new Database(DB_PATH);
db.exec("PRAGMA foreign_keys = ON;");
if (isNewDb) {
  const schemaSql = readFileSync(new URL("./schema.sql", import.meta.url), "utf8");
  db.exec(schemaSql);
  console.log("Database schema initialized.");
}

type SqliteBinding = string | number | bigint | Uint8Array | null;

function asSqliteBinding(value: unknown): SqliteBinding {
  if (value === undefined || value === null) {
    return null;
  }
  if (typeof value === "boolean") {
    return value ? 1 : 0;
  }
  if (
    typeof value === "string" ||
    typeof value === "number" ||
    typeof value === "bigint" ||
    value instanceof Uint8Array
  ) {
    return value;
  }
  throw new Error(`Unsupported sqlite binding type: ${typeof value}`);
}

class SqlitePreparedStatement implements SqlPreparedStatement {
  private values: SqliteBinding[] = [];

  constructor(
    private readonly db: Database,
    private readonly query: string
  ) { }

  bind(...values: unknown[]): SqlPreparedStatement {
    this.values = values.map(asSqliteBinding);
    return this;
  }

  async first<T = SqlResultRow>(): Promise<T | null> {
    const row = this.db.query(this.query).get(...this.values) as T | null | undefined;
    return row ?? null;
  }

  async all<T = SqlResultRow>(): Promise<{ results: T[] }> {
    return {
      results: this.db.query(this.query).all(...this.values) as T[]
    };
  }

  async run(): Promise<{ success: boolean; meta?: Record<string, unknown> }> {
    this.db.query(this.query).run(...this.values);
    return { success: true };
  }
}

// 3. Implement D1Database on top of bun:sqlite
class SqliteDatabase implements SqlDatabase {
  constructor(private readonly db: Database) { }
  prepare(query: string): SqlPreparedStatement {
    return new SqlitePreparedStatement(this.db, query);
  }
}

// 4. Bootstrap & Start Server
const env: Env = {
  DB: new SqliteDatabase(db),
  ACTIVE_STORAGE_PROVIDER: "local-disk",
  PUBLIC_BASE_URL: process.env.PUBLIC_BASE_URL || `http://localhost:${PORT}`,
  SESSION_TTL_HOURS: process.env.SESSION_TTL_HOURS,
  SIGNED_URL_TTL_SECONDS: process.env.SIGNED_URL_TTL_SECONDS,
  MOJANG_HAS_JOINED_ENDPOINT: process.env.MOJANG_HAS_JOINED_ENDPOINT,
  ALLOW_DEV_AUTH: process.env.ALLOW_DEV_AUTH,
  ALLOW_DEV_INSECURE_E4MC: process.env.ALLOW_DEV_INSECURE_E4MC,
  DEV_AUTH_SECRET: process.env.DEV_AUTH_SECRET,
};

const app = createApp(env);
console.log(`Starting self-hosted SharedWorld backend on port ${PORT}...`);
Bun.serve({
  port: PORT,
  fetch(request) {
    return app.fetch(request);
  }
});
