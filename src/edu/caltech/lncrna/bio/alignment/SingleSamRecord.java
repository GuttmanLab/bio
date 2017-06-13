package edu.caltech.lncrna.bio.alignment;

import java.util.Optional;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.SAMFileWriter;
import edu.caltech.lncrna.bio.sequence.Sequence;

/**
 * An sequencing read that corresponds to one line in a SAM file.
 * <p>
 * The <code>SamRecord</code> interface provides methods to get information
 * guaranteed to exist by the SAM format specification.
 */
public interface SingleSamRecord extends SamRecord, Sequence {
    
    /**
     * If this read is paired.
     */
    public boolean isPaired();
    
    /**
     * If this read is mapped in a proper pair.
     * <p>
     * The definition of "proper pair" is aligner-specific. This method only
     * examines the SAM flag to see if the mapped-in-proper-pair bit is set.
     */
    public boolean isMappedInProperPair();
    
    /**
     * If this read is mapped.
     */
    public boolean isMapped();
    
    /**
     * If the mate of this read is mapped.
     * <p>
     * Returns false if this read is unpaired.
     */
    public boolean hasMappedMate();
    
    /**
     * If this read is on the reverse strand.
     */
    public boolean isOnReverseStrand();
    
    /**
     * If the mate of this read is on the reverse strand.
     * <p>
     * Returns false if this read is unpaired.
     */
    public boolean hasMateOnReverseStrand();
    
    /**
     * If this read is the first read in a pair.
     */
    public boolean isFirstInPair();
    
    /**
     * If this read is the second read in a pair.
     */
    public boolean isSecondInPair();
    
    /**
     * If this read is a primary (that is, not a secondary) alignment.
     */
    public boolean isPrimaryAlignment();
    
    /**
     * If this read passes filters such as platform/vendor quality controls.
     */
    public boolean passesQualityChecks();
    
    /**
     * If this read is a PCR or optical duplicate.
     */
    public boolean isDuplicate();
    
    /**
     * If this read is a supplementary alignment.
     */
    public boolean isSupplementaryAlignment();
    
    /**
     * Returns the MAPQ score of this read.
     */
    public int getMappingQuality();
    
    // TODO Javadoc
    public Cigar getCigar();
    
    public String getCigarString();
    
    /**
     * Returns the MD tag of this read as an <code>Optional</code>, if it exists;
     * otherwise, an empty <code>Optional</code>.
     */
    public Optional<String> getMdTag();
    
    public byte[] getQualities();
}