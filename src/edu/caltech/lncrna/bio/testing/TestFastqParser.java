package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hamcrest.core.StringEndsWith;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.io.FastqParser;
import edu.caltech.lncrna.bio.io.IncompleteFileException;
import edu.caltech.lncrna.bio.io.MalformedRecordException;
import edu.caltech.lncrna.bio.sequence.Sequence;

public class TestFastqParser {

    private final static Path FASTQ = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.fastq");
    private final static Path FASTQGZ = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.fastq.gz");
    private final static Path FASTQ_INCOMPLETE1 = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.incomplete1.fastq");
    private final static Path FASTQ_INCOMPLETE2 = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.incomplete2.fastq");
    private final static Path FASTQ_INCOMPLETE3 = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.incomplete3.fastq");
    private final static Path FASTQ_MALFORMED1 = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.missing-at-sign.fastq");
    private final static Path FASTQ_MALFORMED2 = Paths.get(
            "/Users/masonmlai/Documents/Repositories/GuttmanLab/testing/" +
            "test.missing-plus-sign.fastq");
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    //////////////////
    // .fastq tests //
    //////////////////
    
    @Test
    public void TestNumberOfFastqRecords() {
        try (FastqParser fp = new FastqParser(FASTQ)) {
            long count = fp.stream().count();
            assertThat(count, is(100L));
        }
    }
    
    @Test
    public void TestThirdFastqRecordName() {
        try (FastqParser fp = new FastqParser(FASTQ)) {
            String name = "HWI-ST767:196:D2HEMACXX:1:1101:9844:1268 1:N:0:CAGATN";
            fp.next();
            fp.next();
            Sequence rec = fp.next();
            assertThat(rec.getName(), is(name));
        }
    }
    
    @Test
    public void TestThirdFastqRecordBases() {
        try (FastqParser fp = new FastqParser(FASTQ)) {
            String bases = "CTTGAACTTCTTTTTTGTCTCCCCTTTGGGAGGGATATAGGTTTTCATTG";
            fp.next();
            fp.next();
            Sequence rec = fp.next();
            assertThat(rec.getBases(), is(bases));
        }
    }
    
    //////////////////
    // .fa.gz tests //
    //////////////////
    
    @Test
    public void TestNumberOfFastqGzRecords() {
        try (FastqParser fp = new FastqParser(FASTQGZ)) {
            long count = fp.stream().count();
            assertThat(count, is(100L));
        }
    }
    
    @Test
    public void TestThirdFastqGzRecordName() {
        try (FastqParser fp = new FastqParser(FASTQGZ)) {
            String name = "HWI-ST767:196:D2HEMACXX:1:1101:9844:1268 1:N:0:CAGATN";
            fp.next();
            fp.next();
            Sequence rec = fp.next();
            assertThat(rec.getName(), is(name));
        }
    }
    
    @Test
    public void TestThirdFastqGzRecordBases() {
        try (FastqParser fp = new FastqParser(FASTQGZ)) {
            String bases = "CTTGAACTTCTTTTTTGTCTCCCCTTTGGGAGGGATATAGGTTTTCATTG";
            fp.next();
            fp.next();
            Sequence rec = fp.next();
            assertThat(rec.getBases(), is(bases));
        }
    }
    
    @Test
    public void TestParseIncompleteFastq1() {
        thrown.expect(IncompleteFileException.class);
        try (FastqParser fp = new FastqParser(FASTQ_INCOMPLETE1)) {
            while (fp.hasNext()) {
                fp.next();
            }
        }
    }
    
    @Test
    public void TestParseIncompleteFastq2() {
        thrown.expect(IncompleteFileException.class);
        try (FastqParser fp = new FastqParser(FASTQ_INCOMPLETE2)) {
            while (fp.hasNext()) {
                fp.next();
            }
        }
    }
    
    @Test
    public void TestParseIncompleteFastq3() {
        thrown.expect(IncompleteFileException.class);
        try (FastqParser fp = new FastqParser(FASTQ_INCOMPLETE3)) {
            while (fp.hasNext()) {
                fp.next();
            }
        }
    }
    
    @Test
    public void TestParseMalformedFastq1() {
        thrown.expect(MalformedRecordException.class);
        thrown.expectMessage(StringEndsWith.endsWith("Check line number 9."));
        try (FastqParser fp = new FastqParser(FASTQ_MALFORMED1)) {
            while (fp.hasNext()) {
                fp.next();
            }
        }        
    }
    
    @Test
    public void TestParseMalformedFastq2() {
        thrown.expect(MalformedRecordException.class);
        thrown.expectMessage(StringEndsWith.endsWith("Check line number 19."));
        try (FastqParser fp = new FastqParser(FASTQ_MALFORMED2)) {
            while (fp.hasNext()) {
                fp.next();
            }
        }        
    }
}