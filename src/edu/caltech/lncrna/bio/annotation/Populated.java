package edu.caltech.lncrna.bio.annotation;

import java.util.Iterator;

/**
 * This interface defines the behavior of {@link Annotated} objects that can be
 * populated by other <code>Annotated</code> objects.
 * <p>
 * An example usage of this interface would be to define windows or tiles to
 * calculate a binned coverage over a BAM file.
 * 
 * @param <T> - the population type
 */
public interface Populated<T extends Annotated> extends Annotated, Scored {
    
    /**
     * Returns the number of annotations in this annotation's population.
     * 
     * @return the number of annotations in this
     */
    public int getPopulationSize();
    
    /**
     * Adds the specified annotation to this annotation's population.
     * 
     * @param annot - the specified annotation
     */
    public void add(T annot);
    
    /**
     * Returns an {@link Iterator} over this annotation's population.
     * 
     * @return an <code>Iterator</code> over the contained annotations
     */
    public Iterator<T> getPopulation();
}
