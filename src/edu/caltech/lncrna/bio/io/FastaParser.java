package edu.caltech.lncrna.bio.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

import edu.caltech.lncrna.bio.sequence.FastaSequence;

/**
 * This class represents objects which can parse FASTA files.
 * <p>
 * A <code>FastaParser</code> is {@link AutoCloseable}, and is meant to be used
 * in a try-with-resources block. A <code>FastaParser</code> is also an
 * {@link Iterator} over {@link FastaSequence} objects, and is meant to be used
 * as such.
 */
public final class FastaParser extends TextFileParser<FastaSequence> {

    private String nextName = null;
    private int lineNum = 1;

    /**
     * Constructs a <code>FastaParser</code> to parse the the FASTA file at the
     * specified path.
     * 
     * @param p - the specified path
     * @throws NullPointerException if the path is <code>null</code>.
     */
    public FastaParser(Path p) {
        super(p);
        findFirst();
    }

    private void findFirst() {
        String line = null;
        
        try {
            line = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        if (line == null) {
            nextName = null;
        } else if (!line.startsWith(">")) {
            throw new MalformedRecordException("FASTA record found without initial '>'.", lineNum);
        } else {
            nextName = line.substring(1);
        }
        
        lineNum++;
        findNext();
    }
    
    @Override
    protected void findNext() {
        if (nextName == null) {
            next = null;
            return;
        }
        
        StringBuilder sequence = new StringBuilder();
        String line = null;

        try {
            while ((line = br.readLine()) != null && !line.startsWith(">")) {
                sequence.append(line);
                lineNum++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (sequence.length() == 0) {
            throw new MalformedRecordException("Encountered an empty FASTA " +
                    "record.", lineNum);
        }

        next = new FastaSequence(nextName, sequence.toString());
        nextName = line == null ? null : line.substring(1);
    }
}