package edu.caltech.lncrna.bio.alignment;

import java.util.Objects;
import java.util.Optional;

import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.sequence.Base;
import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMRecord;

/**
 * This class represents a single read from a SAM file for which a valid
 * alignment has been found.
 */
public final class SingleReadAlignment extends Annotation
implements Alignment, SingleSamRecord {

    protected final SAMRecord samRecord;
    
    public static SingleReadAlignment newInstance(SAMRecord samRecord) {
        Objects.requireNonNull(samRecord,
                "Null SAM record passed to factory constructor.");

        if (samRecord.getReadUnmappedFlag()) {
            throw new IllegalArgumentException("Attempted to construct " +
                    "SingleReadAlignment from unmapped SAMRecord.");
        }
        
        String ref = samRecord.getReferenceName();

        int start = samRecord.getAlignmentStart();
        Strand strand = samRecord.getReadNegativeStrandFlag()
                ? Strand.NEGATIVE
                : Strand.POSITIVE;
        Annotation annot = (new AnnotationBuilder())
                .addAnnotationFromCigar(samRecord.getCigar(), ref, start, strand)
                .build();
        assert annot.getEnd() == samRecord.getAlignmentEnd() + 1:
            "BlockedAnnotation is not consistant with SAMRecord.";
        
        return new SingleReadAlignment(annot, samRecord);
    }
    
    private SingleReadAlignment(Annotation annot, SAMRecord samRecord) {
        super(annot);
        Objects.requireNonNull(samRecord, "Null SAM record passed to constructor.");
        this.samRecord = samRecord;
    }
    
    @Override
    public Base getReadBaseFromReferencePosition(String chrom, int pos) {

        Annotation interval = new Annotation(chrom, pos, pos + 1, Strand.BOTH);
        if (!overlaps(interval)) {
            return Base.INVALID;
        }
        
        int refIdx = getStart();
        int readIdx = 0;
        
        CigarIterator ops = new CigarIterator(samRecord.getCigar());
        while (ops.hasNext()) {
            CigarOperator op = ops.next();
            
            if (refIdx >= pos && op.consumesReferenceBases()) {
                if (op.equals(CigarOperator.DELETION) ||
                        op.equals(CigarOperator.SKIPPED_REGION)) {
                    return Base.INVALID;
                } else {
                    return Base.of(getBases().charAt(readIdx));
                }
            }
            
            if (op.consumesReadBases()) {
                readIdx++;
            }

            if (op.consumesReferenceBases()) {
                refIdx++;
            }
        }
        
        return Base.INVALID;
    }

    @Override
    public void writeTo(SAMFileWriter writer) {
        writer.addAlignment(samRecord);
    }

    @Override
    public String getName() {
        return samRecord.getReadName();
    }
    
    @Override
    public String getBases() {
        return samRecord.getReadString();
    }
    
    @Override
    public int length() {
        return getBases().length();
    }

    @Override
    public boolean isPaired() {
        return samRecord.getReadPairedFlag();
    }
    
    @Override
    public boolean isMappedInProperPair() {
        return samRecord.getProperPairFlag();
    }
    
    @Override
    public boolean isMapped() {
        return !samRecord.getReadUnmappedFlag();
    }
    
    @Override
    public boolean hasMappedMate() {
        return !samRecord.getMateUnmappedFlag();
    }
    
    @Override
    public boolean isOnReverseStrand() {
        return samRecord.getReadNegativeStrandFlag();
    }
    
    @Override
    public boolean hasMateOnReverseStrand() {
        return samRecord.getMateNegativeStrandFlag();
    }
    
    @Override
    public boolean isFirstInPair() {
        return samRecord.getFirstOfPairFlag();
    }
    
    @Override
    public boolean isSecondInPair() {
        return samRecord.getSecondOfPairFlag();
    }
    
    @Override
    public boolean isPrimaryAlignment() {
        return !samRecord.getNotPrimaryAlignmentFlag();
    }
    
    @Override
    public boolean passesQualityChecks() {
        return !samRecord.getReadFailsVendorQualityCheckFlag();
    }
    
    @Override
    public boolean isDuplicate() {
        return samRecord.getDuplicateReadFlag();
    }
    
    @Override
    public boolean isSupplementaryAlignment() {
        return samRecord.getSupplementaryAlignmentFlag();
    }
    
    @Override
    public int getMappingQuality() {
        return samRecord.getMappingQuality();
    }
    
    @Override
    public Cigar getCigar() {
        return samRecord.getCigar();
    }
    
    @Override
    public String getCigarString() {
        return samRecord.getCigarString();
    }
    
    @Override
    public Optional<String> getMdTag() {
        return Optional.ofNullable(samRecord.getStringAttribute("MD"));
    }
    
    @Override
    public byte[] getQualities() {
        return samRecord.getBaseQualities();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof SingleReadAlignment)) {
            return false;
        }
        
        SingleReadAlignment other = (SingleReadAlignment) o;
        
        return samRecord.equals(other.samRecord) && super.equals(other);
    }
    
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 37 * hashCode + samRecord.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}