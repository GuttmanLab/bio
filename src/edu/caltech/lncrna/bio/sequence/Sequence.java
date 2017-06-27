package edu.caltech.lncrna.bio.sequence;

import edu.caltech.lncrna.bio.io.Formattable;

/**
 * This interface defines the behavior of a nucleotide sequence.
 * <p>
 * All objects of classes which implement this interface
 * <ul>
 * <li>be named
 * <li>be associated with a string of nucleotide bases
 */
public interface Sequence extends Formattable {

    /**
     * Gets the bases of this as a <code>String</code>.
     */
    public String getBases();
    
    /**
     * Gets the name of this.
     */
    public String getName();
    
    default int length() {
        return getBases().length();
    }
    
    public Sequence changeName(String s);
    
    public Sequence complement();
    
    public Sequence complement(String s);
    
    public Sequence reverseComplement();
    
    public Sequence reverseComplement(String s);
    
    /**
     * If this sequence is a poly-A sequence.
     * <p>
     * A sequence is a poly-A sequence if all of its bases are 'A', or if all
     * of its bases are 'T'. The check is not case-sensitive.
     */
    default boolean isPolyA() {
        return getBases().chars().allMatch(c -> c == 'a' || c == 'A') ||
               getBases().chars().allMatch(c -> c == 't' || c == 'T');
    }

    default String toFormattedString() {
        return toFasta();
    }
    
    default String toFasta() {
        return ">" + getName() + System.getProperty("line.separator") +
                getBases() + System.getProperty("line.separator");
    }
}