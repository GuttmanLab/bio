package edu.caltech.lncrna.bio.annotation;

/**
 * This interface defines the behavior of an annotation that has a score.
 */
public interface Scored extends Annotated {
    
    /**
     * Returns the score of this annotation.
     * 
     * @return the score
     */
    public double getScore();    
}
