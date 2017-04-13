package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.annotation.Annotation.AnnotationBuilder;

public class TestAnnotationContainment {

    @Test
    public void testBlockContainsSelf() {
        Annotated b= new Annotation("chr1", 100, 200, Strand.POSITIVE);
        assertThat(b.contains(b), is(true));
    }
    
    @Test
    public void testBlockContainsBlock() {
        Annotated big = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Annotation("chr1", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockDoesNotContainDifferentChromosomeBlock() {
        Annotated big = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Annotation("chr2", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockDoesNotContainOverlappingEdgeBlock() {
        Annotated big = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Annotation("chr1", 50, 150, Strand.POSITIVE);
        assertThat(big.overlaps(small), is(true));
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockDoesNotContainNonOverlappingBlock() {
        Annotated big = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Annotation("chr1", 0, 50, Strand.POSITIVE);
        assertThat(big.overlaps(small), is(false));
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockOnBothStrandsContainsBlock() {
        Annotated big = new Annotation("chr1", 100, 200, Strand.BOTH);
        Annotated small = new Annotation("chr1", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockDoesNotContainBlockOnBothStrands() {
        Annotated big = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = new Annotation("chr1", 125, 175, Strand.BOTH);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockContainsAnnotation() {
        Annotated big = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 120, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 150, 175, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockDoesNotContainOverlappingAnnotation() {
        Annotated big = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 95, 120, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 150, 175, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testAnnotationContainsSelf() {
        Annotated b = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(b.contains(b), is(true));
    }
    
    @Test
    public void testAnnotationContainsBlock() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = new Annotation("chr1", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testAnnotationDoesNotContainIntronBlock() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = new Annotation("chr1", 225, 275, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testAnnotationDoesNotContainOverlappingBlock() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = new Annotation("chr1", 150, 250, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testAnnotationDoesNotContainBlock() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = new Annotation("chr1", 50, 75, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testAnnotationContainsAnnotation() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 325, 375, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testAnnotationDoesNotContainDifferentChromosomeAnnotation() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr2", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr2", 325, 375, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testAnnotationDoesNotContainDifferentStrandAnnotation() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.NEGATIVE))
                .addAnnotation(new Annotation("chr1", 325, 375, Strand.NEGATIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testAnnotationBothStrandsContainsAnnotation() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.BOTH))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.BOTH))
                .build();
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.NEGATIVE))
                .addAnnotation(new Annotation("chr1", 325, 375, Strand.NEGATIVE))
                .build();
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testAnnotationDoesNotContainsAnnotationBothStrands() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.NEGATIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.NEGATIVE))
                .build();
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.BOTH))
                .addAnnotation(new Annotation("chr1", 325, 375, Strand.BOTH))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testAnnotationDoesNotContainAnnotation() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 325, 425, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testAnnotationDoesNotContainAnnotationOnIntron() {
        Annotated big = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated small = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 225, 275, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
}
