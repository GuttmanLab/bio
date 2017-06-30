package edu.caltech.lncrna.bio.alignment;

import java.util.Optional;

import edu.caltech.lncrna.bio.annotation.Annotated;

/**
 * Defines the behavior of objects that have been mapped, successfully or
 * unsuccessfully, to a reference.
 * <p>
 * This interface was originally created to handle unmapped SAM-file reads.
 * Unmapped reads caused problems in previous implementations because reads
 * were required to have interval-like characteristics--a start, an end, and
 * so on. Aligned, interval-like objects which have definitely been mapped to a
 * specific reference location instead implement the <code>Alignment</code>
 * interface.
 * @param <T> - the type of the successful alignment that this object possibly
 * represents; in practical terms, the type of object returned by
 * <code>getAlignment</code> (after unwrapping the <code>Optional</code>)
 */
public interface Aligned<T extends Annotated> extends SamRecord {

    /**
     * Whether this object has a valid alignment.
     * <p>
     * Discordant alignments are considered invalid.
     * @return <code>true</code> if this object has a valid alignment;
     * <code>false</code> otherwise
     */
    public boolean hasAlignment();
    
    /**
     * @return The alignment represented by this object wrapped in an
     * <code>Optional</code> if it exists. If this object is unmapped or has no
     * valid alignment, this method returns an empty <code>Optional</code>.
     */
    public Optional<T> getAlignment();
}