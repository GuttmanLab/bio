package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;

import edu.caltech.lncrna.bio.alignment.Alignable;
import edu.caltech.lncrna.bio.alignment.Aligned;

public abstract class BamParser<T extends Alignable<? extends Aligned>> extends FileParser<T> {
    
    public BamParser(Path p) {
        super(p);
    }
}