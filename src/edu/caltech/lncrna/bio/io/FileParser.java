package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.Objects;

import edu.caltech.lncrna.bio.utils.CloseableIterator;

/**
 * This class represents objects which can parse files.
 * <p>
 * A <code>FileParser</code> is {@link AutoCloseable}, and is meant to be used
 * in a try-with-resources block. A <code>FileParser</code> is also an
 * {@link Iterator} over <code>T</code>, and is meant to be used as such.
 * 
 * @param <T> - the type of record to iterate over
 */
public abstract class FileParser<T> implements CloseableIterator<T> {

    protected final Path p;
    
    /**
     * Constructs a <code>FileParser</code> to parse the the file at the
     * specified path.
     * 
     * @param p - the specified path
     * @throws NullPointerException if the path is <code>null</code>.
     */
    public FileParser(Path p) {
        Objects.requireNonNull(p, "Attempted to create FileParser with null " +
                "path");
        this.p = p;
    }
}