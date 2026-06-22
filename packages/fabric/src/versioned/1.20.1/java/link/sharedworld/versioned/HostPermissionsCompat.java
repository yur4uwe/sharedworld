package link.sharedworld.versioned;

import link.sharedworld.host.SharedWorldHostPermissionPolicy;

public final class HostPermissionsCompat {
    public static final int OWNER_PERMISSION_LEVEL = 4;
    public static final int DEFAULT_PERMISSION_LEVEL = 0;

    private HostPermissionsCompat() {
    }

    public static int effectivePermissions(
            int vanillaPermissions,
            boolean hostingSharedWorld,
            String requestedProfileUuid,
            String sharedWorldOwnerUuid
    ) {
        if (!hostingSharedWorld) {
            return vanillaPermissions;
        }

        return SharedWorldHostPermissionPolicy.hasSharedWorldOwnerPermissions(hostingSharedWorld, requestedProfileUuid, sharedWorldOwnerUuid)
                ? OWNER_PERMISSION_LEVEL
                : DEFAULT_PERMISSION_LEVEL;
    }
}
