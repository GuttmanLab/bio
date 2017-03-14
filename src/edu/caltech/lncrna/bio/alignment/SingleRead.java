package edu.caltech.lncrna.bio.alignment;

import java.util.Optional;

import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMRecord;

/**
 * This class represents a single read from a SAM file.
 * <p>
 * The read represented by this object may be unmapped.
 */
public class SingleRead extends SamRecordImpl implements Aligned<SingleReadAlignment> {

    /**
     * Constructs an instance from an htsjdk <code>SAMRecord</code> object.
     * @param samRecord
     */
    public SingleRead(SAMRecord samRecord) {
        super(samRecord);
    }
    
    public Optional<String> getCigarString() {
        String cigar = samRecord.getCigarString();
        return cigar.equals("*") ? Optional.empty() : Optional.of(cigar);
    }
    
    @Override
    public boolean hasAlignment() {
        return isMapped();
    }
    
    @Override
    public Optional<SingleReadAlignment> getAlignment() {
        if (isMapped()) {
            return Optional.of(new SingleReadAlignment(samRecord));
        }
        return Optional.empty();
    }

    @Override
    public void writeTo(SAMFileWriter writer) {
        writer.addAlignment(samRecord);
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