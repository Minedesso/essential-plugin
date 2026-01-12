package de.minedesso.essentialPlugin.exception;

public class WarpAlreadyExistsException extends RuntimeException {
    public WarpAlreadyExistsException() {
        super("Warp already exists.");
    }
}
