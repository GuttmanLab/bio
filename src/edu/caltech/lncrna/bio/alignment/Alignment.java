package edu.caltech.lncrna.bio.alignment;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.sequence.Base;

/**
 * Defines the behavior of objects that have been aligned concordantly to a
 * reference.
 */
public interface Alignment extends Annotated, SamRecord {

    public boolean isSpliced();
    
    /**
     * @param pos - the position relative to the reference
     * @return The base on the fragment corresponding to a given reference
     * position. Returns <code>Base.INVALID</code> if this alignment does not overlap
     * the given reference position
     */
    public Base getReadBaseFromReferencePosition(int pos);

}