package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.BlockedAnnotation.BlockedBuilder;
import edu.caltech.lncrna.bio.annotation.Strand;

public class TestAnnotationOverlap {
    
    ///////////
    // Block //
    ///////////
    
    @Test
    public void testBlockDoesNotOverlapNull() {
        Annotated block = new Block("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.overlaps(null), is(false));
    }
    
    @Test
    public void testBlockOverlapsItself() {
        Annotated block = new Block("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.overlaps(block), is(true));
    }
    
    @Test
    public void testBlockOverlapsAnotherBlock() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 150, 250, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(true));
    }
    
    @Test
    public void testBlockDoesNotOverlapComplement() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 100, 200, Strand.NEGATIVE);
        assertThat(b1.overlaps(b2), is(false));
    }
    
    @Test
    public void testBlockOverlapsBlockWithBothStrands() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 100, 200, Strand.BOTH);
        assertThat(b1.overlaps(b2), is(true));
    }
    
    @Test
    public void testBlockDoesNotOverlapDifferentReference() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr3", 100, 200, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(false));
    }
    
    @Test
    public void testBlockDoesNotOverlapAdjacentBlocks() {
        Annotated b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 0, 100, Strand.POSITIVE);
        Annotated b3 = new Block("chr2", 200, 300, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(false));
        assertThat(b1.overlaps(b3), is(false));
    }
    
    ///////////////////////
    // BlockedAnnotation //
    ///////////////////////
    
    @Test
    public void testBlockedAnnotationDoesNotOverlapNull() {
        Annotated annot = (new BlockedBuilder())
                .addBlock(new Block("chr2", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr2", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot.overlaps(null), is(false));
    }
    
    @Test
    public void testBlockedAnnotationOverlapsItself() {
        Annotated annot = (new BlockedBuilder())
                .addBlock(new Block("chr2", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr2", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot.overlaps(annot), is(true));
    }
    
    @Test
    public void testBlockedAnnotationOverlapsAnotherBlock() {
        Annotated annot1 = (new BlockedBuilder())
                .addBlock(new Block("chr2", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr2", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = new Block("chr2", 150, 250, Strand.POSITIVE);
        assertThat(annot1.overlaps(annot2), is(true));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotOverlapNonoverlappingBlock() {
        Annotated annot1 = (new BlockedBuilder())
                .addBlock(new Block("chr2", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr2", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = new Block("chr2", 200, 300, Strand.POSITIVE);
        assertThat(annot1.overlaps(annot2), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotOverlapBlockOnDifferentReference() {
        Annotated annot1 = (new BlockedBuilder())
                .addBlock(new Block("chr2", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr2", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = new Block("chr1", 250, 350, Strand.POSITIVE);
        assertThat(annot1.overlaps(annot2), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotOverlapBlockOnDifferentStrand() {
        Annotated annot1 = (new BlockedBuilder())
                .addBlock(new Block("chr2", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr2", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = new Block("chr2", 250, 350, Strand.NEGATIVE);
        assertThat(annot1.overlaps(annot2), is(false));
    }

}
