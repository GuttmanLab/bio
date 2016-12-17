package edu.caltech.lncrna.bio.annotation;

/**
 * Objects that implement the <code>AnnotationFileRecord</code> interface are
 * able to be printed to a standard annotation file such as a BED file.
 */
public interface AnnotationFileRecord {

    /**
     * Converts this to a properly formatted <code>String</code> suitable for
     * outputting directly to a file.
     */
    public String toFormattedString();
}