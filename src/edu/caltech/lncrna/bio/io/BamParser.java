package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;

import edu.caltech.lncrna.bio.alignment.Aligned;
import edu.caltech.lncrna.bio.alignment.Alignment;

public abstract class BamParser<T extends Aligned<? extends Alignment>> extends FileParser<T> {
    
    public BamParser(Path p) {
        super(p);
    }
}