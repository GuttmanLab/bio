package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import edu.caltech.lncrna.bio.alignment.SingleReadAlignment;
import edu.caltech.lncrna.bio.annotation.Populated;
import edu.caltech.lncrna.bio.annotation.WindowIterator;
import edu.caltech.lncrna.bio.io.SingleReadBamParser;

public class TestWindowIterator {
    
    private static Path TEST_BAM = 
            Paths.get("/Users/masonmlai/Documents/Repositories/GuttmanLab/" +
            "testing/wgEncodeUwRepliSeqBg02esG1bAlnRep1.bam");
    
    @Test
    public void testInitialGroupOfReadsOnChr1() {
        
        // First cluster of reads starts just after chr1:10140
        // and ends just after chr1:10190
        
        int numWindows = 11;
        int windowSize = 10;
        int stepSize = 5;
        int groupStart = 10140;
        
        int[] starts = new int[numWindows];
        for (int i = 0; i < numWindows; i++) {
            starts[i] = groupStart + stepSize * i;
        }
        
        int[] ends = new int[numWindows];
        for (int i = 0; i < numWindows; i++) {
            ends[i] = starts[i] + windowSize;
        }
        
        int[] counts = {3, 6, 9, 9, 9, 9, 9, 9, 9, 6, 3};
        
        try (SingleReadBamParser bam = new SingleReadBamParser(TEST_BAM)) {
            WindowIterator<SingleReadAlignment> windows = new WindowIterator<>(
                    bam.stream()
                       .filter(x -> x.hasAlignment())
                       .map(x -> x.getAlignment().get())
                       .iterator(), windowSize, stepSize);

            for (int i = 0; i < numWindows; i++) {
                Populated<SingleReadAlignment> window = windows.next();
                assertThat(window.getStart(), is(starts[i]));
                assertThat(window.getEnd(), is(ends[i]));
                assertThat(window.getPopulationSize(), is(counts[i]));
            }
        }
    }
    
    @Test
    public void testSecondGroupOfReadsOnChr1() {
        
        // This test checks, among other things, that the iterator skips
        // regions with no reads (the region between the 1st and 2nd group
        // of reads)
        
        // Second cluster of reads starts just after chr1:10205
        // and ends just after chr1:10280
        
        int numPreviousWindows = 11;
        int numWindows = 15;
        int windowSize = 10;
        int stepSize = 5;
        int groupStart = 10210;
        
        int[] starts = new int[numWindows];
        for (int i = 0; i < numWindows; i++) {
            starts[i] = groupStart + stepSize * i;
        }
        
        int[] ends = new int[numWindows];
        for (int i = 0; i < numWindows; i++) {
            ends[i] = starts[i] + windowSize;
        }
        
        int[] counts = {4, 4, 4, 4, 6, 9, 10, 10, 10, 6, 6, 6, 6, 4, 1};
        
        try (SingleReadBamParser bam = new SingleReadBamParser(TEST_BAM)) {
            WindowIterator<SingleReadAlignment> windows = new WindowIterator<>(
                    bam.stream()
                       .filter(x -> x.hasAlignment())
                       .map(x -> x.getAlignment().get())
                       .iterator(), windowSize, stepSize);

            for (int i = 0; i < numPreviousWindows; i++) {
                windows.next();
            }
            
            for (int i = 0; i < numWindows; i++) {
                Populated<SingleReadAlignment> window = windows.next();
                assertThat(window.getStart(), is(starts[i]));
                assertThat(window.getEnd(), is(ends[i]));
                assertThat(window.getPopulationSize(), is(counts[i]));
            }
        }
    }
    
    @Test
    public void testFirstGroupOfReadsOnChr2() {
        int numWindows = 3;
        int windowSize = 100;
        int stepSize = 100;
        int groupStart = 10000;
        
        int[] starts = new int[numWindows];
        for (int i = 0; i < numWindows; i++) {
            starts[i] = groupStart + stepSize * i;
        }
        
        int[] ends = new int[numWindows];
        for (int i = 0; i < numWindows; i++) {
            ends[i] = starts[i] + windowSize;
        }
        
        int[] counts = {10, 11, 3};
        
        try (SingleReadBamParser bam = new SingleReadBamParser(TEST_BAM)) {
            WindowIterator<SingleReadAlignment> windows = new WindowIterator<>(
                    bam.stream()
                       .filter(x -> x.hasAlignment())
                       .map(x -> x.getAlignment().get())
                       .iterator(), windowSize, stepSize);
            
            int i = 0;
            
            while (windows.hasNext()) {
                Populated<SingleReadAlignment> window = windows.next();
                if (window.getReferenceName().equals("chr2")) {
                    assertThat(window.getStart(), is(starts[i]));
                    assertThat(window.getEnd(), is(ends[i]));
                    assertThat(window.getPopulationSize(), is(counts[i]));
                    if (++i >= numWindows) {
                        break;
                    }
                }
            }
        }
    }
}
