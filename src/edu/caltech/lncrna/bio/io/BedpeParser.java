package edu.caltech.lncrna.bio.io;

import java.io.IOException;
import java.nio.file.Path;

import edu.caltech.lncrna.bio.annotation.BedpeFileRecord;

public final class BedpeParser extends TextFileParser<BedpeFileRecord> {
    
    public BedpeParser(Path p) throws IOException {
        super(p);
        findNext();
    }

    @Override
    protected void findNext() {
        try {
            String line = br.readLine();
            if (line == null) {
                next = null;
            } else if (line.startsWith("#")) {
                findNext();
            } else {
                next = BedpeFileRecord.fromFormattedString(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}