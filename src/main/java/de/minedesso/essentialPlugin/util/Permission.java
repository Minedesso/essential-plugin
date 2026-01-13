package de.minedesso.essentialPlugin.util;

public enum Permission {
    WARP_USE("essential.warp."),
    WARP_CREATE("essential.warp.create"),
    WARP_DELETE("essential.warp.delete");

    public final String perm;

    Permission(String perm) {
        this.perm = perm;
    }
}
