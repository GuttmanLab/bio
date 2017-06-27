package edu.caltech.lncrna.bio.sequence;

import java.util.Objects;

/**
 * This class represents sequences from a FASTA file.
 */
public class FastaSequence implements Sequence {

    protected final String sequence;
    protected final String name;
    
    /**
     * Constructs an instance of a <code>FastaSequence</code> from a name and a
     * string of bases.
     * 
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

    @Override
    public FastaSequence changeName(String name) {
        return new FastaSequence(name, sequence);
    }
    
    @Override
    public FastaSequence complement() {
        return complement(name);
    }
    
    @Override
    public FastaSequence complement(String name) {
        return new FastaSequence(name, Sequences.complement(sequence));
    }
    
    @Override
    public FastaSequence reverseComplement() {
        return reverseComplement(name);
    }
    
    @Override
    public FastaSequence reverseComplement(String name) {
        return new FastaSequence(name, Sequences.reverseComplement(sequence));
    }
    
    /**
     * Returns a subsequence of this with the given start and end.
     * <p>
     * Intervals are half-open. The base at the start coordinate will be
     * included. The base at the end coordinate will not.
     * 
     * @param start - the start coordinate
     * @param end - the end coordinate
     * @return a subsequence from <code>start</code> to <code>end</code>
     */
    public FastaSequence subsequence(int start, int end) {
        return subsequence(name, start, end);
    }
    
    public FastaSequence subsequence(String name, int start, int end) {
        String subseq = sequence.substring(Math.max(start, 0),
                        Math.min(end, sequence.length()));
        return new FastaSequence(name, subseq);
    }

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