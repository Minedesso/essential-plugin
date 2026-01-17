package de.minedesso.essentialplugin.exception.warp;

public class WarpAlreadyExistsException extends RuntimeException {
    public WarpAlreadyExistsException(String warpName) {
        super("Warp '" + warpName + "' already exists.");
    }
}
