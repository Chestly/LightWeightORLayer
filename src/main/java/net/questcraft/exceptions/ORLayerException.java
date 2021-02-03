package net.questcraft.exceptions;

public abstract class ORLayerException extends Exception {
    private String message;

    public ORLayerException(String message) {
        super(message);
        this.message = message;
    }
}
