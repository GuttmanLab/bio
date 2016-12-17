package edu.caltech.lncrna.bio.alignment;

import java.util.Optional;

import htsjdk.samtools.SAMRecord;

/**
 * This class represents any object corresponding to a single line in a SAM
 * file.
 * <p>
 * As currently implemented, this class is a simple wrapper class for the
 * <code>SAMRecord</code> class in the htsjdk library.
 */
public abstract class SamRecordImpl implements SamRecord {
    
    protected final SAMRecord samRecord;

    /**
     * Constructs an instance from an htsjdk <code>SAMRecord</code> object.
     * @param samRecord - the SAM record backing up this object
     */
    public SamRecordImpl(SAMRecord samRecord) {
        this.samRecord = samRecord;
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
    public Optional<String> getMdTag() {
        return Optional.ofNullable(samRecord.getStringAttribute("MD"));
    }
    
    @Override
    public byte[] getQualities() {
        return samRecord.getBaseQualities();
    }
}