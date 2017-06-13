package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.Objects;

import edu.caltech.lncrna.bio.utils.CloseableIterator;

public abstract class FileParser<T> implements CloseableIterator<T> {

    protected final Path p;
    
    public FileParser(Path p) {
        Objects.requireNonNull(p, "Attempted to create FileParser with null " +
                "Path");
        this.p = p;
    }
}