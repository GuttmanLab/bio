package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.annotation.BlockedAnnotation.BlockedBuilder;

public class TestAnnotationContainment {

    @Test
    public void testBlockContainsSelf() {
        Annotated b= new Block("chr1", 100, 200, Strand.POSITIVE);
        assertThat(b.contains(b), is(true));
    }
    
    @Test
    public void testBlockContainsBlock() {
        Annotated big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Block("chr1", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockDoesNotContainDifferentChromosomeBlock() {
        Annotated big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Block("chr2", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockDoesNotContainOverlappingEdgeBlock() {
        Annotated big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Block("chr1", 50, 150, Strand.POSITIVE);
        assertThat(big.overlaps(small), is(true));
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockDoesNotContainNonOverlappingBlock() {
        Annotated big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Block("chr1", 0, 50, Strand.POSITIVE);
        assertThat(big.overlaps(small), is(false));
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockOnBothStrandsContainsBlock() {
        Annotated big = new Block("chr1", 100, 200, Strand.BOTH);
        Annotated small = new Block("chr1", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockDoesNotContainBlockOnBothStrands() {
        Annotated big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Block("chr1", 125, 175, Strand.BOTH);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockContainsBlockedAnnotation() {
        Annotated big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 120, Strand.POSITIVE))
                .addBlock(new Block("chr1", 150, 175, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockDoesNotContainOverlappingBlockedAnnotation() {
        Annotated big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 95, 120, Strand.POSITIVE))
                .addBlock(new Block("chr1", 150, 175, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockedAnnotationContainsSelf() {
        Annotated b = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(b.contains(b), is(true));
    }
    
    @Test
    public void testBlockedAnnotationContainsBlock() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Block small = new Block("chr1", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotContainIntronBlock() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Block small = new Block("chr1", 225, 275, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotContainOverlappingBlock() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Block small = new Block("chr1", 150, 250, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotContainBlock() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Block small = new Block("chr1", 50, 75, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockedAnnotationContainsBlockedAnnotation() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 325, 375, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotContainDifferentChromosomeBlockedAnnotation() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr2", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr2", 325, 375, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotContainDifferentStrandBlockedAnnotation() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.NEGATIVE))
                .addBlock(new Block("chr1", 325, 375, Strand.NEGATIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockedAnnotationBothStrandsContainsBlockedAnnotation() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.BOTH))
                .addBlock(new Block("chr1", 300, 400, Strand.BOTH))
                .build();
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.NEGATIVE))
                .addBlock(new Block("chr1", 325, 375, Strand.NEGATIVE))
                .build();
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotContainsBlockedAnnotationBothStrands() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.NEGATIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.NEGATIVE))
                .build();
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.BOTH))
                .addBlock(new Block("chr1", 325, 375, Strand.BOTH))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotContainBlockedAnnotation() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 325, 425, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotContainBlockedAnnotationOnIntron() {
        Annotated big = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 225, 275, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
}
