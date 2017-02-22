package edu.caltech.lncrna.bio.alignment;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.Strand;
import htsjdk.samtools.SAMFileWriter;

/**
 * This class represents a successfully aligned paired-end fragment.
 */
public final class PairedEndAlignment implements PairedSamRecord, Alignment {
    
    private final SingleReadAlignment read1;
    private final SingleReadAlignment read2;
    private final Annotated annot;
    private final PairOrientation pairOrientation;

    /**
     * Constructs an instance of this class from two <code>SingleReadAlignment</code>s.
     * <p>
     * The reads do not need to be added in a particular order. This constructor checks
     * the first-in-pair SAM flag and assigns the reads appropriately.
     * <p>
     * If both reads originate from the same strand, the orientation of this object is
     * set to match. Otherwise, it is set to <code>Strand.BOTH</code>.
     * @param read1 - one alignment
     * @param read2 - the other alignment
     * @throws NullPointerException if either argument is null
     * @throws IllegalArgumentException if the reads do not align to the same reference
     */
    public PairedEndAlignment(SingleReadAlignment read1, SingleReadAlignment read2) {
        Objects.requireNonNull(read1, "Attempted to construct a PairedEndAlignment " +
                "with a null read1");
        Objects.requireNonNull(read2, "Attempted to construct a PairedEndAlignment " +
                "with a null read2");
        
        if (read1.isFirstInPair()) {
            this.read1 = read1;
            this.read2 = read2;
        } else {
            this.read1 = read2;
            this.read2 = read1;
        }

        String ref1 = read1.getReferenceName();
        String ref2 = read2.getReferenceName();
        if (!ref1.equals(ref2)) {
            throw new IllegalArgumentException("Attemped to construct a " +
                    "PairedEndAlignment with reads that align to different " +
                    "references: " + ref1 + ", " + ref2);
        }
        
        int start = Math.min(read1.getStart(), read2.getStart());
        int end = Math.max(read1.getEnd(), read2.getEnd());
        
        pairOrientation = PairOrientation.getPairOrientation(read1, read2);
        Strand strand = pairOrientation.getStrand();
        annot = new Block(ref1, start, end, strand);
    }
    
    @Override
    public SingleReadAlignment getFirstReadInPair() {
        return read1;
    }

    @Override
    public SingleReadAlignment getSecondReadInPair() {
        return read2;
    }

    @Override
    public String getReferenceName() {
        return annot.getReferenceName();
    }

    @Override
    public int getStart() {
        return annot.getStart();
    }

    @Override
    public int getEnd() {
        return annot.getEnd();
    }

    @Override
    public int getSize() {
        return annot.getSize();
    }

    @Override
    public int getSpan() {
        return annot.getSpan();
    }

    @Override
    public Strand getStrand() {
        return annot.getStrand();
    }

    @Override
    public int getNumberOfBlocks() {
        return annot.getNumberOfBlocks();
    }

    @Override
    public Iterator<Block> getBlockIterator() {
        return annot.getBlockIterator();
    }

    @Override
    public Stream<Block> getBlockStream() {
        return annot.getBlockStream();
    }

    @Override
    public boolean overlaps(Annotated other) {
        return annot.overlaps(other);
    }

    @Override
    public boolean isAdjacentTo(Annotated other) {
        return annot.isAdjacentTo(other);
    }

    @Override
    public Annotated getHull() {
        return annot.getHull();
    }

    @Override
    public Optional<Annotated> minus(Annotated other) {
        return annot.minus(other);
    }

    @Override
    public Optional<Annotated> intersect(Annotated other) {
        return annot.intersect(other);
    }

    @Override
    public boolean contains(Annotated other) {
        return annot.contains(other);
    }

    @Override
    public int getPositionRelativeToFivePrime(int absolutePosition) {
        return annot.getPositionRelativeToFivePrime(absolutePosition);
    }
    
    @Override
    public int getReadPositionFromReferencePosition(int referencePosition) {
        return annot.getReadPositionFromReferencePosition(referencePosition);
    }

    @Override
    public int getReferencePositionFromReadPosition(int readPosition) {
        return annot.getReferencePositionFromReadPosition(readPosition);
    }

    @Override
    public void writeTo(SAMFileWriter writer) {
        read1.writeTo(writer);
        read2.writeTo(writer);
    }
    
    /**
     * Returns the insert size of this <code>PairedEndAlignment</code>.
     * <p>
     * If the reads overlap, this method returns 0.
     */
    public int getInsertSize() {
        int read1Start = read1.getStart();
        int read2Start = read2.getStart();
        int read1End = read1.getEnd();
        int read2End = read2.getEnd();
        
        if (read1Start < read2End && read2Start < read1End) {
            return 0;
        }
        
        return Math.max(read1Start, read2Start) - Math.min(read1End, read2End);
    }
}
