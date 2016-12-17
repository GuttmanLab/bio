package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.io.SingleReadBamParser;

public class TestSingleReadBamParser {

    private final static Path TEST_BAM = Paths.get("/Users/mason/Documents/workspace/bio/src/testing/test_single.bam");
    private final static int TOTAL_READ_COUNT = 3923;
    private final static int CHR1_READ_COUNT = 1940;
    private final static int CHR2_READ_COUNT = 40;
    private final static int CHR3_READ_COUNT = 1940;
    private final static int CHR1_LENGTH = 1000;
    private final static int CHR2_LENGTH = 50;
    private final static int CHR3_LENGTH = 1000;
    
    
    @Test
    public void testNonEmptyFileHasNext() {
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM)) {
            assertThat(bp.hasNext(), is(true));
        }
    }
    
    @Test
    public void testNumberOfBamRecords() {
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(TOTAL_READ_COUNT));
        }
    }
    
    @Test
    public void testNumberOfChromosomeOverlappersBothStrand() {
        Block b = new Block("chr1", 0, CHR1_LENGTH, Strand.BOTH);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR1_READ_COUNT));
        }
        b = new Block("chr2", 0, CHR2_LENGTH, Strand.BOTH);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR2_READ_COUNT));
        }
        b = new Block("chr3", 0, CHR3_LENGTH, Strand.BOTH);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR3_READ_COUNT));
        }
    }
    
    @Test
    public void testNumberOfChromosomeOverlappersPositiveStrand() {
        Block b = new Block("chr1", 0, CHR1_LENGTH, Strand.POSITIVE);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR1_READ_COUNT / 2));
        }
        b = new Block("chr2", 0, CHR2_LENGTH, Strand.POSITIVE);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR2_READ_COUNT / 2));
        }
        b = new Block("chr3", 0, CHR3_LENGTH, Strand.POSITIVE);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR3_READ_COUNT / 2));
        }
    }
    
    @Test
    public void testNumberOfChromosomeOverlappersNegativeStrand() {
        Block b = new Block("chr1", 0, CHR1_LENGTH, Strand.NEGATIVE);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR1_READ_COUNT / 2));
        }
        b = new Block("chr2", 0, CHR2_LENGTH, Strand.NEGATIVE);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR2_READ_COUNT / 2));
        }
        b = new Block("chr3", 0, CHR3_LENGTH, Strand.NEGATIVE);
        try (SingleReadBamParser bp = new SingleReadBamParser(TEST_BAM, b)) {
            int count = bp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(CHR3_READ_COUNT / 2));
        }
    }
}