package edu.caltech.lncrna.bio.alignment;

import htsjdk.samtools.SAMFileWriter;

/**
  * This interface defines the behavior of objects that can be written to a
  * SAM or BAM file.
  */
public interface SamRecord {
    
    /**
     * Adds this object to a <code>SAMFileWriter</code> to be written to disk.
     * 
     * @param writer - the writer to add this read to
     */
    public void writeTo(SAMFileWriter writer);
}
