package edu.caltech.lncrna.bio.annotation;

import java.util.Iterator;

/**
 * This interface defines the behavior of annotations that can be populated by
 * other annotations.
 * <p>
 * An example usage of this interface would be to define windows or tiles to
 * calculate a binned coverage over a BAM file.
 * @param <T> - the population type
 */
public interface Populated<T extends Annotated> extends Annotated, Scored {
    
    /**
     * Gets the number of annotations in the population.
     */
    public int getPopulationSize();
    
    /**
     * Adds the given annotation to the population.
     * @param annot - the given annotation
     */
    public void add(T annot);
    
    /**
     * Gets an iterator over the population.
     */
    public Iterator<T> getPopulation();
}
