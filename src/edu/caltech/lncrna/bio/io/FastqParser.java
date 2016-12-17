package edu.caltech.lncrna.bio.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import edu.caltech.lncrna.bio.sequence.FastqSequence;
import edu.caltech.lncrna.bio.sequence.PhredEncoding;

public final class FastqParser extends TextFileParser<FastqSequence> {

    private final PhredEncoding pe;
    private final static int NUM_FASTQ_LINES = 4;
    
    public FastqParser(Path p, PhredEncoding pe) throws IOException {
        super(p);
        Objects.requireNonNull(pe, "Attempted to construct a FastqParser " +
                "with a null Phred encoding.");
        this.pe = pe;
        findNext();
    }
    
    public FastqParser(Path p) throws IOException {
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
            next = new FastqSequence(s[0].substring(1), s[1], s[3], pe);
        } else {
            next = null;
            //TODO add logging support here
            System.err.println("FASTQ file " + p.toString() + " has an incomplete final record.");
        }
    }
}