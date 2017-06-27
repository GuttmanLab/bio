package edu.caltech.lncrna.bio.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

import edu.caltech.lncrna.bio.annotation.BedFileRecord;

/**
 * This class represents objects which can parse BED files.
 * <p>
 * A <code>BedParser</code> is {@link AutoCloseable}, and is meant to be used
 * in a try-with-resources block. A <code>BedParser</code> is also an
 * {@link Iterator} over {@link BedFileRecord} objects, and is meant to be used
 * as such.
 */
public final class BedParser extends TextFileParser<BedFileRecord> {

    /**
     * Constructs a <code>BedParser</code> to parse the the BED file at the
     * specified path.
     * 
     * @param p - the specified path
     * @throws NullPointerException if the path is <code>null</code>.
     */
    public BedParser(Path p) {
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