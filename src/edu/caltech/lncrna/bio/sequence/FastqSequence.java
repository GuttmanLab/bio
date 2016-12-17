package edu.caltech.lncrna.bio.sequence;

import java.util.Objects;

import edu.caltech.lncrna.bio.alignment.SamRecord;

/**
 * This class represents sequences from a FASTQ file.
 */
public final class FastqSequence extends FastaSequence {

    public static final PhredEncoding DEFAULT_PHRED_ENCODING = PhredEncoding.SANGER;
    private final byte[] quality;
    
    public FastqSequence(String name, String seq, byte[] quality) {
        super(name, seq);
        this.quality = quality.clone();
    }
    
    /**
     * Constructs a new instance of a FastqSeuence with the given name, sequence,
     * and Phred quality scores.
     * <p>
     * The quality scores should be provided as the string of characters seen
     * in the FASTQ file. This constructor will convert the string to an array
     * of numeric scores according to the given <code>PhredEncoding</code>. 
     * @param name - the name of the <code>FastqSequence</code>
     * @param seq - the bases of the <code>FastqSequence</code>
     * @param quality - the Phred qualities of the <code>FastqSequence</code>,
     * as a <code>String</code>
     * @param pe - the Phred encoding scheme of this <code>FastqSequence</code>
     * @throws NullPointerException if any arguments are null
     * @throws IllegalArgumentException if the number of bases and the number of
     * Phred scores do not match
     */
    public FastqSequence(String name, String seq, String quality,
            PhredEncoding pe) {
        super(name, seq);
        Objects.requireNonNull(pe,
                "Attempted to create FASTQ sequence with null Phred encoding");
        this.quality = pe.stringToPhred(Objects.requireNonNull(quality,
                "Attempted to create FASTQ sequence with null quality string"));
        
        if (seq.length() != quality.length()) {
            throw new IllegalArgumentException("Sequence bases " +
                    seq + " and sequence quality scores " +
                    quality + " must have same length.");
        }
    }

    /**
     * Constructs a new instance of a FastqSeuence with the given name, sequence,
     * and Phred quality scores.
     * <p>
     * The quality scores should be provided as the string of characters seen
     * in the FASTQ file. This constructor will convert the string to an array
     * of numeric scores according to the Sanger Phred-encoding scheme. 
     * @param name - the name of the <code>FastqSequence</code>
     * @param seq - the bases of the <code>FastqSequence</code>
     * @param quality - the Phred qualities of the <code>FastqSequence</code>,
     * as a <code>String</code>
     * @throws NullPointerException if any arguments are null
     * @throws IllegalArgumentException if the number of bases and the number of
     * Phred scores do not match
     */
    public FastqSequence(String name, String seq, String quality) {
        this(name, seq, quality, DEFAULT_PHRED_ENCODING);
    }
    
    public FastqSequence(SamRecord record) {
        this(record.getName(), record.getBases(), record.getQualities());
    }
    
    /**
     * Returns a <code>FastqSequence</code> with the same sequence and qualities,
     * but a new name.
     * <p>
     * This method does not mutate any member variables, but instead constructs a
     * new <code>FastqSequence</code>.
     * @param name - the new name
     * @return a new <code>FastqSequence</code> with the given name
     */
    @Override
    public FastqSequence changeName(String name) {
        return new FastqSequence(name, sequence, quality);
    }
    
    @Override
    public FastqSequence complement() {
        return complement(name);
    }
    
    @Override
    public FastqSequence complement(String name) {
        return new FastqSequence(name, Sequences.complement(sequence), quality);
    }
    
    @Override
    public FastqSequence reverseComplement() {
        return reverseComplement(name);
    }
    
    @Override
    public FastqSequence reverseComplement(String name) {
        byte[] reverseQuality = new byte[quality.length];
        for (int i = 0; i < quality.length; i++) {
            reverseQuality[i] = quality[quality.length - i - 1];
        }
        
        return new FastqSequence(name, Sequences.reverseComplement(sequence),
                reverseQuality);
    }
    
    @Override
    public FastqSequence subsequence(int start, int end) {
        return subsequence(name, start, end);
    }
    
    @Override
    public FastqSequence subsequence(String name, int start, int end) {
        String subseq = sequence.substring(start, end);
        byte[] subqual = new byte[end - start];
        for (int i = start; i < end; i++) {
            subqual[i - start] = quality[i];
        }
        return new FastqSequence(name, subseq, subqual);
    }
    
    @Override
    public String toFormattedString() {
        return toFormattedString(DEFAULT_PHRED_ENCODING);
    }
    
    public String toFormattedString(PhredEncoding pe) {
        return "@" + name + nl + sequence + nl + "+" + nl +
                pe.phredToString(quality);
    }
    
    public boolean hasAnyBaseWithQualityLessThan(byte minimumAllowedQuality) {
        for (int i = 0; i < quality.length; i++) {
            if (quality[i] < minimumAllowedQuality) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        
        if (!(other instanceof FastqSequence)) {
            return false;
        }

        FastqSequence o = (FastqSequence) other;
        
        return quality.equals(o.quality) && name.equals(o.name)
                && sequence.equals(o.sequence);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + name.hashCode();
        hashCode = 37 * hashCode + sequence.hashCode();
        hashCode = 37 * hashCode + quality.hashCode();
        return hashCode;
    }
}