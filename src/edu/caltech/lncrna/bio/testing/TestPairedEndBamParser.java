package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.io.PairedEndBamParser;

public class TestPairedEndBamParser {

    private final static Path TEST_BAM = Paths.get("/Users/mason/Documents/workspace/bio/src/testing/test_normal_paired_alignments.bam");
    private final static int TOTAL_FRAGMENT_COUNT = 1880;
    private final static int CHR1_FRAGMENT_COUNT = 940;
    private final static int CHR2_FRAGMENT_COUNT = 0;
    private final static int CHR3_FRAGMENT_COUNT = 940;
    private final static int CHR1_LENGTH = 1000;
    private final static int CHR2_LENGTH = 50;
    private final static int CHR3_LENGTH = 1000;
    
    
    @Test
    public void testNonEmptyFileHasNext() {
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM)) {
            assertThat(bp.hasNext(), is(true));
        }
    }
    
    @Test
    public void testNumberOfBamRecords() {
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(TOTAL_FRAGMENT_COUNT));
        }
    }
    
    @Test
    public void testNumberOfChromosomeOverlappersBothStrand() throws IOException {
        Block b = new Block("chr1", 0, CHR1_LENGTH, Strand.BOTH);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR1_FRAGMENT_COUNT));
        }
        b = new Block("chr2", 0, CHR2_LENGTH, Strand.BOTH);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR2_FRAGMENT_COUNT));
        }
        b = new Block("chr3", 0, CHR3_LENGTH, Strand.BOTH);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR3_FRAGMENT_COUNT));
        }
    }
    
    @Test
    public void testNumberOfChromosomeOverlappersPositiveStrand() throws IOException {
        Block b = new Block("chr1", 0, CHR1_LENGTH, Strand.POSITIVE);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR1_FRAGMENT_COUNT));
        }
        b = new Block("chr2", 0, CHR2_LENGTH, Strand.POSITIVE);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR2_FRAGMENT_COUNT));
        }
        b = new Block("chr3", 0, CHR3_LENGTH, Strand.POSITIVE);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR3_FRAGMENT_COUNT));
        }
    }
    
    @Test
    public void testNumberOfChromosomeOverlappersNegativeStrand() throws IOException {
        Block b = new Block("chr1", 0, CHR1_LENGTH, Strand.NEGATIVE);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR1_FRAGMENT_COUNT));
        }
        b = new Block("chr2", 0, CHR2_LENGTH, Strand.NEGATIVE);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR2_FRAGMENT_COUNT));
        }
        b = new Block("chr3", 0, CHR3_LENGTH, Strand.NEGATIVE);
        try (PairedEndBamParser bp = new PairedEndBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR3_FRAGMENT_COUNT));
        }
    }
}