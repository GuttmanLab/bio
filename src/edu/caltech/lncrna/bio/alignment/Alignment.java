package edu.caltech.lncrna.bio.alignment;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.sequence.Base;

/**
 * This interface defines the behavior of objects that have been aligned
 * concordantly to a reference.
 */
public interface Alignment extends Annotated, SamRecord {

    /**
     * Whether this alignment is spliced or not.
     * @return <code>true</code> if this alignment is spliced
     */
    public boolean isSpliced();
    
    /**
     * Returns the base on the fragment corresponding to a given reference
     * position.
     * <p>
     * Returns <code>Base.INVALID</code> if this alignment does not overlap
     * the given reference position
     * @param pos - the position relative to the reference
     * @return the base at the corresponding position on this alignment
     */
    public Base getReadBaseFromReferencePosition(int pos);

}