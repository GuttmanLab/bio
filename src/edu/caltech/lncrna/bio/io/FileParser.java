package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;

import edu.caltech.lncrna.bio.utils.CloseableIterator;

public abstract class FileParser<T> implements CloseableIterator<T> {

    protected final Path p;
    
    public FileParser(Path p) {
        if (p == null) {
            throw new IllegalArgumentException("FileParser constructed " +
                    "with null Path.");
        }
        this.p = p;
    }
}