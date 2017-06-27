package edu.caltech.lncrna.bio.sequence;

import java.util.Arrays;
import java.util.Objects;

import edu.caltech.lncrna.bio.alignment.SingleSamRecord;

/**
 * This class represents sequences from a FASTQ file.
 */
public final class FastqSequence extends FastaSequence {

    public static final PhredEncoding DEFAULT_PHRED_ENCODING = PhredEncoding.SANGER;
    private final byte[] quality;
    private static final String nl = System.getProperty("line.separator");
    
    /**
     * Constructs a new instance of a <code>FastqSequence</code> with the 
     * given name, sequence, and Phred quality scores.
     * <p>
     * The quality scores should be provided as an <code>byte</code> array.
     * Typical values will range from 0 to 40.
     * 
     * @param name - the name of the <code>FastqSequence</code>
     * @param seq - the bases of the <code>FastqSequence</code>
     * @param quality - the Phred quality scores of the
     * <code>FastqSequence</code> as a <code>byte[]</code>
     * @throws NullPointerException if any arguments are <code>null</code>
     * @throws IllegalArgumentException if the number of bases and the number
     * of Phred scores do not match
     */
    public FastqSequence(String name, String seq, byte[] quality) {
        super(name, seq);
        Objects.requireNonNull(quality, "Attempted to create a FASTQ " +
                "sequence with a null byte array.");
        if (seq.length() != quality.length) {
            throw new IllegalArgumentException("Sequence bases and sequence "
                    + "quality scores must have the same length.");
        }
        this.quality = quality.clone();
    }
    
    /**
     * Constructs a new instance of a <code>FastqSeuence</code> with the given
     * name, sequence, and Phred quality scores.
     * <p>
     * The quality scores should be provided as the string of characters seen
     * in the FASTQ file. This constructor will convert the string to an array
     * of numeric scores according to the given <code>PhredEncoding</code>.
     * 
     * @param name - the name of the <code>FastqSequence</code>
     * @param seq - the bases of the <code>FastqSequence</code>
     * @param quality - the Phred qualities of the <code>FastqSequence</code>,
     * as a <code>String</code>
     * @param pe - the Phred encoding scheme of this <code>FastqSequence</code>
     * @throws NullPointerException if any arguments are <code>null</code>
     * @throws IllegalArgumentException if the number of bases and the number
     * of Phred scores do not match
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
     * Constructs a new instance of a <code>FastqSequence</code> with the given
     * name, sequence, and Phred quality scores.
     * <p>
     * The quality scores should be provided as the string of characters seen
     * in the FASTQ file. This constructor will convert the string to an array
     * of numeric scores according to the Sanger Phred-encoding scheme.
     * 
     * @param name - the name of the <code>FastqSequence</code>
     * @param seq - the bases of the <code>FastqSequence</code>
     * @param quality - the Phred qualities of the <code>FastqSequence</code>,
     * as a <code>String</code>
     * @throws NullPointerException if any arguments are <code>null</code>
     * @throws IllegalArgumentException if the number of bases and the number
     * of Phred scores do not match
     */
    public FastqSequence(String name, String seq, String quality) {
        this(name, seq, quality, DEFAULT_PHRED_ENCODING);
    }
    
    /**
     * Constructs a new instance of a <code>FastqSequence</code> with the name,
     * sequence, and Phred quality scores of the passed
     * <code>SingleSamRecord</code>.
     * 
     * @param record - the SAM record to copy
     * @throws NullPointerException if the passed <code>SingleSamRecord</code>
     * is <code>null</code>
     */
    public FastqSequence(SingleSamRecord record) {
        this(record.getName(), record.getBases(), record.getQualities());
    }
    
    @Override
    public FastqSequence changeName(String name) {
        Objects.requireNonNull(name, "Attempted to change name of a FASTQ "
                + "record to a null String.");
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
                pe.phredToString(quality) + nl;
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
        
        return Arrays.equals(quality, o.quality) && name.equals(o.name)
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