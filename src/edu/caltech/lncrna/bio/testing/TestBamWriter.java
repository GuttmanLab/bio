package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.Test;

import edu.caltech.lncrna.bio.alignment.Aligned;
import edu.caltech.lncrna.bio.alignment.Alignment;
import edu.caltech.lncrna.bio.alignment.CoordinateSpace;
import edu.caltech.lncrna.bio.io.BamParser;
import edu.caltech.lncrna.bio.io.BamWriter;

public class TestBamWriter {
    
    private final static Path BAM = Paths.get("/Users/masonmlai/Documents/" +
            "Repositories/GuttmanLab/testing/paired_end_with_splice.bam");
    
    @Test
    public void testBamWriterWriteAll() throws IOException {
        
        CoordinateSpace cs = new CoordinateSpace(BAM);
        File tmp = File.createTempFile("tmp", "bam");
        tmp.deleteOnExit();
        
        try (BamParser<? extends Aligned<? extends Alignment>> bp =
                BamParser.newInstance(BAM);
             BamWriter bw = new BamWriter(tmp.toPath(), cs)) {
            
            bp.getAlignmentStream().forEach(bw::writeSamRecord);
        }
        
        try (BamParser<? extends Aligned<? extends Alignment>> bp1 =
                BamParser.newInstance(BAM);
             BamParser<? extends Aligned<? extends Alignment>> bp2 =
                BamParser.newInstance(tmp.toPath())) {
            
            long count1 = bp1.getAlignmentStream().count();
            long count2 = bp2.getAlignmentStream().count();
            
            assertThat(count1, is(count2));
        }
    }
    
    @Test
    public void testBamWriterFirstRecord() throws IOException {
        
        CoordinateSpace cs = new CoordinateSpace(BAM);
        File tmp = File.createTempFile("tmp", "bam");
        tmp.deleteOnExit();
        
        Alignment firstRecord = null;
        
        try (BamParser<? extends Aligned<? extends Alignment>> bp =
                BamParser.newInstance(BAM);
             BamWriter bw = new BamWriter(tmp.toPath(), cs)) {
            
            Iterator<? extends Alignment> aligned = bp.getAlignmentIterator();
            firstRecord = aligned.next();
            bw.writeSamRecord(firstRecord);
        }
        
        try (BamParser<? extends Aligned<? extends Alignment>> bp =
                BamParser.newInstance(tmp.toPath())) {
            
            Iterator<? extends Alignment> aligned = bp.getAlignmentIterator();
            assertThat(aligned.next(), is(firstRecord));
        }
    }
}
