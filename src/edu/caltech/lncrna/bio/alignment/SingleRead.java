package edu.caltech.lncrna.bio.alignment;

import java.util.Optional;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMRecord;

/**
 * This class represents a single read from a SAM file.
 * <p>
 * The read represented by this object may be unmapped.
 */
public final class SingleRead
implements Aligned<SingleReadAlignment>, SingleSamRecord {

    private final SAMRecord samRecord;
    
    /**
     * Constructs an instance from an htsjdk <code>SAMRecord</code> object.
     * @param samRecord
     */
    public SingleRead(SAMRecord samRecord) {
        this.samRecord = samRecord;
    }
    
    @Override
    public boolean hasAlignment() {
        return isMapped();
    }
    
    @Override
    public Optional<SingleReadAlignment> getAlignment() {
        if (isMapped()) {
            return Optional.of(SingleReadAlignment.newInstance(samRecord));
        }
        return Optional.empty();
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
        
        if (!(o instanceof SingleRead)) {
            return false;
        }
        
        SingleRead other = (SingleRead) o;
        
        return samRecord.equals(other.samRecord);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + samRecord.hashCode();
        return hashCode;
    }
}