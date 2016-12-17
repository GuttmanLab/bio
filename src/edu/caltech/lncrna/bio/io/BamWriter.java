package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.Objects;

import edu.caltech.lncrna.bio.alignment.PairedSamRecord;
import edu.caltech.lncrna.bio.alignment.SamRecord;
import edu.caltech.lncrna.bio.alignment.CoordinateSpace;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;

public class BamWriter implements AutoCloseable {

    private final SAMFileWriter writer;
    
    public BamWriter(Path p, CoordinateSpace c) {
        Path outputPath = Objects.requireNonNull(p, "Attempted to create "
                + "BamWriter with null Path");
        CoordinateSpace coords = Objects.requireNonNull(c, "Attempted to create "
                + "BamWriter with null CoordinateSpace");
        writer = new SAMFileWriterFactory()
                .makeSAMOrBAMWriter(coords.getSAMFileHeader(), false, outputPath.toFile());
    }
    
    public void addAlignment(SamRecord record) {
        record.writeTo(writer);
    }
    
    public void addAlignment(PairedSamRecord record) {
        record.writeTo(writer);
    }

    @Override
    public void close() {
        writer.close();
    }
}