import { json } from "@src/http.ts";
import type { Env } from "@src/env.ts";
import { RouteDefinition, UrlPattern } from "./shared.ts";

/**
 * Unauthenticated capabilities endpoint.
 * Clients use this to discover backend feature flags before showing UI
 * that requires specific backend support (e.g. Google Drive linking).
 */
export function capabilitiesRoutes(env: Env): RouteDefinition[] {
  return [
    {
      method: "GET",
      pattern: new UrlPattern({ pathname: "/capabilities" }),
      handler: async () =>
        json({
          storageProvider: env.ACTIVE_STORAGE_PROVIDER ?? "google-drive"
        })
    }
  ];
}
