package net.questcraft.exceptions;

public class FatalORLayerException extends ORLayerException {
    public FatalORLayerException(String message) {
        super(message);
    }

    public FatalORLayerException(Exception e) {
        super(e.getMessage());
    }
}
