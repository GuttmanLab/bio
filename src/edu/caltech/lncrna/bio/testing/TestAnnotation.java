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

public class TestAnnotation {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testAnnotationConstructorFailsWithNullReference() {
        thrown.expect(NullPointerException.class);
        new Annotation(null, 10, 20, Strand.POSITIVE);
    }
    
    @Test
    public void testAnnotationConstructorFailsWithNullStrand() {
        thrown.expect(NullPointerException.class);
        new Annotation("chr1", 10, 20, null);
    }
    
    @Test
    public void testAnnotationConstructorFailsWithNegativeCoords() {
        thrown.expect(IllegalArgumentException.class);
        new Annotation("chr1", -1, 20, Strand.POSITIVE);
    }
    
    @Test
    public void testAnnotationConstructorFailsWithZeroLength() {
        thrown.expect(IllegalArgumentException.class);
        new Annotation("chr1", 10, 10, Strand.POSITIVE);
    }
    
    @Test
    public void testAnnotationConstructorFailsWithInverseCoords() {
        thrown.expect(IllegalArgumentException.class);
        new Annotation("chr1", 20, 10, Strand.POSITIVE);
    }
    
    @Test
    public void testAnnotationConstructorFailsWithInvalidStrand() {
        thrown.expect(IllegalArgumentException.class);
        new Annotation("chr1", 10, 20, Strand.INVALID);
    }

    @Test
    public void testAnnotationConstructorWithValidBlock() {
        Annotated b1 = new Annotation("chr1", 10, 20, Strand.BOTH);
        Annotated b2 = new Annotation(b1);
        assertThat(b2, is(b1));
    }
    
    @Test
    public void testBlockIteratorWithOneBlock() {
        Annotated b = new Annotation("chr1", 10, 20, Strand.BOTH);
        Iterator<Annotated> iter = b.iterator();
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), is(b));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testAnnotationEqualityIdentity() {
        Annotated b = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(b, is(b));
    }
    
    @Test
    public void testAnnotationIsNotEqualToNull() {
        Annotated b = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        assertThat(b.equals(null), is(false));
    }
    
    @Test
    public void testAnnotationEqualityPositive() {
        Annotated b1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated b2 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(b1, is(b2));
    }
    
    @Test
    public void testAnnotationEqualityNonmatchingReference() {
        Annotated b1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated b2 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr2", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr2", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(b1.equals(b2), is(false));
    }
    
    @Test
    public void testAnnotationEqualityNonmatchingBlocks() {
        Annotated b1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 401, Strand.POSITIVE))
                .build();
        Annotated b2 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        assertThat(b1.equals(b2), is(false));
    }
    
    @Test
    public void testAnnotationEqualityNonmatchingStrand() {
        Annotated b1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build();
        Annotated b2 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.NEGATIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.NEGATIVE))
                .build();
        assertThat(b1.equals(b2), is(false));
    }
    
    @Test
    public void testAnnotationEqualityFromBuilder() {
        Annotated b1 = (new AnnotationBuilder())
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .build();
        Annotated b2 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        assertThat(b1, is(b2));
    }
    
    @Test
    public void testUpstreamOfPositiveStrandTrue() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr1", 0, 50, Strand.POSITIVE);
        assertThat(b2.isUpstreamOf(b1), is(true));
    }
    
    @Test
    public void testUpstreamOfPositiveStrandFalse() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr1", 0, 50, Strand.POSITIVE);
        assertThat(b1.isUpstreamOf(b2), is(false));
    }
    
    @Test
    public void testDownstreamOfPositiveStrandTrue() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr1", 0, 50, Strand.POSITIVE);
        assertThat(b2.isDownstreamOf(b1), is(false));
    }
    
    @Test
    public void testDownstreamOfPositiveStrandFalse() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr1", 0, 50, Strand.POSITIVE);
        assertThat(b1.isDownstreamOf(b2), is(true));
    }
    
    @Test
    public void testUpstreamOfAdjacentPositiveStrandTrue() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr1", 0, 100, Strand.POSITIVE);
        assertThat(b2.isUpstreamOf(b1), is(true));
    }
    
    @Test
    public void testUpstreamOfAdjacentPositiveStrandFalse() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr1", 0, 100, Strand.POSITIVE);
        assertThat(b1.isUpstreamOf(b2), is(false));
    }
    
    @Test
    public void testDownstreamOfAdjacentPositiveStrandTrue() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr1", 0, 100, Strand.POSITIVE);
        assertThat(b2.isDownstreamOf(b1), is(false));
    }
    
    @Test
    public void testDownstreamOfAdjacentPositiveStrandFalse() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        Annotated b2 = new Annotation("chr1", 0, 100, Strand.POSITIVE);
        assertThat(b1.isDownstreamOf(b2), is(true));
    }
    
    @Test
    public void testUpstreamOfNegativeStrandTrue() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        Annotated b2 = new Annotation("chr1", 0, 50, Strand.NEGATIVE);
        assertThat(b1.isUpstreamOf(b2), is(true));
    }
    
    @Test
    public void testUpstreamOfNegativeStrandFalse() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        Annotated b2 = new Annotation("chr1", 0, 50, Strand.NEGATIVE);
        assertThat(b2.isUpstreamOf(b1), is(false));
    }
    
    @Test
    public void testDownstreamOfNegativeStrandTrue() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        Annotated b2 = new Annotation("chr1", 0, 50, Strand.NEGATIVE);
        assertThat(b2.isDownstreamOf(b1), is(true));
    }
    
    @Test
    public void testDownstreamOfNegativeStrandFalse() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        Annotated b2 = new Annotation("chr1", 0, 50, Strand.NEGATIVE);
        assertThat(b1.isDownstreamOf(b2), is(false));
    }
    
    @Test
    public void testUpstreamOfAdjacentNegativeStrandTrue() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        Annotated b2 = new Annotation("chr1", 0, 100, Strand.NEGATIVE);
        assertThat(b1.isUpstreamOf(b2), is(true));
    }
    
    @Test
    public void testUpstreamOfAdjacentNegativeStrandFalse() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        Annotated b2 = new Annotation("chr1", 0, 100, Strand.NEGATIVE);
        assertThat(b2.isUpstreamOf(b1), is(false));
    }
    
    @Test
    public void testDownstreamOfAdjacentNegativeStrandTrue() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        Annotated b2 = new Annotation("chr1", 0, 100, Strand.NEGATIVE);
        assertThat(b2.isDownstreamOf(b1), is(true));
    }
    
    @Test
    public void testDownstreamOfAdjacentNegativeStrandFalse() {
        Annotated b1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        Annotated b2 = new Annotation("chr1", 0, 100, Strand.NEGATIVE);
        assertThat(b1.isDownstreamOf(b2), is(false));
    }

}
