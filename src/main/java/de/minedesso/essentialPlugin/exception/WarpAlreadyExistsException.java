package de.minedesso.essentialPlugin.exception;

public class WarpAlreadyExistsException extends RuntimeException {
    public WarpAlreadyExistsException(String warpName) {
        super("Warp '" + warpName + "' already exists.");
    }
}
