package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.annotation.Annotation.AnnotationBuilder;
import edu.caltech.lncrna.bio.annotation.Strand;

public class TestAnnotationBuilder {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testAnnotationBuilder() {
        AnnotationBuilder builder = new AnnotationBuilder();
        Annotated block1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Annotation("chr1", 300, 400, Strand.POSITIVE);
        builder.addAnnotation(block1);
        builder.addAnnotation(block2);
        Annotated b = builder.build();

        assertThat(b.getReferenceName(), is("chr1"));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(400));
        assertThat(b.getStrand(), is(Strand.POSITIVE));
        assertThat(b.getNumberOfBlocks(), is(2));

        Iterator<Annotated> iter = b.getBlockIterator();
        assertThat(iter.next(), is(block1));
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), is(block2));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testAnnotationBuilderFailsWithNoBlocks() {
        thrown.expect(IllegalArgumentException.class);
        (new AnnotationBuilder()).build();
    }
    
    @Test
    public void testAnnotationBuilderFailsWithDifferentChromosomes() {
        thrown.expect(IllegalArgumentException.class);
        AnnotationBuilder builder = new AnnotationBuilder();
        Annotated block1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Annotation("chr2", 300, 400, Strand.POSITIVE);
        builder.addAnnotation(block1);
        builder.addAnnotation(block2);
        builder.build();
    }
    
    @Test
    public void testAnnotationBuilderFailsWithDifferentStrands() {
        thrown.expect(IllegalArgumentException.class);
        AnnotationBuilder builder = new AnnotationBuilder();
        Annotated block1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Annotation("chr1", 300, 400, Strand.NEGATIVE);
        builder.addAnnotation(block1);
        builder.addAnnotation(block2);
        builder.build();
    }
    
    @Test
    public void testAnnotationBuilderFailsWithInvalidStrands() {
        thrown.expect(IllegalArgumentException.class);
        AnnotationBuilder builder = new AnnotationBuilder();
        Annotated block1 = new Annotation("chr1", 100, 200, Strand.INVALID);
        Annotated block2 = new Annotation("chr1", 300, 400, Strand.INVALID);
        builder.addAnnotation(block1);
        builder.addAnnotation(block2);
        builder.build();
    }
    
    @Test
    public void testMergingOverlappingBlocks() {
        AnnotationBuilder builder = new AnnotationBuilder();
        
        Annotated blockA1 = new Annotation("chr1", 100, 300, Strand.BOTH);
        Annotated blockA2 = new Annotation("chr1", 200, 400, Strand.BOTH);
        Annotated blockA3 = new Annotation("chr1", 300, 500, Strand.BOTH);
        Annotated blockA4 = new Annotation("chr1", 450, 475, Strand.BOTH);
        
        Annotated blockB1 = new Annotation("chr1", 600, 800, Strand.BOTH);
        Annotated blockB2 = new Annotation("chr1", 700, 900, Strand.BOTH);
        Annotated blockB3 = new Annotation("chr1", 800, 1000, Strand.BOTH);
        Annotated blockB4 = new Annotation("chr1", 950, 975, Strand.BOTH);
        
        Annotated blockC1 = new Annotation("chr1", 1200, 1400, Strand.BOTH);
        Annotated blockC2 = new Annotation("chr1", 1300, 1500, Strand.BOTH);
        Annotated blockC3 = new Annotation("chr1", 1400, 1600, Strand.BOTH);
        Annotated blockC4 = new Annotation("chr1", 1550, 1575, Strand.BOTH);
        
        builder.addAnnotation(blockA1).addAnnotation(blockA2).addAnnotation(blockA3).addAnnotation(blockA4);
        builder.addAnnotation(blockB1).addAnnotation(blockB2).addAnnotation(blockB3).addAnnotation(blockB4);
        builder.addAnnotation(blockC1).addAnnotation(blockC2).addAnnotation(blockC3).addAnnotation(blockC4);
        Annotated b = builder.build();
        assertThat(b.getReferenceName(), is("chr1"));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(1600));
        assertThat(b.getStrand(), is(Strand.BOTH));
        assertThat(b.getNumberOfBlocks(), is(3));
        Iterator<Annotated> iter = b.getBlockIterator();
        assertThat(new Annotation("chr1", 100, 500, Strand.BOTH), is(iter.next()));
        assertThat(new Annotation("chr1", 600, 1000, Strand.BOTH), is(iter.next()));
        assertThat(new Annotation("chr1", 1200, 1600, Strand.BOTH), is(iter.next()));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testMergingAdjacentBlocks() {
        AnnotationBuilder builder = new AnnotationBuilder();
        Annotated block1 = new Annotation("chr1", 100, 200, Strand.BOTH);
        Annotated block2 = new Annotation("chr1", 200, 300, Strand.BOTH);
        builder.addAnnotation(block1).addAnnotation(block2);
        Annotated b = builder.build();
        assertThat(b.getNumberOfBlocks(), is(1));
        assertThat(b.getStart(), is(100));
        assertThat(b.getEnd(), is(300));
        assertThat(b.getStrand(), is(Strand.BOTH));
    }

}
