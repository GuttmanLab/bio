package edu.caltech.lncrna.bio.alignment;

import java.util.Iterator;
import java.util.NoSuchElementException;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;

/**
 * An iterator over an htsjdk <code>Cigar</code> object.
 * <p>
 * Iterating over the CIGAR "2M1D1M" will result in the following sequence of
 * htsjdk <code>CigarOperator</code> enums:
 * <li><code>CigarOperator.MATCH_OR_MISMATCH</code>
 * <li><code>CigarOperator.MATCH_OR_MISMATCH</code>
 * <li><code>CigarOperator.DELETION</code>
 * <li><code>CigarOperator.MATCH_OR_MISMATCH</code>
 */
public final class CigarIterator implements Iterator<CigarOperator> {
    
    private final Iterator<CigarElement> elements;
    private int currentLength;
    private CigarOperator currentOp;
    
    /**
     * Constructs an iterator over a given htsjdk <code>Cigar</code>.
     * 
     * @param cigar - the <code>Cigar</code> to iterate over
     */
    public CigarIterator(Cigar cigar) {
        elements = cigar.iterator();
        currentLength = 0;
    }

    @Override
    public boolean hasNext() {
        return elements.hasNext() || currentLength != 0; 
    }

    @Override
    public CigarOperator next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in CIGAR");
        }
        
        if (currentLength == 0) {
            CigarElement nextElem = elements.next();
            currentLength = nextElem.getLength();
            currentOp = nextElem.getOperator();
        }
        
        currentLength--;
        return currentOp;
    }
}
