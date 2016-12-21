package edu.caltech.lncrna.bio.alignment;

import java.util.Objects;
import java.util.Optional;

import htsjdk.samtools.SAMFileWriter;

/**
 * This class represents a pair of reads from paired-end sequencing.
 * <p>
 * Either or both of the reads represented by this object may be unaligned.
 * If both reads are aligned, a corresponding <code>PairedEndAlignment</code>
 * can be retrieved with <code>getAlignment()</code>.
 */
public class ReadPair implements PairedSamRecord, Aligned<PairedEndAlignment> {

    private final SingleRead read1;
    private final SingleRead read2;
    
    /**
     * Constructs an instance of this object from two <code>SingleRead</code>s.
     * <p>
     * The reads do not need to be added in a particular order. This constructor checks
     * the first-in-pair SAM flag and assigns the reads appropriately.
     * <p>
     * These reads do not need to originate from the same reference or even have
     * valid mappings.
     * @param read1 - one alignment
     * @param read2 - the other alignment
     * @throws NullPointerException if either argument is null
     */
    public ReadPair(SingleRead read1, SingleRead read2) {
        Objects.requireNonNull(read1, "null read-1 value passed to constructor");
        Objects.requireNonNull(read2, "null read-2 value passed to constructor");
        if (read1.isFirstInPair()) {
            this.read1 = read1;
            this.read2 = read2;
        } else {
            this.read1 = read2;
            this.read2 = read1;
        }
    }

    @Override
    public SingleRead getFirstReadInPair() {
        return read1;
    }

    @Override
    public SingleRead getSecondReadInPair() {
        return read2;
    }
    
    @Override
    public boolean hasAlignment() {
        Optional<SingleReadAlignment> align1 = read1.getAlignment();
        Optional<SingleReadAlignment> align2 = read2.getAlignment();
        if (align1.isPresent() && align2.isPresent()) {
            return align1.get().getReferenceName().equals(align2.get().getReferenceName());
        }
        return false;
    }
    
    @Override
    public Optional<PairedEndAlignment> getAlignment() {
        if (!hasAlignment()) {
            return Optional.empty();
        }
        Optional<SingleReadAlignment> align1 = read1.getAlignment();
        Optional<SingleReadAlignment> align2 = read2.getAlignment();
        assert align1.isPresent() : "align1 is empty";
        assert align2.isPresent() : "align2 is empty";
        return Optional.of(new PairedEndAlignment(align1.get(), align2.get()));
    }

    @Override
    public void writeTo(SAMFileWriter writer) {
        read1.writeTo(writer);
        read2.writeTo(writer);
    }
}