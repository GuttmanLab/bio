package edu.caltech.lncrna.bio.io;

/**
 * Signals that an exception has occurred relating to an incorrectly formatted
 * record while parsing a file.
 */
public class MalformedRecordException extends FileParseException {
    
    private static final long serialVersionUID = 664571899432293892L;

    /**
     * Constructs a <code>MalformedRecordException</code> with the specified
     * detail message and line number.
     * <p>
     * The line number is not saved separately. Rather, the phrase
     * "Check line number <code>lineNum</code>." is appended to the end of the
     * detail message.
     * 
     * @param msg - the detail message (which is saved for later retrieval by
     * the {@link Throwable#getMessage()} method)
     * @param lineNum - the line number where the malformed record was found
     */
    public MalformedRecordException(String msg, int lineNum) {
        super(msg + " Check line number " + lineNum + ".");
    }
}
