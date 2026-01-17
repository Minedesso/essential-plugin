package de.minedesso.essentialplugin.exception.warp;

public class WarpDoesNotExistException extends RuntimeException {
    public WarpDoesNotExistException(String warpName) {
        super("Warp '" + warpName + "' does not exist.");
    }
}
