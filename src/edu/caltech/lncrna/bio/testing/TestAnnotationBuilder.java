package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.BlockedAnnotation.BlockedBuilder;
import edu.caltech.lncrna.bio.annotation.Strand;

public class TestAnnotationBuilder {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testBlockedBuilder() {
        BlockedBuilder bb = new BlockedBuilder();
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 300, 400, Strand.POSITIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        Annotated b = bb.build();
        assertThat(b.getReferenceName(), is("chr1"));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(400));
        assertThat(b.getStrand(), is(Strand.POSITIVE));
        assertThat(b.getNumberOfBlocks(), is(2));
        Iterator<Annotated> iter = b.getBlockIterator();
        assertThat(iter.next(), is(block1));
        assertThat(iter.next(), is(block2));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testBlockedBuilderFailsWithNoBlocks() {
        thrown.expect(IllegalArgumentException.class);
        (new BlockedBuilder()).build();
    }
    
    @Test
    public void testBlockedBuilderFailsWithDifferentChromosomes() {
        thrown.expect(IllegalArgumentException.class);
        BlockedBuilder bb = new BlockedBuilder();
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr2", 300, 400, Strand.POSITIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        bb.build();
    }
    
    @Test
    public void testBlockedBuilderFailsWithDifferentStrands() {
        thrown.expect(IllegalArgumentException.class);
        BlockedBuilder bb = new BlockedBuilder();
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 300, 400, Strand.NEGATIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        bb.build();
    }
    
    @Test
    public void testBlockedBuilderFailsWithInvalidStrands() {
        thrown.expect(IllegalArgumentException.class);
        BlockedBuilder bb = new BlockedBuilder();
        Annotated block1 = new Block("chr1", 100, 200, Strand.INVALID);
        Annotated block2 = new Block("chr1", 300, 400, Strand.INVALID);
        bb.addBlock(block1);
        bb.addBlock(block2);
        bb.build();
    }
    
    @Test
    public void testMergingOverlappingBlocks() {
        BlockedBuilder bb = new BlockedBuilder();
        
        Annotated blockA1 = new Block("chr1", 100, 300, Strand.BOTH);
        Annotated blockA2 = new Block("chr1", 200, 400, Strand.BOTH);
        Annotated blockA3 = new Block("chr1", 300, 500, Strand.BOTH);
        Annotated blockA4 = new Block("chr1", 450, 475, Strand.BOTH);
        
        Annotated blockB1 = new Block("chr1", 600, 800, Strand.BOTH);
        Annotated blockB2 = new Block("chr1", 700, 900, Strand.BOTH);
        Annotated blockB3 = new Block("chr1", 800, 1000, Strand.BOTH);
        Annotated blockB4 = new Block("chr1", 950, 975, Strand.BOTH);
        
        Annotated blockC1 = new Block("chr1", 1200, 1400, Strand.BOTH);
        Annotated blockC2 = new Block("chr1", 1300, 1500, Strand.BOTH);
        Annotated blockC3 = new Block("chr1", 1400, 1600, Strand.BOTH);
        Annotated blockC4 = new Block("chr1", 1550, 1575, Strand.BOTH);
        
        bb.addBlock(blockA1).addBlock(blockA2).addBlock(blockA3).addBlock(blockA4);
        bb.addBlock(blockB1).addBlock(blockB2).addBlock(blockB3).addBlock(blockB4);
        bb.addBlock(blockC1).addBlock(blockC2).addBlock(blockC3).addBlock(blockC4);
        Annotated b = bb.build();
        assertThat(b.getReferenceName(), is("chr1"));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(1600));
        assertThat(b.getStrand(), is(Strand.BOTH));
        assertThat(b.getNumberOfBlocks(), is(3));
        Iterator<Annotated> iter = b.getBlockIterator();
        assertThat(new Block("chr1", 100, 500, Strand.BOTH), is(iter.next()));
        assertThat(new Block("chr1", 600, 1000, Strand.BOTH), is(iter.next()));
        assertThat(new Block("chr1", 1200, 1600, Strand.BOTH), is(iter.next()));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testMergingAdjacentBlocks() {
        BlockedBuilder bb = new BlockedBuilder();
        Annotated block1 = new Block("chr1", 100, 200, Strand.BOTH);
        Annotated block2 = new Block("chr1", 200, 300, Strand.BOTH);
        bb.addBlock(block1).addBlock(block2);
        Annotated b = bb.build();
        assertThat(b.getNumberOfBlocks(), is(1));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(300));
        assertThat(b.getStrand(), is(Strand.BOTH));
    }

}
