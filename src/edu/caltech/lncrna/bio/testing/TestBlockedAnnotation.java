package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.BlockedAnnotation;
import edu.caltech.lncrna.bio.annotation.BlockedAnnotation.BlockedBuilder;
import edu.caltech.lncrna.bio.annotation.Strand;

public class TestBlockedAnnotation {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testBlockedAnnotationConstructorNullReference() {
        thrown.expect(NullPointerException.class);
        new BlockedAnnotation(null);
    }
    
    @Test
    public void testBlockedAnnotationFromBlockReference() {
        Block block = new Block("chr1", 100, 200, Strand.POSITIVE);
        BlockedAnnotation annot = new BlockedAnnotation(block);
        assertThat(annot.getReferenceName(), is("chr1"));
        assertThat(annot.getStart(), is(100));
        assertThat(annot.getEnd(), is(200));
        assertThat(annot.getStrand(), is(Strand.POSITIVE));
        assertThat(annot.getNumberOfBlocks(), is(1));
        assertThat(annot.getBlockIterator().next(), is(block));
        assertThat(annot.getBlockStream().findFirst().get(), is(block));
    }
    
    @Test
    public void testBlockedAnnotationFromAnnotation() {
        BlockedBuilder bb = new BlockedBuilder();
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr1", 300, 400, Strand.POSITIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        Annotated oldAnnot = bb.build();
        BlockedAnnotation newAnnot = new BlockedAnnotation(oldAnnot);
        assertThat(newAnnot.getReferenceName(), is("chr1"));
        assertThat(newAnnot.getStart(), is(100));
        assertThat(newAnnot.getEnd(), is(400));
        assertThat(newAnnot.getStrand(), is(Strand.POSITIVE));
        assertThat(newAnnot.getNumberOfBlocks(), is(2));
        Iterator<Block> iter = newAnnot.getBlockIterator();
        assertThat(iter.next(), is(block1));
        assertThat(iter.next(), is(block2));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testBlockedAnnotationFromAnnotationAndStrand() {
        BlockedBuilder bb = new BlockedBuilder();
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr1", 300, 400, Strand.POSITIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        Annotated oldAnnot = bb.build();
        BlockedAnnotation newAnnot = new BlockedAnnotation(oldAnnot, Strand.NEGATIVE);
        assertThat(newAnnot.getReferenceName(), is("chr1"));
        assertThat(newAnnot.getStart(), is(100));
        assertThat(newAnnot.getEnd(), is(400));
        assertThat(newAnnot.getStrand(), is(Strand.NEGATIVE));
        assertThat(newAnnot.getNumberOfBlocks(), is(2));
        Iterator<Block> iter = newAnnot.getBlockIterator();
        assertThat(iter.next(), is(new Block(block1, Strand.NEGATIVE)));
        assertThat(iter.next(), is(new Block(block2, Strand.NEGATIVE)));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testBlockedBuilder() {
        BlockedBuilder bb = new BlockedBuilder();
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr1", 300, 400, Strand.POSITIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        Annotated b = bb.build();
        assertThat(b.getReferenceName(), is("chr1"));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(400));
        assertThat(b.getStrand(), is(Strand.POSITIVE));
        assertThat(b.getNumberOfBlocks(), is(2));
        Iterator<Block> iter = b.getBlockIterator();
        assertThat(iter.next(), is(block1));
        assertThat(iter.next(), is(block2));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testBlockedBuilderFailsWithDifferentChromosomes() {
        thrown.expect(IllegalArgumentException.class);
        BlockedBuilder bb = new BlockedBuilder();
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr2", 300, 400, Strand.POSITIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        bb.build();
    }
    
    @Test
    public void testBlockedBuilderFailsWithDifferentStrands() {
        thrown.expect(IllegalArgumentException.class);
        BlockedBuilder bb = new BlockedBuilder();
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr1", 300, 400, Strand.NEGATIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        bb.build();
    }
    
    @Test
    public void testBlockedBuilderFailsWithInvalidStrands() {
        thrown.expect(IllegalArgumentException.class);
        BlockedBuilder bb = new BlockedBuilder();
        Block block1 = new Block("chr1", 100, 200, Strand.INVALID);
        Block block2 = new Block("chr1", 300, 400, Strand.INVALID);
        bb.addBlock(block1);
        bb.addBlock(block2);
        bb.build();
    }
    
    @Test
    public void testMergingBlocks() {
        BlockedBuilder bb = new BlockedBuilder();
        
        Block blockA1 = new Block("chr1", 100, 300, Strand.BOTH);
        Block blockA2 = new Block("chr1", 200, 400, Strand.BOTH);
        Block blockA3 = new Block("chr1", 300, 500, Strand.BOTH);
        Block blockA4 = new Block("chr1", 450, 475, Strand.BOTH);
        
        Block blockB1 = new Block("chr1", 600, 800, Strand.BOTH);
        Block blockB2 = new Block("chr1", 700, 900, Strand.BOTH);
        Block blockB3 = new Block("chr1", 800, 1000, Strand.BOTH);
        Block blockB4 = new Block("chr1", 950, 975, Strand.BOTH);
        
        Block blockC1 = new Block("chr1", 1200, 1400, Strand.BOTH);
        Block blockC2 = new Block("chr1", 1300, 1500, Strand.BOTH);
        Block blockC3 = new Block("chr1", 1400, 1600, Strand.BOTH);
        Block blockC4 = new Block("chr1", 1550, 1575, Strand.BOTH);
        
        bb.addBlock(blockA1).addBlock(blockA2).addBlock(blockA3).addBlock(blockA4);
        bb.addBlock(blockB1).addBlock(blockB2).addBlock(blockB3).addBlock(blockB4);
        bb.addBlock(blockC1).addBlock(blockC2).addBlock(blockC3).addBlock(blockC4);
        Annotated b = bb.build();
        assertThat(b.getReferenceName(), is("chr1"));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(1600));
        assertThat(b.getStrand(), is(Strand.BOTH));
        assertThat(b.getNumberOfBlocks(), is(3));
        Iterator<Block> iter = b.getBlockIterator();
        assertThat(new Block("chr1", 100, 500, Strand.BOTH), is(iter.next()));
        assertThat(new Block("chr1", 600, 1000, Strand.BOTH), is(iter.next()));
        assertThat(new Block("chr1", 1200, 1600, Strand.BOTH), is(iter.next()));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testMergeAdjacentBlocks() {
        BlockedBuilder bb = new BlockedBuilder();
        Block block1 = new Block("chr1", 100, 200, Strand.BOTH);
        Block block2 = new Block("chr1", 200, 300, Strand.BOTH);
        bb.addBlock(block1).addBlock(block2);
        Annotated b = bb.build();
        assertThat(b.getNumberOfBlocks(), is(1));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(300));
        assertThat(b.getStrand(), is(Strand.BOTH));
    }
    
    //TODO test overlap / intersect functions
    
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