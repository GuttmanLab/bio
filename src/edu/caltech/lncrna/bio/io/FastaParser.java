package edu.caltech.lncrna.bio.io;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

import edu.caltech.lncrna.bio.sequence.FastaSequence;

public final class FastaParser extends TextFileParser<FastaSequence> {

    private String nextName = null;

    public FastaParser(Path p) throws ParseException {
        super(p);
        findFirst();
    }

    private void findFirst() throws ParseException {
        String line = null;
        
        try {
            line = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        if (line == null) {
            nextName = null;
        } else if (!line.startsWith(">")) {
            throw new ParseException("FASTA record found without initial '>'", 0);
        } else {
            nextName = line.substring(1);
        }

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
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        next = new FastaSequence(nextName, sequence.toString());
        nextName = line == null ? null : line.substring(1);
    }
}