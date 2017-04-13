package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.Test;

import edu.caltech.lncrna.bio.alignment.PairOrientation;
import edu.caltech.lncrna.bio.alignment.PairedEndAlignment;
import edu.caltech.lncrna.bio.alignment.SingleReadAlignment;
import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.io.PairedEndBamParser;
import edu.caltech.lncrna.bio.io.SingleReadBamParser;

public class TestSingleReadBamParser {

    /**
     * Paired-end BAM file, aligned with a splice-tolerant aligner (STAR).
     * <p>
     * Only contains fragments from chromosomes 1 and 2. All unmapped and
     * discordant pairs have been removed. The samtools flagstat output:
     * <li>3543484 (100%) mapped
     * <li>3543484 paired in sequencing
     * <li>1771742 read1
     * <li>1771742 read2
     */
    private final static Path BAM1 = Paths.get("/Users/masonmlai/Documents/" +
            "Repositories/GuttmanLab/testing/paired_end_with_splice.bam");
    
    private final static int BAM1_FRAGMENT_COUNT = 3543484;
    
    @Test
    public void testNonEmptyFileHasNext() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            assertThat(bp.hasNext(), is(true));
        }
    }
    
    @Test
    public void testNumberOfBamRecords() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(BAM1_FRAGMENT_COUNT));
        }
    }
    
    @Test
    public void testStrandOrientation() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            Iterator<SingleReadAlignment> alignments = bp.getAlignmentIterator();
            while (alignments.hasNext()) {
                SingleReadAlignment alignment = alignments.next();
                
                if (alignment.isOnReverseStrand()) {
                    assertThat(alignment.getStrand(), is(Strand.NEGATIVE));
                } else {
                    assertThat(alignment.getStrand(), is(Strand.POSITIVE));
                }
            }
        }
    }
}