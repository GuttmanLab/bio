package edu.caltech.lncrna.bio.annotation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Window<T extends Annotated>
extends Annotation implements Populated<T>, Scored {

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
        
        return super.equals(other) &&
               annotations.equals(other.annotations);
    }
    
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 37 * hashCode + annotations.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString() {
        return ref + ":" + getStart() + "-" + getEnd() + "(" + strand.toString() +
                ") Population: " + getPopulationSize();
    }
}
