package edu.caltech.lncrna.bio.alignment;

import java.util.Objects;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Strand;

/**
 * An enumeration of orientations for paired reads.
 * <p>
 * These orientations are identical to the pair orientations used by IGV.
 * As an example, <code>PairOrientation.F2R1</code> contains a left
 * orientation, <code>F2</code>, and a right orientation, <code>R1</code>. The
 * left orientation describes the read that aligns closer to the beginning of
 * the reference. In this case, that read is the secondary read (typically
 * originating from an R2 FASTQ file prior to alignment), and it aligns
 * with a forward (positive) orientation. The right orientation describes the
 * read that aligns closer to the end of the reference. In this case, that read
 * is the primary read (typically originating from an R1 FASTQ file prior to
 * alignment), and it aligns with a reverse (negative) orientation.
 */
public enum PairOrientation {
    F1F2(false, Strand.POSITIVE),
    F1R2(true, Strand.NEGATIVE),
    R1F2(false, Strand.INVALID),
    R1R2(false, Strand.NEGATIVE),
    F2F1(false, Strand.POSITIVE),
    F2R1(true, Strand.POSITIVE),
    R2F1(false, Strand.INVALID),
    R2R1(false, Strand.NEGATIVE),
    INVALID(false, Strand.INVALID);
    
    private boolean isConcordant;
    private Strand strand;
    
    private PairOrientation(boolean isConcordant, Strand strand) {
        this.isConcordant = isConcordant;
        this.strand = strand;
    }
    
    /**
     * Returns <code>true</code> if this pair orientation describes a
     * concordant alignment.
     * @return <code>true</code> if this pair orientation describes a
     * concordant alignment
     */
    public boolean isConcordant() {
        return isConcordant;
    }
    
    /**
     * Returns <code>true</code> if this pair orientation aligns in the forward
     * orientation.
     * <ul>
     * <li>F1F2
     * <li>F2F1
     * <li>F2R1
     * </ul>
     * @return <code>true</code> if this pair orientation aligns in the forward
     * orientation
     */
    public boolean isForward() {
        return strand.equals(Strand.POSITIVE);
    }

    /**
     * Returns <code>true</code> if this pair orientation aligns in the reverse
     * orientation.
     * <ul>
     * <li>R1R2
     * <li>R2R1
     * <li>F1R2
     * </ul>
     * @return <code>true</code> if this pair orientation aligns in the reverse
     * orientation
     */
    public boolean isReverse() {
        return strand.equals(Strand.NEGATIVE);
    }

    /**
     * Returns <code>true</code> if the two orientations in this alignment
     * conflict with one another.
     * <p>
     * Note that <code>this.isInvalid() == false</code> does not imply that
     * <code>this.isConcordant() == true</code>.
     * <ul>
     * <li>R1F2
     * <li>R2F1
     * <li>INVALID
     * </ul>
     * @return <code>true</code> if the two orientations in this alignment
     * conflict with one another
     */    
    public boolean isInvalid() {
        return !(isForward() || isReverse());
    }
    
    /**
     * Returns the single orientation implied by this pair orientation.
     * <p>
     * <code>Strand.POSITIVE</code> will be returned from
     * <ul>
     * <li>F1F2
     * <li>F2F1
     * <li>F2R1
     * </ul>
     * 
     * <code>Strand.NEGATIVE</code> will be returned from
     * <ul>
     * <li>R1R2
     * <li>R2R1
     * <li>F1R2
     * </ul>
     * 
     * <code>Strand.INVALID</code> will be returned from
     * <ul>
     * <li>R1F2
     * <li>R2F1
     * <li>INVALID
     * </ul>
     * 
     * @return the single orientation implied by this pair orientation
     */
    public Strand getStrand() {
        return strand;
    }
    
    /**
     * Returns the <code>PairOrientation</code> implied if these two reads were
     * paired together.
     * 
     * @param read1 - the first read
     * @param read2 - the second read
     * 
     * @return the <code>PairOrientation</code> implied if these two reads were
     * paired together
     * @throws NullPointerException if either argument is <code>null</code>.
     */
    public static PairOrientation getPairOrientation(Annotated read1, Annotated read2) {
        Objects.requireNonNull(read1, "Read 1 cannot be null.");
        Objects.requireNonNull(read2, "Read 2 cannot be null.");

        switch (read1.getStrand()) {
        case POSITIVE:
            return getPairOrientationFromForwardRead1(read1, read2);
        case NEGATIVE:
            return getPairOrientationFromReverseRead1(read1, read2);
        default:
            return PairOrientation.INVALID;
        }
    }
    
    private static PairOrientation getPairOrientationFromForwardRead1(
            Annotated read1, Annotated read2) {
        switch (read2.getStrand()) {
        case POSITIVE:
            return getPairOrientationFromForwardRead1ForwardRead2(read1, read2);
        case NEGATIVE:
            return getPairOrientationFromForwardRead1ReverseRead2(read1, read2);
        default:
            return PairOrientation.INVALID;
        }
    }
    
    private static PairOrientation getPairOrientationFromReverseRead1(
            Annotated read1, Annotated read2) {
        switch (read2.getStrand()) {
        case POSITIVE:
            return getPairOrientationFromReverseRead1ForwardRead2(read1, read2);
        case NEGATIVE:
            return getPairOrientationFromReverseRead1ReverseRead2(read1, read2);
        default:
            return PairOrientation.INVALID;
        }
    }
    
    private static PairOrientation getPairOrientationFromForwardRead1ForwardRead2(
            Annotated read1, Annotated read2) {
        int firstReadFivePrime = read1.getFivePrimePosition();
        int secondReadFivePrime = read2.getFivePrimePosition();
        return firstReadFivePrime < secondReadFivePrime
                ? PairOrientation.F1F2
                : PairOrientation.F2F1;
    }
    
    private static PairOrientation getPairOrientationFromForwardRead1ReverseRead2(
            Annotated read1, Annotated read2) {
        int firstReadFivePrime = read1.getFivePrimePosition();
        int secondReadFivePrime = read2.getFivePrimePosition();
        return firstReadFivePrime < secondReadFivePrime
                ? PairOrientation.F1R2
                : PairOrientation.R2F1;
    }
    
    private static PairOrientation getPairOrientationFromReverseRead1ForwardRead2(
            Annotated read1, Annotated read2) {
        int firstReadFivePrime = read1.getFivePrimePosition();
        int secondReadFivePrime = read2.getFivePrimePosition();
        return firstReadFivePrime < secondReadFivePrime
                ? PairOrientation.R1F2
                : PairOrientation.F2R1;
    }
    
    private static PairOrientation getPairOrientationFromReverseRead1ReverseRead2(
            Annotated read1, Annotated read2) {
        int firstReadFivePrime = read1.getFivePrimePosition();
        int secondReadFivePrime = read2.getFivePrimePosition();
        return firstReadFivePrime < secondReadFivePrime
                ? PairOrientation.R1R2
                : PairOrientation.R2R1;
    }
}