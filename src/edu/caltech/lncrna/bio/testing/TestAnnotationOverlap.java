package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.annotation.Annotation.AnnotationBuilder;
import edu.caltech.lncrna.bio.annotation.Strand;

public class TestAnnotationOverlap {
    
    ///////////
    // Block //
    ///////////
    
    @Test
    public void testBlockDoesNotOverlapNull() {
        Annotated block = new Annotation("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.overlaps(null), is(false));
    }
    
    @Test
    public void testBlockOverlapsItself() {
        Annotated block = new Annotation("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.overlaps(block), is(true));
    }
    
    @Test
    public void testBlockOverlapsAnotherBlock() {
        Annotated b1 = new Annotation("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr2", 150, 250, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(true));
    }
    
    @Test
    public void testBlockDoesNotOverlapComplement() {
        Annotated b1 = new Annotation("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr2", 100, 200, Strand.NEGATIVE);
        assertThat(b1.overlaps(b2), is(false));
    }
    
    @Test
    public void testBlockOverlapsBlockWithBothStrands() {
        Annotated b1 = new Annotation("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr2", 100, 200, Strand.BOTH);
        assertThat(b1.overlaps(b2), is(true));
    }
    
    @Test
    public void testBlockDoesNotOverlapDifferentReference() {
        Annotated b1 = new Annotation("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr3", 100, 200, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(false));
    }
    
    @Test
    public void testBlockDoesNotOverlapAdjacentBlocks() {
        Annotated b1 = new Annotation("chr2", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr2", 0, 100, Strand.POSITIVE);
        Annotated b3 = new Annotation("chr2", 200, 300, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(false));
        assertThat(b1.overlaps(b3), is(false));
    }
    
    ///////////////////////
    // Annotation //
    ///////////////////////
    
    @Test
    public void testAnnotationDoesNotOverlapNull() {
        Annotated annot = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr2", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr2", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot.overlaps(null), is(false));
    }
    
    @Test
    public void testAnnotationOverlapsItself() {
        Annotated annot = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr2", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr2", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(annot.overlaps(annot), is(true));
    }
    
    @Test
    public void testAnnotationOverlapsAnotherBlock() {
        Annotated annot1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr2", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr2", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = new Annotation("chr2", 150, 250, Strand.POSITIVE);
        assertThat(annot1.overlaps(annot2), is(true));
    }
    
    @Test
    public void testAnnotationDoesNotOverlapNonoverlappingBlock() {
        Annotated annot1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr2", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr2", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = new Annotation("chr2", 200, 300, Strand.POSITIVE);
        assertThat(annot1.overlaps(annot2), is(false));
    }
    
    @Test
    public void testAnnotationDoesNotOverlapBlockOnDifferentReference() {
        Annotated annot1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr2", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr2", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = new Annotation("chr1", 250, 350, Strand.POSITIVE);
        assertThat(annot1.overlaps(annot2), is(false));
    }
    
    @Test
    public void testAnnotationDoesNotOverlapBlockOnDifferentStrand() {
        Annotated annot1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr2", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr2", 300, 400, Strand.POSITIVE))
                .build();
        Annotated annot2 = new Annotation("chr2", 250, 350, Strand.NEGATIVE);
        assertThat(annot1.overlaps(annot2), is(false));
    }

}
