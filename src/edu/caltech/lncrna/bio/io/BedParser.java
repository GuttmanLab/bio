package edu.caltech.lncrna.bio.io;

import java.io.IOException;
import java.nio.file.Path;

import edu.caltech.lncrna.bio.annotation.BedFileRecord;

public final class BedParser extends TextFileParser<BedFileRecord> {
  
    public BedParser(Path p) throws IOException {
        super(p);
        findNext();
    }

    @Override
    protected void findNext() {
        try {
            String line = br.readLine();
            next = line == null ? null : BedFileRecord.fromFormattedString(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}