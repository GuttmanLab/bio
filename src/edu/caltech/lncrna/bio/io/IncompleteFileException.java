package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;

/**
 * Signals that an exception has occurred due to parsing an incomplete file.
 */
public class IncompleteFileException extends FileParseException {
    
    private static final long serialVersionUID = -7893737834805175008L;

    /**
     * Constructs an <code>IncompleteFileException</code> with the specified
     * detail message.
     * 
     * @param msg - the detail message (which is saved for later retrieval by
     * the {@link Throwable#getMessage()} method)
     */
    public IncompleteFileException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>IncompleteFileException</code> with the specified
     * <code>Path</code>.
     * <p>
     * The detail message of this exception will be "The file at
     * <code>p.toString()</code> appears to be incomplete."
     * 
     * @param p - the <code>Path</code> of the file being parsed
     */
    public IncompleteFileException(Path p) {
        super("The file at " + p.toString() + " appears to be incomplete.");
    }
}
