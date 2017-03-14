package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.alignment.Aligned;
import edu.caltech.lncrna.bio.alignment.Alignment;

public abstract class BamParser<T extends Aligned<? extends Alignment>>
extends FileParser<T> {
    
    public BamParser(Path p) {
        super(p);
    }
    
    public abstract Stream<? extends Alignment> getAlignmentStream();
    public abstract Iterator<? extends Alignment> getAlignmentIterator();
}
