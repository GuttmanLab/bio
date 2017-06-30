package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.Objects;

import edu.caltech.lncrna.bio.alignment.SamRecord;
import edu.caltech.lncrna.bio.alignment.CoordinateSpace;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;

/**
 * This class represents objects which can write BAM files.
 * <p>
 * A <code>BamParser</code> is {@link AutoCloseable}, and is meant to be used
 * in a try-with-resources block.
 */
public class BamWriter implements AutoCloseable {

    private final SAMFileWriter writer;
    
    public BamWriter(Path p, CoordinateSpace c) {
        Path outputPath = Objects.requireNonNull(p, "Attempted to create "
                + "BamWriter with null Path");
        CoordinateSpace coords = Objects.requireNonNull(c, "Attempted to "
                + "create BamWriter with null CoordinateSpace");
        writer = new SAMFileWriterFactory()
                .makeSAMOrBAMWriter(coords.getSAMFileHeader(), false,
                        outputPath.toFile());
    }
    
    /**
     * Add a {@link SamRecord} to this BAM writer.
     * <p>
     * The added <code>SamRecord</code> will be written in BAM format to the
     * path associated with this writer.
     * 
     * @param record - the <code>SamRecord</code> to write
     */
    public void writeSamRecord(SamRecord record) {
        record.writeTo(writer);
    }

    @Override
    public void close() {
        writer.close();
    }
}