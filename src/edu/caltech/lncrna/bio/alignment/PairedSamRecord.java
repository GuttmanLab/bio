package edu.caltech.lncrna.bio.alignment;

/**
 * A pair of sequencing reads, each of which correspond to one line in a SAM
 * file.
 */
public interface PairedSamRecord extends SamRecord {
    
    /**
     * Gets the name of this.
     */
    public String getName();
    
    /**
     * Returns the first read in the pair.
     */
    public SingleSamRecord getFirstReadInPair();
    
    /**
     * Returns the second read in the pair.
     */
    public SingleSamRecord getSecondReadInPair();
}