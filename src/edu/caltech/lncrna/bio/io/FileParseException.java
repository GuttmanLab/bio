package edu.caltech.lncrna.bio.io;

/**
 * Signals that an exception has occurred when parsing a file.
 */
public abstract class FileParseException extends RuntimeException {

    private static final long serialVersionUID = -8847917223716984846L;

    /**
     * Constructs a <code>FileParseException</code> with the specified detail
     * message.
     * 
     * @param msg - the detail message (which is saved for later retrieval by
     * the {@link Throwable#getMessage()} method)
     */
    public FileParseException(String msg) {
        super(msg);
    }
}
