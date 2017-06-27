package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import edu.caltech.lncrna.bio.alignment.SingleRead;
import edu.caltech.lncrna.bio.io.SingleReadBamParser;

public class TestSingleRead {
    private final static Path BAM1 = Paths.get("/Users/masonmlai/Documents/" +
            "Repositories/GuttmanLab/testing/paired_end_with_splice.bam");
    
    @Test
    public void testGetName() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            SingleRead read = bp.next();
            assertThat(read.getName(), is("HISEQ:634:HC2KYBCXY:2:1111:7643:34633"));
        }
    }
    
    @Test
    public void testGetBases() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            SingleRead read = bp.next();
            assertThat(read.getBases(), is("AACAGTTTCAAGGGCTCCTAGATACCAGGGCA" +
                    "GATGCAACAGTAATTTCCTCAACACACTGGCCTGCAGCCTGGCCCTTACAGCCCA" + 
                    "CTAAGGGATGGTG"));
        }        
    }
    
    @Test
    public void testLength() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            SingleRead read = bp.next();
            assertThat(read.length(), is(100));
        }           
    }
    
    @Test
    public void testGetMapq() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            SingleRead read = bp.next();
            assertThat(read.getMappingQuality(), is(255));
        }        
    }
    
    @Test
    public void testGetMdtag() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            SingleRead read = bp.next();
            assertThat(read.getMdTag().isPresent(), is(false));
        }        
    }
    
    @Test
    public void testGetCigar() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            SingleRead read = bp.next();
            assertThat(read.getCigarString(), is("100M"));
            assertThat(read.getCigar().toString(), is(read.getCigarString()));
        }           
    }
    
    @Test
    public void testReadFlags() {
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            SingleRead read = bp.next();
            assertThat(read.isDuplicate(), is(false));
            assertThat(read.isFirstInPair(), is(false));
            assertThat(read.isMapped(), is(true));
            assertThat(read.isMappedInProperPair(), is(true));
            assertThat(read.isOnReverseStrand(), is(false));
            assertThat(read.isPaired(), is(true));
            assertThat(read.isPrimaryAlignment(), is(true));
            assertThat(read.isSecondInPair(), is(true));
            assertThat(read.isSupplementaryAlignment(), is(false));
            assertThat(read.hasMappedMate(), is(true));
            assertThat(read.hasMateOnReverseStrand(), is(true));
        }
    }
    
    @Test
    public void testEquals() {
        SingleRead firstRead1 = null;
        SingleRead firstRead2 = null;
        SingleRead secondRead = null;

        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            firstRead1 = bp.next();
            secondRead = bp.next();
        }
        
        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            firstRead2 = bp.next();
        }
        
        assertThat(firstRead1, is(firstRead1));
        assertThat(firstRead1.equals(null), is(false));
        assertThat(firstRead1, is(firstRead2));
        assertThat(firstRead1.equals(secondRead), is(false));
    }
}
