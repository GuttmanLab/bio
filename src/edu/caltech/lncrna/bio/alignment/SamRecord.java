package edu.caltech.lncrna.bio.alignment;

import htsjdk.samtools.SAMFileWriter;

public interface SamRecord {
    
    /**
     * Adds this read to a <code>SAMFileWriter</code> to be written to disk.
     * @param writer - the writer to add this read to
     */
    public void writeTo(SAMFileWriter writer);
}
