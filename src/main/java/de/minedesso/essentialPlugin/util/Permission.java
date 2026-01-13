package de.minedesso.essentialPlugin.util;

public enum Permission {
    WARP_BASE("essential.warp."),
    WARP_USE(WARP_BASE.perm + "use."),
    WARP_CREATE(WARP_BASE.perm + "create"),
    WARP_DELETE(WARP_BASE.perm + "delete"),;

    public final String perm;

    Permission(String perm) {
        this.perm = perm;
    }
}
