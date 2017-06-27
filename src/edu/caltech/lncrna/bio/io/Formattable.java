package edu.caltech.lncrna.bio.io;

/**
 * Defines the behavior of objects which can be represented in a
 * human-readable text format. A class that implements <code>Formattable</code>
 * typically has an associated file format, e.g., BED.
 */
public interface Formattable {
    
    /**
     * Returns a human-readable <code>String</code> representation of this,
     * suitable for writing to disk.
     * 
     * @return a formatted <code>String</code> representation of this
     */
    public String toFormattedString();
}
