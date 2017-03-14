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
import edu.caltech.lncrna.bio.io.PairedEndBamParser;

public class TestPairedEndBamParser {

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
    
    private final static int BAM1_FRAGMENT_COUNT = 1771742;
    
    @Test
    public void testNonEmptyFileHasNext() {
        try (PairedEndBamParser bp = new PairedEndBamParser(BAM1)) {
            assertThat(bp.hasNext(), is(true));
        }
    }
    
    @Test
    public void testNumberOfBamRecords() {
        try (PairedEndBamParser bp = new PairedEndBamParser(BAM1)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(BAM1_FRAGMENT_COUNT));
        }
    }
    
    @Test
    public void testTypicalPairedEndFragment() {
        try (PairedEndBamParser bp = new PairedEndBamParser(BAM1)) {
            Iterator<PairedEndAlignment> alignments = bp.getAlignmentIterator();

            while (alignments.hasNext()) {
                PairedEndAlignment alignment = alignments.next();
            
                if (alignment.getName().equals(
                        "HISEQ:634:HC2KYBCXY:2:1111:7643:34633")) {
                    
                    assertThat(alignment.getPairOrientation(),
                            is(PairOrientation.F2R1));          
                    assertThat(alignment.getReferenceName(), is("chr1"));
                    assertThat(alignment.getNumberOfBlocks(), is(2));
                    assertThat(alignment.getSpan(), is(493));
                    assertThat(alignment.getStart(), is(3044663));
                    assertThat(alignment.getEnd(), is(3045156));
                    
                    Iterator<Annotated> blocks = alignment.getBlockIterator();
                    Annotated block1 = blocks.next();
                    assertThat(block1.getStart(), is(3044663));
                    assertThat(block1.getEnd(), is(3044763));
                    
                    Annotated block2 = blocks.next();
                    assertThat(block2.getStart(), is(3045041));
                    assertThat(block2.getEnd(), is(3045156));
                    
                    SingleReadAlignment read1 = alignment.getFirstReadInPair();
                    assertThat(read1.getSpan(), is(115));
                    assertThat(read1.getNumberOfBlocks(), is(1));
                    assertThat(read1.getStart(), is(3045041));
                    assertThat(read1.getEnd(), is(3045156));

                    SingleReadAlignment read2 = alignment.getSecondReadInPair();
                    assertThat(read2.getSpan(), is(100));
                    assertThat(read2.getNumberOfBlocks(), is(1));
                    assertThat(read2.getStart(), is(3044663));
                    assertThat(read2.getEnd(), is(3044763));
                    
                    break;
                }
            }
        }
    }
    
    @Test
    public void testDovetailedFragment() {
        try (PairedEndBamParser bp = new PairedEndBamParser(BAM1)) {
            Iterator<PairedEndAlignment> alignments = bp.getAlignmentIterator();

            while (alignments.hasNext()) {
                PairedEndAlignment alignment = alignments.next();
            
                if (alignment.getName().equals(
                        "HISEQ:634:HC2KYBCXY:2:1212:12559:40649")) {
                    
                    assertThat(alignment.getPairOrientation(),
                            is(PairOrientation.F2R1));          
                    assertThat(alignment.getReferenceName(), is("chr1"));
                    assertThat(alignment.getNumberOfBlocks(), is(1));
                    assertThat(alignment.getSpan(), is(112));
                    assertThat(alignment.getStart(), is(3045037));
                    assertThat(alignment.getEnd(), is(3045149));
                    
                    SingleReadAlignment read1 = alignment.getFirstReadInPair();
                    assertThat(read1.getSpan(), is(112));
                    assertThat(read1.getNumberOfBlocks(), is(1));
                    assertThat(read1.getStart(), is(3045037));
                    assertThat(read1.getEnd(), is(3045149));

                    SingleReadAlignment read2 = alignment.getSecondReadInPair();
                    assertThat(read2.getSpan(), is(99));
                    assertThat(read2.getNumberOfBlocks(), is(1));
                    assertThat(read2.getStart(), is(3045037));
                    assertThat(read2.getEnd(), is(3045136));
                    
                    break;
                }
            }
        }
        
    }
    
    @Test
    public void testFragmentWithSpliceJunction() {
        try (PairedEndBamParser bp = new PairedEndBamParser(BAM1)) {
            Iterator<PairedEndAlignment> alignments = bp.getAlignmentIterator();

            while (alignments.hasNext()) {
                PairedEndAlignment alignment = alignments.next();
            
                if (alignment.getName().equals(
                        "HISEQ:634:HC2KYBCXY:2:1209:18108:79498")) {
                    
                    assertThat(alignment.getPairOrientation(),
                            is(PairOrientation.F1R2));
                    
                    assertThat(alignment.getReferenceName(), is("chr1"));
                    
                    SingleReadAlignment read1 = alignment.getFirstReadInPair();
                    assertThat(read1.getSpan(), is(2134));
                    assertThat(read1.getNumberOfBlocks(), is(2));
                    Iterator<Annotated> read1Blocks = read1.getBlockIterator();

                    Annotated block = read1Blocks.next();
                    assertThat(block.getStart(), is(4764554));
                    assertThat(block.getEnd(), is(4764598));
                    
                    block = read1Blocks.next();
                    assertThat(block.getStart(), is(4766617));
                    assertThat(block.getEnd(), is(4766688));
                    
                    SingleReadAlignment read2 = alignment.getSecondReadInPair();
                    assertThat(read2.getSpan(), is(99));
                    assertThat(read2.getNumberOfBlocks(), is(1));
                    assertThat(read2.getStart(), is(4766627));
                    assertThat(read2.getEnd(), is(4766726));
                    
                    assertThat(alignment.getNumberOfBlocks(), is(2));
                    Iterator<Annotated> blocks = alignment.getBlockIterator();
                    
                    block = blocks.next();
                    assertThat(block.getStart(), is(4764554));
                    assertThat(block.getEnd(), is(4764598));
                    
                    block = blocks.next();
                    assertThat(block.getStart(), is(4766617));
                    assertThat(block.getEnd(), is(4766726));
                    
                    break;
                }
            }
        }
    }
}