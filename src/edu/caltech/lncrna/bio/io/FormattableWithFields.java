package edu.caltech.lncrna.bio.io;

/**
 * Defines the behavior of objects which can be represented in a
 * human-readable text format with fields or columns. The BED format is one
 * such format. The FASTQ format is not. 
 */
public interface FormattableWithFields extends Formattable {
    
    /**
     * Returns a human-readable <code>String</code> representation of this
     * with the given number of fields, suitable for writing to disk.
     * 
     * @param numFields - the number of fields to output
     * @return a formatted <code>String</code> representation of this
     * @throws IllegalArgumentException if <code>numFields</code> is not a
     * valid number of fields for this format
     */
    public String toFormattedString(int numFields);
}
