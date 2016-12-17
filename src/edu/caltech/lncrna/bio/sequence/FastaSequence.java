package edu.caltech.lncrna.bio.sequence;

import java.util.Objects;

import edu.caltech.lncrna.bio.annotation.AnnotationFileRecord;

/**
 * This class represents sequences from a FASTA file.
 */
public class FastaSequence implements Sequence, AnnotationFileRecord {

    protected final String sequence;
    protected final String name;
    protected static final String nl = System.getProperty("line.separator");
    
    /**
     * Constructs an instance of a <code>FastaSequence</code> from a name and a string of bases.
     * @param name - the name of this <code>FastaSequence</code>
     * @param seq - the bases of this <code>FastaSequence</code>
     * @throws NullPointerException if either argument is null.
     */
    public FastaSequence(String name, String seq) {
        this.name = Objects.requireNonNull(name,
                "Attempted to construct FASTA sequence with null name");
        this.sequence = Objects.requireNonNull(seq,
                "Attempted to construct FASTA sequence with null sequence string");
    }
    
    @Override
    public String getBases() {
        return sequence;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Returns a <code>FastaSequence</code> with the same sequence but a new name.
     * <p>
     * This method does not mutate any member variables, but instead constructs a
     * new <code>FastaSequence</code>.
     * @param name - the new name
     * @return a new <code>FastaSequence</code> with the given name
     */
    public FastaSequence changeName(String name) {
        return new FastaSequence(name, sequence);
    }
    
    public FastaSequence complement() {
        return complement(name);
    }
    
    public FastaSequence complement(String name) {
        return new FastaSequence(name, Sequences.complement(sequence));
    }
    
    public FastaSequence reverseComplement() {
        return reverseComplement(name);
    }
    
    public FastaSequence reverseComplement(String name) {
        return new FastaSequence(name, Sequences.reverseComplement(sequence));
    }
    
    public FastaSequence subsequence(int start, int end) {
        return subsequence(name, start, end);
    }
    
    public FastaSequence subsequence(String name, int start, int end) {
        String subseq = sequence.substring(Math.max(start, 0),
                        Math.min(end, sequence.length()));
        return new FastaSequence(name, subseq);
    }
    
    public int length() {
        return sequence.length();
    }
    
    /**
     * If this sequence is a poly-A sequence.
     * <p>
     * A sequence is a poly-A sequence if all of its bases are 'A', or if all
     * of its bases are 'T'. The check is not case-sensitive.
     */
    public boolean isPolyA() {
        return sequence.chars().allMatch(c -> c == 'a' || c == 'A') ||
               sequence.chars().allMatch(c -> c == 't' || c == 'T');
    }
    
    public String toFasta() {
        return ">" + name + nl + sequence;
    }
    
    @Override
    public String toFormattedString() {
        return toFasta();
    }
    
    @Override
    public String toString() {
        return name + ": " + sequence;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof FastaSequence)) {
            return false;
        }
        
        FastaSequence other = (FastaSequence) o;
        
        return name.equals(other.name) && sequence.equals(other.sequence);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + name.hashCode();
        hashCode = 37 * hashCode + sequence.hashCode();
        return hashCode;
    }
}