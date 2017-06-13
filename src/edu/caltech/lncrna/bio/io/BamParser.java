package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.alignment.Aligned;
import edu.caltech.lncrna.bio.alignment.Alignment;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

public abstract class BamParser<T extends Aligned<? extends Alignment>>
extends FileParser<T> {
    
    public BamParser(Path p) {
        super(p);
    }
    
    public abstract Stream<? extends Alignment> getAlignmentStream();
    public abstract Iterator<? extends Alignment> getAlignmentIterator();
    
    public static BamParser<? extends Aligned<? extends Alignment>>
    newInstance(Path p) {
        return isPairedEnd(p) ? new PairedEndBamParser(p) : new SingleReadBamParser(p);
    }

    protected static SamReader getSamReaderFromPath(Path p) {
        return SamReaderFactory.makeDefault()
                .validationStringency(ValidationStringency.SILENT).open(p);
    }

    private static boolean isPairedEnd(Path p) {
        try (SAMRecordIterator iter = getSamReaderFromPath(p).iterator()) {
            return iter.hasNext() ? iter.next().getReadPairedFlag() : false;
        }
    }
}
