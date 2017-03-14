package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import edu.caltech.lncrna.bio.alignment.PairedEndAlignment;
import edu.caltech.lncrna.bio.alignment.SingleReadAlignment;
import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.BlockedAnnotation.BlockedBuilder;
import edu.caltech.lncrna.bio.io.PairedEndBamParser;
import edu.caltech.lncrna.bio.annotation.Strand;


public class TestAnnotationIntersection {
    
    private PairedEndAlignment readPair;
    private SingleReadAlignment posRead;
    private SingleReadAlignment negRead;
    private final static Path BAM1 = Paths.get("/Users/masonmlai/Documents/" +
            "Repositories/GuttmanLab/testing/paired_end_with_splice.bam");
    
    @Before
    public void setup() {
        
        try (PairedEndBamParser bp = new PairedEndBamParser(BAM1)) {
            Iterator<PairedEndAlignment> alignments = bp.getAlignmentIterator();

            while (alignments.hasNext()) {
                readPair = alignments.next();
            
                if (readPair.getName().equals(
                        "HISEQ:634:HC2KYBCXY:2:1209:18108:79498")) {
                                        
                    posRead = readPair.getFirstReadInPair();
                    negRead = readPair.getSecondReadInPair();
                    
                    System.err.println(posRead.toString());
                    System.err.println(negRead.toString());

                    break;
                }
            }
        }
    }
    
    ///////////
    // Block //
    ///////////
    
    @Test
    public void testBlockDoesNotIntersectNull() {
        Annotated block = new Block("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.intersect(null).isPresent(), is(false));
    }
    
    @Test
    public void testBlockIntersectsItself() {
        Annotated block = new Block("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.intersect(block).get(), is(block));
    }
    
    @Test
    public void testBlockIntersectsAnotherBlock() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 150, 250, Strand.POSITIVE);
        Annotated intersection = new Block("chr2", 150, 200, Strand.POSITIVE);
        assertThat(b1.intersect(b2).get(), is(intersection));
    }
    
    @Test
    public void testBlockDoesNotIntersectComplementaryBlock() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 100, 200, Strand.NEGATIVE);
        assertThat(b1.intersect(b2).isPresent(), is(false));
    }
    
    @Test
    public void testBlockIntersectsBlockWithBothStrands() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 100, 200, Strand.BOTH);
        assertThat(b1.intersect(b2).get(), is(b1));
    }
    
    @Test
    public void testBlockDoesNotIntersectBlockOnDifferentReference() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr3", 100, 200, Strand.POSITIVE);
        assertThat(b1.intersect(b2).isPresent(), is(false));
    }
    
    @Test
    public void testBlockDoesNotIntersectAdjacentBlocks() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 0, 100, Strand.POSITIVE);
        Annotated b3 = new Block("chr2", 200, 300, Strand.POSITIVE);
        assertThat(b1.intersect(b2).isPresent(), is(false));
        assertThat(b1.intersect(b3).isPresent(), is(false));
    }
    
    ///////////////////////
    // BlockedAnnotation //
    ///////////////////////
    
    @Test
    public void testBlockedAnnotationDoesNotIntersectNull() {
        Annotated annot = (new BlockedBuilder())
                .addBlock(new Block("chr1", 10, 20, Strand.POSITIVE))
                .addBlock(new Block("chr1", 30, 40, Strand.POSITIVE))
                .build();
        assertThat(annot.intersect(null).isPresent(), is(false));
    }
    
    @Test
    public void testBlockedAnnotationIntersectsItself() {
        Annotated annot = (new BlockedBuilder())
                .addBlock(new Block("chr1", 10, 20, Strand.POSITIVE))
                .addBlock(new Block("chr1", 30, 40, Strand.POSITIVE))
                .build();
        assertThat(annot.intersect(annot).get(), is(annot));
    }
    
    @Test
    public void testBlockedAnnotationIntersectsAnOverlappingBlock() {
        Annotated annot = (new BlockedBuilder())
                .addBlock(new Block("chr1", 10, 20, Strand.POSITIVE))
                .addBlock(new Block("chr1", 30, 40, Strand.POSITIVE))
                .build();
        Annotated block = new Block("chr1", 15, 25, Strand.POSITIVE);
        Annotated result = (new BlockedBuilder())
                .addBlock(new Block("chr1", 15, 20, Strand.POSITIVE))
                .build();
        assertThat(annot.intersect(block).get(), is(result));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotIntersectBlockOnOtherChromosome() {
        Annotated annot = (new BlockedBuilder())
                .addBlock(new Block("chr1", 10, 20, Strand.POSITIVE))
                .addBlock(new Block("chr1", 30, 40, Strand.POSITIVE))
                .build();
        Annotated block = new Block("chr2", 15, 25, Strand.POSITIVE);
        assertThat(annot.intersect(block).isPresent(), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotIntersectIntrons() {
        Annotated annot = (new BlockedBuilder())
                .addBlock(new Block("chr1", 0, 100, Strand.NEGATIVE))
                .addBlock(new Block("chr1", 200, 300, Strand.NEGATIVE))
                .addBlock(new Block("chr1", 400, 500, Strand.NEGATIVE))
                .build();
        Annotated introns = annot.getIntrons().get();
        assertThat(annot.intersect(introns).isPresent(), is(false));
    }
    
    ////////////////////////
    // PairedEndAlignment //
    ////////////////////////
    
    @Test
    public void testPairedEndAlignmentPosReadIntersectsPosStrand() {
        Annotated block = new Block("chr1", 4764500, 4766620, Strand.POSITIVE);
        Annotated intersection = (new BlockedBuilder())
                .addBlock(new Block("chr1", 4764554, 4764598, Strand.POSITIVE))
                .addBlock(new Block("chr1", 4766617, 4766620, Strand.POSITIVE))
                .build();
        assertThat(posRead.intersect(block)
                .orElseThrow(() -> new IllegalStateException("No intersection"))
                , is(intersection));
    }
    
    @Test
    public void testPairedEndAlignmentDoesPosReadNotIntersectNegStrand() {
        Annotated block = new Block("chr1", 4764500, 4766620, Strand.NEGATIVE);
        assertThat(posRead.intersect(block).isPresent(), is(false));
    }
    
    @Test
    public void testPairedEndAlignmentPosReadIntersectsBothStrand() {
        Annotated block = new Block("chr1", 4764500, 4766620, Strand.BOTH);
        Annotated intersection = (new BlockedBuilder())
                .addBlock(new Block("chr1", 4764554, 4764598, Strand.POSITIVE))
                .addBlock(new Block("chr1", 4766617, 4766620, Strand.POSITIVE))
                .build();
        assertThat(posRead.intersect(block)
                .orElseThrow(() -> new IllegalStateException("No intersection"))
                , is(intersection));
    }
    
    @Test
    public void testPairedEndAlignmentNegReadIntersectsNegStrand() {
        Annotated block = new Block("chr1", 4765500, 4766750, Strand.NEGATIVE);
        Annotated intersection = new Block("chr1", 4766627, 4766726, Strand.NEGATIVE);
        assertThat(negRead.intersect(block)
                .orElseThrow(() -> new IllegalStateException("No intersection"))
                , is(intersection));
    }
    
    @Test
    public void testPairedEndAlignmentNegReadDoesNotIntersectPosStrand() {
        Annotated block = new Block("chr1", 4765500, 4766750, Strand.POSITIVE);
        assertThat(negRead.intersect(block).isPresent(), is(false));
    }
    
    @Test
    public void testPairedEndAlignmentNegReadIntersectsBothStrand() {
        Annotated block = new Block("chr1", 4765500, 4766750, Strand.BOTH);
        Annotated intersection = new Block("chr1", 4766627, 4766726, Strand.NEGATIVE);
        assertThat(negRead.intersect(block)
                .orElseThrow(() -> new IllegalStateException("No intersection"))
                , is(intersection));
    }
    
    @Test
    public void testPairedEndAlignmentIntersectsNegStrand() {
        Annotated block = new Block("chr1", 4764500, 4766750, Strand.NEGATIVE);
        Annotated intersection = new Block("chr1", 4764554, 4766726, Strand.NEGATIVE);
        assertThat(negRead.intersect(block)
                .orElseThrow(() -> new IllegalStateException("No intersection"))
                , is(intersection));
    }
    
    @Test
    public void testPairedEndAlignmentDoesNotIntersectPosStrand() {
        Annotated block = new Block("chr1", 4764500, 4766750, Strand.POSITIVE);
        assertThat(negRead.intersect(block).isPresent(), is(false));
    }
    
    @Test
    public void testPairedEndAlignmentIntersectsBothStrand() {
        Annotated block = new Block("chr1", 4764500, 4766750, Strand.BOTH);
        Annotated intersection = new Block("chr1", 4764554, 4766726, Strand.NEGATIVE);
        assertThat(negRead.intersect(block)
                .orElseThrow(() -> new IllegalStateException("No intersection"))
                , is(intersection));
    }
}
