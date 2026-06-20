import { errorResponse, HttpError } from "./http.ts";
import type { RequestContext } from "./repository.ts";
import { authRoutes } from "./router/auth-routes.ts";
import { capabilitiesRoutes } from "./router/capabilities-routes.ts";
import { runtimeRoutes } from "./router/runtime-routes.ts";
import { snapshotRoutes } from "./router/snapshot-routes.ts";
import { authenticate, decodeStorageKey, type RouteDefinition, type RouterService } from "./router/shared.ts";
import { storageRoutes } from "./router/storage-routes.ts";
import { worldRoutes } from "./router/world-routes.ts";
import type { Env } from "./env.ts";

export function createRouter(service: RouterService, env: Env = {}) {
  const routes: RouteDefinition[] = [
    ...capabilitiesRoutes(env),
    ...authRoutes(service),
    ...storageRoutes(service),
    ...worldRoutes(service),
    ...runtimeRoutes(service),
    ...snapshotRoutes(service)
  ];

  return async function route(request: Request): Promise<Response> {
    const url = new URL(request.url);
    const start = Date.now();
    try {
      for (const route of routes) {
        const match = route.pattern.exec(request.url);
        if (!match) {
          continue;
        }
        if (route.method !== request.method) {
          continue;
        }
        const ctx: RequestContext = route.auth
          ? await authenticate(request, service)
          : { playerUuid: "", playerName: "" };
        const response = await route.handler(request, match.pathname.groups, ctx);
        console.log(`[${request.method}] ${url.pathname} - ${response.status} (${Date.now() - start}ms)`);
        return response;
      }
      throw new HttpError(404, "not_found", "Route not found.");
    } catch (error) {
      const isExpected = error instanceof HttpError && error.status < 500;
      if (isExpected) {
        console.warn(`[${request.method}] ${url.pathname} - ${error.status} ${error.code}: ${error.message}`);
      } else {
        console.error(`[${request.method}] ${url.pathname} - Internal Error:`, error);
      }
      return errorResponse(error);
    }
  };
}

export { decodeStorageKey };
export type { RouterService };
