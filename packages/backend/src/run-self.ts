import { Database } from "bun:sqlite";
import { readFileSync, existsSync, mkdirSync } from "node:fs";
import { join } from "node:path";
import { createApp } from "./index.ts";
import type {
  Env, SqlDatabase,
  SqlPreparedStatement,
  SqlResultRow
} from "./env.ts";
import { StorageProviderType } from "@shared/contracts.ts";

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

function loadWranglerTomlVars(tomlPath: string): Record<string, string> {
  const vars: Record<string, string> = {};
  if (!existsSync(tomlPath)) {
    return vars;
  }
  const content = readFileSync(tomlPath, "utf8");
  const lines = content.split(/\r?\n/);
  let inVarsSection = false;
  for (const line of lines) {
    const trimmed = line.trim();
    if (trimmed.startsWith("#") || trimmed === "") {
      continue;
    }
    if (trimmed.startsWith("[")) {
      inVarsSection = trimmed === "[vars]";
      continue;
    }
    if (inVarsSection) {
      const match = trimmed.match(/^([A-Za-z0-9_-]+)\s*=\s*(.+)$/);
      if (match) {
        const key = match[1];
        let val = match[2].trim();
        if (val.startsWith('"') && val.endsWith('"')) {
          val = val.slice(1, -1);
        } else if (val.startsWith("'") && val.endsWith("'")) {
          val = val.slice(1, -1);
        }
        vars[key] = val;
      }
    }
  }
  return vars;
}

function loadDotEnvStyle(filePath: string): Record<string, string> {
  const vars: Record<string, string> = {};
  if (!existsSync(filePath)) {
    return vars;
  }
  const content = readFileSync(filePath, "utf8");
  const lines = content.split(/\r?\n/);
  for (const line of lines) {
    const trimmed = line.trim();
    if (trimmed.startsWith("#") || trimmed === "") {
      continue;
    }
    const match = trimmed.match(/^([A-Za-z0-9_-]+)\s*=\s*(.+)$/);
    if (match) {
      const key = match[1];
      let val = match[2].trim();
      if (val.startsWith('"') && val.endsWith('"')) {
        val = val.slice(1, -1);
      } else if (val.startsWith("'") && val.endsWith("'")) {
        val = val.slice(1, -1);
      }
      vars[key] = val;
    }
  }
  return vars;
}

// 4. Bootstrap & Start Server
const wranglerTomlPath = join(import.meta.dir, "../wrangler.toml");
const devVarsPath = join(import.meta.dir, "../.dev.vars");
const envPath = join(import.meta.dir, "../.env");

const tomlVars = loadWranglerTomlVars(wranglerTomlPath);
const devVars = loadDotEnvStyle(devVarsPath);
const envVars = loadDotEnvStyle(envPath);

const mergedEnv = {
  ...tomlVars,
  ...devVars,
  ...envVars,
  ...process.env
};

const env: Env = {
  DB: new SqliteDatabase(db),
  ACTIVE_STORAGE_PROVIDER: mergedEnv.ACTIVE_STORAGE_PROVIDER as StorageProviderType || "local-disk",
  PUBLIC_BASE_URL: mergedEnv.PUBLIC_BASE_URL || `http://localhost:${PORT}`,
  SESSION_TTL_HOURS: mergedEnv.SESSION_TTL_HOURS,
  SIGNED_URL_TTL_SECONDS: mergedEnv.SIGNED_URL_TTL_SECONDS,
  MOJANG_HAS_JOINED_ENDPOINT: mergedEnv.MOJANG_HAS_JOINED_ENDPOINT,
  ALLOW_DEV_AUTH: mergedEnv.ALLOW_DEV_AUTH,
  ALLOW_DEV_INSECURE_E4MC: mergedEnv.ALLOW_DEV_INSECURE_E4MC,
  DEV_AUTH_SECRET: mergedEnv.DEV_AUTH_SECRET,
};

const app = createApp(env);
console.log(`Starting self-hosted SharedWorld backend on port ${PORT}...`);
Bun.serve({
  port: PORT,
  fetch(request) {
    return app.fetch(request);
  }
});
