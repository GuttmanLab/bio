package edu.caltech.lncrna.bio.alignment;

import java.util.Optional;

import edu.caltech.lncrna.bio.annotation.Annotated;

/**
 * This interface defines the behavior of objects that have been mapped,
 * successfully or unsuccessfully, to a reference.
 * <p>
 * This interface was originally created to handle unmapped SAM-file reads.
 * Unmapped reads caused problems in previous implementations because reads
 * were required to have interval-like characteristics--a start, an end, and
 * so on. Aligned, interval-like objects which have definitely been mapped to a
 * specific reference location instead implement the <code>Aligned</code>
 * interface.
 * @param <T> - the type of the successful alignment that this object possibly
 * represents; in practical terms, the type of object returned by
 * <code>getAlignment</code> (after unwrapping the <code>Optional</code>)
 */
public interface Aligned<T extends Annotated> extends SamRecord {

    /**
     * If this has a valid alignment.
     * <p>
     * Returns <code>false</code> if this is unmapped or does not otherwise have a
     * valid alignment. Otherwise, returns <code>true</code>.
     */
    public boolean hasAlignment();
    
    /**
     * Gets the successful alignment represented by this object wrapped in an
     * <code>Optional</code> if it exists.
     * <p>
     * If this object is unmapped or has no valid alignment, this method returns
     * an empty <code>Optional</code>.
     */
    public Optional<T> getAlignment();
}