package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.annotation.BedFileRecord;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.io.BedParser;

public class TestBedParser {

    private final static Path BED = Paths.get("/Users/masonmlai/Documents/" +
            "Repositories/GuttmanLab/testing/refseq_mm9.bed");
    private final static long NUM_RECORDS = 29567;
    
    private final static String MALAT1 = "NR_002847";
    
    @Test
    public void testNumRecords() {
        try (BedParser bp = new BedParser(BED)) {
            assertThat(bp.stream().count(), is(NUM_RECORDS));
        }
    }
    
    @Test
    public void testSingleBlockParsing() {
        try (BedParser bp = new BedParser(BED)) {
            while (bp.hasNext()) {
                BedFileRecord bed = bp.next();
                if (bed.getName().equals(MALAT1)) {
                    BedFileRecord cmp = BedFileRecord.builder()
                            .addAnnotation(new Annotation("chr19", 5795690, 5802672, Strand.NEGATIVE))
                            .addName(MALAT1)
                            .build();
                    assertThat(bed, is(cmp));
                    break;
                }
            }
        }
    }
}