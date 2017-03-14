package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Test;

import edu.caltech.lncrna.bio.io.FastaParser;

public class TestFastaParser {

    private final static Path TEST_FASTA = Paths.get("/Users/mason/Documents/workspace/bio/src/testing/test.fa");

    @Test
    public void TestNumberOfFastaRecords() throws ParseException {
        try (FastaParser fp = new FastaParser(TEST_FASTA)) {
            int count = fp.stream().mapToInt(e -> 1).sum();
            assertThat(count, is(3));
        }
    }
    
    @Test
    public void TestFastaNames() throws ParseException {
        try (FastaParser fp = new FastaParser(TEST_FASTA)) {
            String[] names = {"chr1", "chr2", "chr3"};
            int index = 0;
            while (fp.hasNext()) {
                assertThat(fp.next().getName(), is(names[index]));
                index++;
            }
        }
    }
    
    @Test
    public void TestFastaSequenceLengths() throws ParseException {
        try (FastaParser fp = new FastaParser(TEST_FASTA)) {
            int[] lengths = {1000, 50, 1000};
            int index = 0;
            while (fp.hasNext()) {
                assertThat(fp.next().length(), is(lengths[index]));
                index++;
            }
        }
    }
}