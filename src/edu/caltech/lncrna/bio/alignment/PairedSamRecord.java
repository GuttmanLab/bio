package edu.caltech.lncrna.bio.alignment;

import htsjdk.samtools.SAMFileWriter;

/**
 * A pair of sequencing reads, each of which correspond to one line in a SAM
 * file.
 */
public interface PairedSamRecord {
    
    /**
     * Gets the name of this.
     */
    public String getName();
    
    /**
     * Returns the first read in the pair.
     */
    public SamRecord getFirstReadInPair();
    
    /**
     * Returns the second read in the pair.
     */
    public SamRecord getSecondReadInPair();
    
    /**
     * Adds these reads to a <code>SAMFileWriter</code> to be written to disk.
     * @param writer - the writer to add these reads to
     */
    public void writeTo(SAMFileWriter writer);
}