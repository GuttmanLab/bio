package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import edu.caltech.lncrna.bio.alignment.Aligned;
import edu.caltech.lncrna.bio.alignment.Alignment;
import edu.caltech.lncrna.bio.io.BamParser;
import edu.caltech.lncrna.bio.io.BedParser;

public class TestPairedEndReadAlignment {

    public static final Path SINGLE_FRAGMENT_BAM = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "single_read_contained_by_xist.bam");
    
    public static final Path REFSEQ_BED = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "refseq_mm9.bed");
    
    public static final String REFSEQ_XIST1 = "NR_001463";
    public static final String REFSEQ_XIST2 = "NR_001570";
    
    @Test
    public void testFragmentContainedByXistOnly() {
        Alignment align;
        try (BamParser<? extends Aligned<? extends Alignment>> bams = 
                BamParser.newInstance(SINGLE_FRAGMENT_BAM)) {
            align = bams.next().getAlignment().get();
        }
        
        try (BedParser beds = new BedParser(REFSEQ_BED)) {
        beds.stream()
            .forEach(x -> {
                if (x.getName().equals(REFSEQ_XIST1) ||
                    x.getName().equals(REFSEQ_XIST2)) {
                    assertThat(x.contains(align), is(true));
                } else {
                    assertThat(x.contains(align), is(false));
                }
            });
        
        }
    }
}
