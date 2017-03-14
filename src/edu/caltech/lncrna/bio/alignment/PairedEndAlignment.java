package edu.caltech.lncrna.bio.alignment;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.BlockedAnnotation.BlockedBuilder;
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
        
        pairOrientation = PairOrientation.getPairOrientation(read1, read2);
        Strand strand = pairOrientation.getStrand();
        annot = (new BlockedBuilder())
                .addBlocksFromCigar(read1.samRecord.getCigar(), ref1,
                        read1.getStart(), strand)
                .addBlocksFromCigar(read2.samRecord.getCigar(), ref2,
                        read2.getStart(), strand)
                .build();
        assert annot.getEnd() == Math.max(read1.getEnd(), read2.getEnd()) :
            "BlockedAnnotation is not consistant with SAMRecord.";
    }
    
    public PairOrientation getPairOrientation() {
        return pairOrientation;
    }
    
    @Override
    public String getName() {
        return read1.getName();
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
    public int getFivePrimePosition() {
        return annot.getFivePrimePosition();
    }
    
    @Override
    public int getThreePrimePosition() {
        return annot.getThreePrimePosition();
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
    public Iterator<Annotated> iterator() {
        return getBlockIterator();
    }
    
    @Override
    public Iterator<Annotated> getBlockIterator() {
        return annot.getBlockIterator();
    }

    @Override
    public Stream<Annotated> getBlockStream() {
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
    public Annotated getBody() {
        return annot.getBody();
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
    
    @Override
    public Optional<Annotated> getIntrons() {
        return annot.getIntrons();
    }

    @Override
    public Iterator<Annotated> getIntronIterator() {
        return annot.getIntronIterator();
    }

    @Override
    public Stream<Annotated> getIntronStream() {
        return annot.getIntronStream();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof PairedEndAlignment)) {
            return false;
        }
        
        PairedEndAlignment other = (PairedEndAlignment) o;
        
        return read1.equals(other.read1) &&
               read2.equals(other.read2) &&
               annot.equals(other.annot) &&
               pairOrientation.equals(other.pairOrientation);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + read1.hashCode();
        hashCode = 37 * hashCode + read2.hashCode();
        hashCode = 37 * hashCode + annot.hashCode();
        hashCode = 37 * hashCode + pairOrientation.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString() {
        return annot.toString();
    }
}
