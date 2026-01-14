package de.minedesso.essentialplugin.util;

public enum Permission {
    ESSENTIAL_BASE("essential."),

    WARP_BASE(ESSENTIAL_BASE.perm + "warp."),
    WARP_USE(WARP_BASE.perm + "use."),
    WARP_CREATE(WARP_BASE.perm + "create"),
    WARP_DELETE(WARP_BASE.perm + "delete"),

    INV_BASE(ESSENTIAL_BASE.perm + "inv."),
    INV_SEE(INV_BASE.perm + "see"),
    INV_EC(INV_BASE.perm + "ec"),

    HOME_BASE(ESSENTIAL_BASE.perm + "home."),
    HOME_COUNT(HOME_BASE.perm + "count.");

    public final String perm;

    Permission(String perm) {
        this.perm = perm;
    }
}
