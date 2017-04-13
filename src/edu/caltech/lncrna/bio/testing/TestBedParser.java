package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.annotation.BedFileRecord;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.annotation.BedFileRecord.BedBuilder;
import edu.caltech.lncrna.bio.io.BedParser;

public class TestBedParser {

    private final static Path BED = Paths.get("/Users/masonmlai/Documents/" +
            "Repositories/GuttmanLab/testing/refseq_mm9.bed");
    
    private final static String MALAT1 = "NR_002847";
    
    @Test
    public void testSingleBlockParsing() {
        try (BedParser bp = new BedParser(BED)) {
            while (bp.hasNext()) {
                BedFileRecord bed = bp.next();
                if (bed.getName().equals(MALAT1)) {
                    BedFileRecord cmp = (new BedBuilder())
                            .addAnnotation(new Annotation("chr19", 5795690, 5802672, Strand.NEGATIVE))
                            .addName(MALAT1)
                            .build();
                    System.out.println(bed.getStart());
                    System.out.println(bed.getEnd());
                    System.out.println(bed.getCodingRegion().isPresent());
                    System.out.println(cmp.getCodingRegion().isPresent());
                    assertThat(bed, is(cmp));
                    break;
                }
            }
        }
    }
}