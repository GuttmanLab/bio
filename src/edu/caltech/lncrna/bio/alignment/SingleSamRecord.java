package edu.caltech.lncrna.bio.alignment;

import java.util.Optional;

import htsjdk.samtools.Cigar;
import edu.caltech.lncrna.bio.sequence.Sequence;

/**
 * This interface defines the behavior of a single sequencing read.
 * <p>
 * The <code>SingleSamRecord</code> interface provides methods to get
 * information guaranteed to exist by the SAM format specification.
 * <p>
 * Each instance of <code>SingleSamRecord</code> corresponds to one line in a
 * SAM file. Read pairs do not implement this interface, although each of the
 * constituent reads does.
 */
public interface SingleSamRecord extends SamRecord, Sequence {
    
    /**
     * Returns <code>true</code> if this read is paired.
     * @return <code>true</code> if this read is paired
     */
    public boolean isPaired();
    
    /**
     * Returns <code>true</code> if this read is mapped in a proper pair.
     * <p>
     * The definition of "proper pair" is aligner-specific. This method only
     * examines the SAM flag to see if the mapped-in-proper-pair bit is set.
     * 
     * @return <code>true</code> if this read is mapped in a proper pair
     */
    public boolean isMappedInProperPair();
    
    /**
     * Returns <code>true</code if this read is mapped.
     * 
     * @return <code>true</code if this read is mapped
     */
    public boolean isMapped();
    
    /**
     * Returns <code>true</code> if the mate of this read is mapped.
     * <p>
     * Returns <code>false</code> if this read is unpaired.
     * 
     * @return <code>true</code> if the mate of this read is mapped
     */
    public boolean hasMappedMate();
    
    /**
     * Returns <code>true</code> if this read is on the reverse strand.
     * 
     * @return <code>true</code> if this read is on the reverse strand
     */
    public boolean isOnReverseStrand();
    
    /**
     * Returns <code>true</code> if the mate of this read is on the reverse
     * strand.
     * <p>
     * Returns <code>false</code> if this read is unpaired.
     * 
     * @return <code>true</code> if the mate of this read is on the reverse
     * strand
     */
    public boolean hasMateOnReverseStrand();
    
    /**
     * Returns <code>true</code> if this read is the first read in a pair.
     * 
     * @return <code>true</code> if this read is the first read in a pair
     */
    public boolean isFirstInPair();
    
    /**
     * Returns <code>true</code> if this read is the second read in a pair.
     * 
     * @return <code>true</code> if this read is the second read in a pair
     */
    public boolean isSecondInPair();
    
    /**
     * Returns <code>true</code> if this read is a primary (that is, not a
     * secondary) alignment.
     * 
     * @return <code>true</code> if this read is a primary alignment
     */
    public boolean isPrimaryAlignment();
    
    /**
     * Returns <code>true</code> if this read passes filters such as
     * platform/vendor quality checks.
     * 
     * @return <code>true</code> if this read passes quality checks
     */
    public boolean passesQualityChecks();
    
    /**
     * Returns <code>true</code> if this read is a PCR or optical duplicate.
     * 
     * @return <code>true</code> if this read is a PCR or optical duplicate
     */
    public boolean isDuplicate();
    
    /**
     * Returns <code>true</code> if this read is a supplementary alignment.
     * 
     * @return <code>true</code> if this read is a supplementary alignment
     */
    public boolean isSupplementaryAlignment();
    
    /**
     * Returns the MAPQ score of this read.
     * 
     * @return the MAPQ score of this read
     */
    public int getMappingQuality();
    
    /**
     * Returns the htsjdk <code>Cigar</code> of this read.
     * 
     * @return the htsjdk <code>Cigar</code> of this read
     */
    public Cigar getCigar();
    
    /**
     * Returns the CIGAR string of this read.
     * 
     * @return the CIGAR string of this read
     */
    public String getCigarString();
    
    /**
     * Returns the MD tag of this read wrapped in an <code>Optional</code>, if
     * it exists; otherwise, an empty <code>Optional</code> instance.
     */
    public Optional<String> getMdTag();
    
    /**
     * Returns the Phred quality scores of the bases as a <code>byte[]</code>.
     * <p>
     * The returned array contains the numerical scores and not the characters
     * from the string representation in the BAM/SAM file.
     *  
     * @return the Phred quality scores of the bases
     */
    public byte[] getQualities();
}