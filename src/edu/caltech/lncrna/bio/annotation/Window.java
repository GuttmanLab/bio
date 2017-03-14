package edu.caltech.lncrna.bio.annotation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @param <T>
 */
public class Window<T extends Annotated>
extends Block implements Populated<T>, Scored {

    private Set<T> annotations;
    
    public Window(String ref, int start, int end, Strand strand) {
        super(ref, start, end, strand);
        annotations = new HashSet<>();
    }

    @Override
    public int getPopulationSize() {
        return annotations.size();
    }

    @Override
    public void add(T annotation) {
        annotations.add(annotation);
    }

    @Override
    public Iterator<T> getPopulation() {
        return annotations.iterator();
    }
    
    @Override
    public double getScore() {
        return (double) getPopulationSize();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof Window<?>)) {
            return false;
        }
        
        Window<?> other = (Window<?>) o;
        
        return ref.equals(other.ref) &&
               start == other.start &&
               end == other.end &&
               strand.equals(other.strand) &&
               annotations.equals(other.annotations);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + ref.hashCode();
        hashCode = 37 * hashCode + strand.hashCode();
        hashCode = 37 * hashCode + start;
        hashCode = 37 * hashCode + end;
        hashCode = 37 * hashCode + annotations.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString() {
        return ref + ":" + start + "-" + end + "(" + strand.toString() +
                ") Population: " + getPopulationSize();
    }
}
