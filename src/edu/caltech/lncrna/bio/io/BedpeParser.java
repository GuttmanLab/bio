package edu.caltech.lncrna.bio.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

import edu.caltech.lncrna.bio.annotation.BedpeFileRecord;

/**
 * This class represents objects which can parse BEDPE files.
 * <p>
 * A <code>BedpeParser</code> is {@link AutoCloseable}, and is meant to be used
 * in a try-with-resources block. A <code>BedpeParser</code> is also an
 * {@link Iterator} over {@link BedpeFileRecord} objects, and is meant to be
 * used as such.
 */
public final class BedpeParser extends TextFileParser<BedpeFileRecord> {

    /**
     * Constructs a <code>BedpeParser</code> to parse the the BED file at the
     * specified path.
     * 
     * @param p - the specified path
     * @throws NullPointerException if the path is <code>null</code>.
     */
    public BedpeParser(Path p) {
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