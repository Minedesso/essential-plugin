package de.minedesso.essentialPlugin.exception;

public class WarpDoesNotExistException extends RuntimeException {
    public WarpDoesNotExistException(String warpName) {
        super("Warp '" + warpName + "' does not exist.");
    }
}
