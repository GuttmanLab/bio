package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hamcrest.core.StringEndsWith;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.io.FastaParser;
import edu.caltech.lncrna.bio.io.MalformedRecordException;

public class TestFastaParser {

    private final static Path FASTA = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.fa");
    private final static Path FASTAGZ = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.fa.gz");
    private final static Path FASTA_ERROR1 = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.malformed-first-header.fa");
    private final static Path FASTA_ERROR2 = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.empty-second-record.fa");
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    ///////////////
    // .fa tests //
    ///////////////
    
    @Test
    public void TestNumberOfFastaRecords() {
        try (FastaParser fp = new FastaParser(FASTA)) {
            long count = fp.stream().count();
            assertThat(count, is(3L));
        }
    }
    
    @Test
    public void TestFastaNames() {
        try (FastaParser fp = new FastaParser(FASTA)) {
            String[] names = {"chr1", "chr2", "chr3"};
            int index = 0;
            while (fp.hasNext()) {
                assertThat(fp.next().getName(), is(names[index]));
                index++;
            }
        }
    }
    
    @Test
    public void TestFastaSequenceLengths() {
        try (FastaParser fp = new FastaParser(FASTA)) {
            int[] lengths = {432, 1299, 391};
            int index = 0;
            while (fp.hasNext()) {
                assertThat(fp.next().length(), is(lengths[index]));
                index++;
            }
        }
    }
    
    //////////////////
    // .fa.gz tests //
    //////////////////
    
    @Test
    public void TestNumberOfFastaGzRecords() {
        try (FastaParser fp = new FastaParser(FASTAGZ)) {
            long count = fp.stream().count();
            assertThat(count, is(3L));
        }
    }
    
    @Test
    public void TestFastaGzNames() {
        try (FastaParser fp = new FastaParser(FASTAGZ)) {
            String[] names = {"chr1", "chr2", "chr3"};
            int index = 0;
            while (fp.hasNext()) {
                assertThat(fp.next().getName(), is(names[index]));
                index++;
            }
        }
    }
    
    @Test
    public void TestFastaGzSequenceLengths() {
        try (FastaParser fp = new FastaParser(FASTAGZ)) {
            int[] lengths = {432, 1299, 391};
            int index = 0;
            while (fp.hasNext()) {
                assertThat(fp.next().length(), is(lengths[index]));
                index++;
            }
        }
    }
    
    @Test
    public void TestParseFastaWithMissingBracket() {
        thrown.expect(MalformedRecordException.class);
        thrown.expectMessage(StringEndsWith.endsWith("Check line number 1."));
        try (FastaParser fp = new FastaParser(FASTA_ERROR1)) {
            while (fp.hasNext()) {
                fp.next();
            }
        }
    }
    
    @Test
    public void TestParseFastaWithEmptyRecord() {
        thrown.expect(MalformedRecordException.class);
        thrown.expectMessage(StringEndsWith.endsWith("Check line number 11."));
        try (FastaParser fp = new FastaParser(FASTA_ERROR2)) {
            while (fp.hasNext()) {
                fp.next();
            }
        }
    }
}