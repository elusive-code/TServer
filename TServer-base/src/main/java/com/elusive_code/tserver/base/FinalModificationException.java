package com.elusive_code.tserver.base;

/**
 * @author Vladislav Dolgikh
 */
public class FinalModificationException extends IllegalArgumentException {

    public FinalModificationException() {
        super("Attempting to modify final key");
    }

    public FinalModificationException(String s) {
        super(s);
    }

    public FinalModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinalModificationException(Throwable cause) {
        super(cause);
    }
}
