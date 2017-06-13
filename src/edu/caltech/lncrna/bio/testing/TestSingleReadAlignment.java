package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.Test;

import edu.caltech.lncrna.bio.alignment.SingleReadAlignment;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.io.SingleReadBamParser;
import edu.caltech.lncrna.bio.sequence.Base;

public class TestSingleReadAlignment {

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

    @Test
    public void testReadBaseFromRefPosWrongChromosome() {
        
        // Two reads exist with this. Will test the second in pair.
        String name = "HISEQ:634:HC2KYBCXY:1:2212:10038:36930";

        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            Iterator<SingleReadAlignment> alignments = bp.getAlignmentIterator();
            while (alignments.hasNext()) {
                SingleReadAlignment alignment = alignments.next();
                if (alignment.getName().equals(name) && !alignment.isFirstInPair()) {
                    assertThat(alignment.getReadBaseFromReferencePosition("chr2", 34210228),
                            is(Base.INVALID));
                    break;
                }
            }
        }
    }
    
    @Test
    public void testReadBaseFromRefPosInitialSoftClip() {
        
        // Two reads exist with this. Will test the second in pair.
        String name = "HISEQ:634:HC2KYBCXY:1:2212:10038:36930";

        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            Iterator<SingleReadAlignment> alignments = bp.getAlignmentIterator();
            while (alignments.hasNext()) {
                SingleReadAlignment alignment = alignments.next();
                if (alignment.getName().equals(name) && !alignment.isFirstInPair()) {
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 34210226),
                            is(Base.INVALID));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 34210227),
                            is(Base.A));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 34210228),
                            is(Base.C));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 34210229),
                            is(Base.G));
                    break;
                }
            }
        }
    }
    
    @Test
    public void testReadBaseFromRefPosInsertion() {
        
        // Two reads exist with this. Will test the second in pair.
        String name = "HISEQ:634:HC2KYBCXY:1:2212:10038:36930";

        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            Iterator<SingleReadAlignment> alignments = bp.getAlignmentIterator();
            while (alignments.hasNext()) {
                SingleReadAlignment alignment = alignments.next();
                if (alignment.getName().equals(name) && !alignment.isFirstInPair()) {
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 34210261),
                            is(Base.C));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 34210262),
                            is(Base.T));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 34210263),
                            is(Base.A));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 34210264),
                            is(Base.G));
                    break;
                }
            }
        }
    }
    
    @Test
    public void testReadBaseFromRefPosDeletion() {
        
        // Two reads exist with this. Will test the first in pair.
        String name = "HISEQ:634:HC2KYBCXY:2:1104:18193:68170";

        try (SingleReadBamParser bp = new SingleReadBamParser(BAM1)) {
            Iterator<SingleReadAlignment> alignments = bp.getAlignmentIterator();
            while (alignments.hasNext()) {
                SingleReadAlignment alignment = alignments.next();
                if (alignment.getName().equals(name) && alignment.isFirstInPair()) {
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 60603460),
                            is(Base.G));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 60603461),
                            is(Base.G));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 60603462),
                            is(Base.INVALID));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 60603463),
                            is(Base.INVALID));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 60603464),
                            is(Base.T));
                    assertThat(alignment.getReadBaseFromReferencePosition("chr1", 60603465),
                            is(Base.T));
                    break;
                }
            }
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