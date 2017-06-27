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

/**
 * This class represents objects which can parse BAM files.
 * <p>
 * A <code>BamParser</code> is {@link AutoCloseable}, and is meant to be used
 * in a try-with-resources block. A <code>BamParser</code> is also an
 * {@link Iterator} over <code>T</code>, and is meant to be used as such.
 * 
 * @param <T> - the type of record to iterate over, typically a
 * {@link SingleRead} or {@link ReadPair}
 */
public abstract class BamParser<T extends Aligned<? extends Alignment>>
extends FileParser<T> {
    
    /**
     * Constructs a <code>BamParser</code> to parse the the BAM file at the
     * specified path.
     * 
     * @param p - the specified path
     * @throws NullPointerException if the path is <code>null</code>.
     */
    public BamParser(Path p) {
        super(p);
    }
    
    /**
     * Returns a {@link Stream} of {@link Alignment} objects corresponding to
     * the valid alignments contained in the BAM file.
     * <p>
     * An <code>Alignment</code> represents a BAM record which was successfully
     * aligned. Unaligned or discordantly-aligning records are not included in
     * the returned <code>Stream</code>. See {@link Alignment} and
     * {@link Aligned} for a distinction between the two.
     * 
     * @return a <code>Stream</code> of the BAM file's alignments
     */
    public abstract Stream<? extends Alignment> getAlignmentStream();
    
    
    /**
     * Returns an {@link Iterator} over {@link Alignment} objects corresponding
     * to the valid alignments contained in the BAM file.
     * <p>
     * An <code>Alignment</code> represents a BAM record which was successfully
     * aligned. Unaligned or discordantly-aligning records are not included in
     * the returned <code>Stream</code>. See {@link Alignment} and
     * {@link Aligned} for a distinction between the two.
     * 
     * @return an <code>Iterator</code> over the BAM file's alignments
     */    
    public abstract Iterator<? extends Alignment> getAlignmentIterator();
    
    /**
     * Returns a new instance of either a {@link SingleReadBamParser} or a
     * {@link PairedEndBamParser}, depending on the BAM file at the specified
     * path.
     * <p>
     * This method first checks the initial record in the BAM file and returns
     * the appropriate type of parser based on the is-paired SAM flag (the 0x1
     * bit).
     * 
     * @param p - the specified path
     * @return a <code>BamParser</code> to parse the file at the specified path
     */
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
