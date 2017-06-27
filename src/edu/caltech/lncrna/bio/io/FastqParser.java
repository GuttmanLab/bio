package edu.caltech.lncrna.bio.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

import edu.caltech.lncrna.bio.sequence.FastqSequence;
import edu.caltech.lncrna.bio.sequence.PhredEncoding;

/**
 * This class represents objects which can parse FASTQ files.
 * <p>
 * A <code>FastqParser</code> is {@link AutoCloseable}, and is meant to be used
 * in a try-with-resources block. A <code>FastqParser</code> is also an
 * {@link Iterator} over {@link FastqSequence} objects, and is meant to be used
 * as such.
 */
public final class FastqParser extends TextFileParser<FastqSequence> {

    private final PhredEncoding pe;
    private final static int NUM_FASTQ_LINES = 4;
    private int lineNum = 1;

    /**
     * Constructs a <code>FastqParser</code> to parse the the FASTQ file at the
     * specified path.
     * <p>
     * This <code>FastqParser</code> will interpret the Phred scores according
     * to the specified {@link PhredEncoding}.
     * 
     * @param p - the specified path
     * @param pe - the specified Phred encoding
     * @throws NullPointerException if either argument is <code>null</code>.
     */
    public FastqParser(Path p, PhredEncoding pe) {
        super(p);
        Objects.requireNonNull(pe, "Attempted to construct a FastqParser " +
                "with a null Phred encoding.");
        this.pe = pe;
        findNext();
    }

    /**
     * Constructs a <code>FastqParser</code> to parse the the FASTQ file at the
     * specified path.
     * <p>
     * This <code>FastqParser</code> will interpret the Phred quality scores as
     * though they were Sanger-encoded.
     * 
     * @param p - the specified path
     * @throws NullPointerException if the path is <code>null</code>.
     */
    public FastqParser(Path p) {
        this(p, PhredEncoding.SANGER);
    }
    
    protected void findNext() {
        String[] s = new String[NUM_FASTQ_LINES];
        String line = null;
        int i = 0;
        try {
            while (i < NUM_FASTQ_LINES && (line = br.readLine()) != null) {
                s[i] = line;
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (s[3] != null) {
            validateFastqRecord(s);
            next = new FastqSequence(s[0].substring(1), s[1], s[3], pe);
        } else if (s[0] != null) {
            throw new IncompleteFileException(p);
        } else {
            // End of file.
            next = null;
        }
        
        lineNum += NUM_FASTQ_LINES;
    }
    
    private void validateFastqRecord(String[] s) {
        if (!s[0].startsWith("@")) {
            throw new MalformedRecordException("FASTQ file " + p.toString() +
                    " missing an initial \"@\".", lineNum);
        }
        if (!s[2].startsWith("+")) {
            throw new MalformedRecordException("FASTQ file " + p.toString() +
                    " missing an initial \"+\".", lineNum + 2);
        }
    }
}