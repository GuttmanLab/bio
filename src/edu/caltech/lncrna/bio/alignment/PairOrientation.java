package edu.caltech.lncrna.bio.alignment;

import java.util.Objects;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Strand;

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
    
    public boolean isConcordant() {
        return isConcordant;
    }
    
    public boolean isForward() {
        return strand.equals(Strand.POSITIVE);
    }
    
    public boolean isReverse() {
        return strand.equals(Strand.NEGATIVE);
    }
    
    public boolean isInvalid() {
        return !(isForward() || isReverse());
    }
    
    public Strand getStrand() {
        return strand;
    }
    
    public static PairOrientation getPairOrientation(Annotated read1, Annotated read2) {
        Objects.requireNonNull(read1, "Read1 cannot be null.");
        Objects.requireNonNull(read2, "Read2 cannot be null.");

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
        int firstReadFivePrime = read1.getReferencePositionFromReadPosition(0);
        int secondReadFivePrime = read2.getReferencePositionFromReadPosition(0);
        return firstReadFivePrime < secondReadFivePrime
                ? PairOrientation.F1F2
                : PairOrientation.F2F1;
    }
    
    private static PairOrientation getPairOrientationFromForwardRead1ReverseRead2(
            Annotated read1, Annotated read2) {
        int firstReadFivePrime = read1.getReferencePositionFromReadPosition(0);
        int secondReadFivePrime = read2.getReferencePositionFromReadPosition(0);
        return firstReadFivePrime < secondReadFivePrime
                ? PairOrientation.F1R2
                : PairOrientation.R2F1;
    }
    
    private static PairOrientation getPairOrientationFromReverseRead1ForwardRead2(
            Annotated read1, Annotated read2) {
        int firstReadFivePrime = read1.getReferencePositionFromReadPosition(0);
        int secondReadFivePrime = read2.getReferencePositionFromReadPosition(0);
        return firstReadFivePrime < secondReadFivePrime
                ? PairOrientation.R1F2
                : PairOrientation.F2R1;
    }
    
    private static PairOrientation getPairOrientationFromReverseRead1ReverseRead2(
            Annotated read1, Annotated read2) {
        int firstReadFivePrime = read1.getReferencePositionFromReadPosition(0);
        int secondReadFivePrime = read2.getReferencePositionFromReadPosition(0);
        return firstReadFivePrime < secondReadFivePrime
                ? PairOrientation.R1R2
                : PairOrientation.R2R1;
    }
}