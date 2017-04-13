package edu.caltech.lncrna.bio.alignment;

import java.util.Iterator;
import java.util.NoSuchElementException;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;

public final class CigarIterator implements Iterator<CigarOperator> {
    
    private final Iterator<CigarElement> elements;
    private int currentLength;
    private CigarOperator currentOp;
    
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
