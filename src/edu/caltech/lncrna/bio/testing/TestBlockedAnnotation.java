/*package edu.caltech.lncrna.bio.testing;

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
        Annotated block = new Block("chr1", 100, 200, Strand.POSITIVE);
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
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 300, 400, Strand.POSITIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        Annotated oldAnnot = bb.build();
        BlockedAnnotation newAnnot = new BlockedAnnotation(oldAnnot);
        assertThat(newAnnot.getReferenceName(), is("chr1"));
        assertThat(newAnnot.getStart(), is(100));
        assertThat(newAnnot.getEnd(), is(400));
        assertThat(newAnnot.getStrand(), is(Strand.POSITIVE));
        assertThat(newAnnot.getNumberOfBlocks(), is(2));
        Iterator<Annotated> iter = newAnnot.getBlockIterator();
        assertThat(iter.next(), is(block1));
        assertThat(iter.next(), is(block2));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testBlockedAnnotationFromAnnotationAndStrand() {
        BlockedBuilder bb = new BlockedBuilder();
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 300, 400, Strand.POSITIVE);
        bb.addBlock(block1);
        bb.addBlock(block2);
        Annotated oldAnnot = bb.build();
        Annotated newAnnot = new BlockedAnnotation(oldAnnot, Strand.NEGATIVE);
        assertThat(newAnnot.getReferenceName(), is("chr1"));
        assertThat(newAnnot.getStart(), is(100));
        assertThat(newAnnot.getEnd(), is(400));
        assertThat(newAnnot.getStrand(), is(Strand.NEGATIVE));
        assertThat(newAnnot.getNumberOfBlocks(), is(2));
        Iterator<Annotated> iter = newAnnot.getBlockIterator();
        assertThat(iter.next(), is(new Block(block1, Strand.NEGATIVE)));
        assertThat(iter.next(), is(new Block(block2, Strand.NEGATIVE)));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testBlockedAnnotationEqualsItself() {
        Annotated annot = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot.equals(annot), is(true));
    }
    
    @Test
    public void testBlockedAnnotationEqualsEquivalentBlockedAnnotation() {
        Annotated annot1 = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot1.equals(annot2), is(true));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotEqualBlockedAnnotationOnDifferentReference() {
        Annotated annot1 = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = (new BlockedBuilder())
                .addBlock(new Block("chr3", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr3", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot1.equals(annot2), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotEqualBlockedAnnotationOnDifferentStrand() {
        Annotated annot1 = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.BOTH))
                .addBlock(new Block("chr1", 300, 400, Strand.BOTH))
                .build();
        Annotated annot2 = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot1.equals(annot2), is(false));
    }
    
    @Test
    public void testBlockedAnnotationDoesNotEqualBlockedAnnotationWithDifferentBlocks() {
        Annotated annot1 = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 401, Strand.POSITIVE))
                .build();
        Annotated annot2 = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 200, Strand.POSITIVE))
                .addBlock(new Block("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot1.equals(annot2), is(false));
    }
    
}*/