package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.datastructures.GenomeTree;
import edu.caltech.lncrna.bio.io.BedParser;

public class TestGenomeTree {

    private GenomeTree<Annotated> emptyTree;
    
    private GenomeTree<Annotated> singletonTree;
    private Annotated singletonValue = new Annotation("chr1", 500, 1000, Strand.POSITIVE);
    
    private GenomeTree<Annotated> fullTree;
    
    private final static Path BED = Paths.get("/Users/masonmlai/Documents/" +
            "Repositories/GuttmanLab/testing/refseq_mm9.bed");
    private final static int NUM_BED_RECORDS = 29564;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Before
    public void setup() {
        emptyTree = new GenomeTree<>();
        
        singletonTree = new GenomeTree<>();
        singletonTree.insert(singletonValue);
        
        fullTree = new GenomeTree<>();
        try (BedParser bp = new BedParser(BED)) {
            bp.stream().forEach(x -> fullTree.insert(x));
        }
    }
    
    //////////////////////
    // Empty tree tests //
    //////////////////////
    
    @Test
    public void testEmptyTreeIsEmpty() {
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeSize() {
        assertThat(emptyTree.getSize(), is(0));
    }
    
    @Test
    public void testEmptyTreeIteratorHasNoNextElement() {
        assertThat(emptyTree.iterator().hasNext(), is(false));
    }
    
    @Test
    public void testEmptyTreeIteratorNextThrowsExceptions() {
        thrown.expect(NoSuchElementException.class);
        emptyTree.iterator().next();
    }
    
    @Test
    public void testEmptyTreeStreamIsEmpty() {
        assertThat(emptyTree.stream().count(), is(0L));
    }
    
    @Test
    public void testEmptyTreeOverlapsIsFalse() {
        assertThat(emptyTree.overlaps(singletonValue), is(false));
    }
    
    @Test
    public void testEmptyTreeNumOverlappersIsZero() {
        assertThat(emptyTree.getNumOverlappers(singletonValue), is(0));
    }
    
    @Test
    public void testEmptyTreeOverlapperIteratorIsEmpty() {
        assertThat(emptyTree.getOverlappers(singletonValue).hasNext(), is(false));
    }
    
    @Test
    public void testEmptyTreeOverlapperIteratorNextThrowsException() {
        thrown.expect(NoSuchElementException.class);
        emptyTree.getOverlappers(singletonValue).next();
    }
    
    @Test
    public void testEmptyTreeAnyGeneBodyOverlapsIsFalse() {
        assertThat(emptyTree.anyGeneBodyOverlaps(singletonValue), is(false));
    }
    
    //////////////////////////
    // Singleton tree tests //
    //////////////////////////
    
    @Test
    public void testSingletonTreeIsNotEmpty() {
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeSize() {
        assertThat(singletonTree.getSize(), is(1));
    }
    
    @Test
    public void testSingletonTreeIteratorNextElement() {
        Iterator<Annotated> iter = singletonTree.iterator();
        assertThat(iter.hasNext(), is(true));
        iter.next();
        assertThat(iter.hasNext(), is(false));
    }

    @Test
    public void testSingletonStreamHasOneElement() {
        assertThat(singletonTree.stream().count(), is(1L));
    }
    
    @Test
    public void testSingletonTreeOverlapsIsTrue() {
        assertThat(singletonTree.overlaps(singletonValue), is(true));
    }
    
    @Test
    public void testSingletonTreeNumOverlappersIsOne() {
        assertThat(singletonTree.getNumOverlappers(singletonValue), is(1));
    }
    
    @Test
    public void testSingletonTreeAnyGeneBodyOverlapsIsTrue() {
        assertThat(singletonTree.anyGeneBodyOverlaps(singletonValue), is(true));
    }
    
    ///////////////////////
    // Simple tree tests //
    ///////////////////////
    
    @Test
    public void testTreeOverlapPositive() {
        GenomeTree<Annotated> tree = new GenomeTree<>();
        tree.insert(new Annotation("chr1", 100, 200, Strand.POSITIVE));
        tree.insert(new Annotation("chr1", 300, 400, Strand.POSITIVE));
        Annotated testAnnot1 = new Annotation("chr1", 100, 200, Strand.POSITIVE);
        assertThat(tree.overlaps(testAnnot1), is(true));
        assertThat(tree.getNumOverlappers(testAnnot1), is(1));
        Annotated testAnnot2 = new Annotation("chr1", 150, 350, Strand.POSITIVE);
        assertThat(tree.overlaps(testAnnot2), is(true));
        assertThat(tree.getNumOverlappers(testAnnot2), is(2));
        Annotated testAnnot3 = new Annotation("chr1", 50, 400, Strand.POSITIVE);
        assertThat(tree.overlaps(testAnnot3), is(true));
        assertThat(tree.getNumOverlappers(testAnnot3), is(2));
    }
    
    @Test
    public void testTreeOverlapDifferentChromosomeIsNegative() {
        GenomeTree<Annotated> tree = new GenomeTree<>();
        tree.insert(new Annotation("chr1", 100, 200, Strand.POSITIVE));
        tree.insert(new Annotation("chr1", 300, 400, Strand.POSITIVE));
        Annotated testAnnot1 = new Annotation("chr2", 100, 200, Strand.POSITIVE);
        assertThat(tree.overlaps(testAnnot1), is(false));
        assertThat(tree.getNumOverlappers(testAnnot1), is(0));
        Annotated testAnnot2 = new Annotation("chr2", 150, 350, Strand.POSITIVE);
        assertThat(tree.overlaps(testAnnot2), is(false));
        assertThat(tree.getNumOverlappers(testAnnot2), is(0));
        Annotated testAnnot3 = new Annotation("chr2", 50, 400, Strand.POSITIVE);
        assertThat(tree.overlaps(testAnnot3), is(false));
        assertThat(tree.getNumOverlappers(testAnnot3), is(0));
    }
    
    @Test
    public void testTreeOverlapDifferentStrandIsFalse() {
        GenomeTree<Annotated> tree = new GenomeTree<>();
        tree.insert(new Annotation("chr1", 100, 200, Strand.POSITIVE));
        tree.insert(new Annotation("chr1", 300, 400, Strand.POSITIVE));
        Annotated testAnnot1 = new Annotation("chr1", 100, 200, Strand.NEGATIVE);
        assertThat(tree.overlaps(testAnnot1), is(false));
        assertThat(tree.getNumOverlappers(testAnnot1), is(0));
        Annotated testAnnot2 = new Annotation("chr1", 150, 350, Strand.NEGATIVE);
        assertThat(tree.overlaps(testAnnot2), is(false));
        assertThat(tree.getNumOverlappers(testAnnot2), is(0));
        Annotated testAnnot3 = new Annotation("chr1", 50, 400, Strand.NEGATIVE);
        assertThat(tree.overlaps(testAnnot3), is(false));
        assertThat(tree.getNumOverlappers(testAnnot3), is(0));
    }
    
    @Test
    public void testTreeOverlapIntronIsFalse() {
        GenomeTree<Annotated> tree = new GenomeTree<>();
        tree.insert(Annotation.builder()
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build());
        Annotated testAnnot1 = new Annotation("chr1", 200, 300, Strand.POSITIVE);
        assertThat(tree.overlaps(testAnnot1), is(false));
        assertThat(tree.getNumOverlappers(testAnnot1), is(0));
    }
    
    @Test
    public void testTreeGeneBodyOverlapIntronIsTrue() {
        GenomeTree<Annotated> tree = new GenomeTree<>();
        tree.insert(Annotation.builder()
                .addAnnotation(new Annotation("chr1", 100, 200, Strand.POSITIVE))
                .addAnnotation(new Annotation("chr1", 300, 400, Strand.POSITIVE))
                .build());
        Annotated testAnnot1 = new Annotation("chr1", 200, 300, Strand.POSITIVE);
        assertThat(tree.anyGeneBodyOverlaps(testAnnot1), is(true));
    }
    
    /////////////////////////
    // BED file tree tests //
    /////////////////////////
    
    @Test
    public void testFullTreeIsNotEmpty() {
        assertThat(fullTree.isEmpty(), is(false));
    }
    
    @Test
    public void testFullTreeSize() {
        assertThat(fullTree.getSize(), is(NUM_BED_RECORDS));
    }
}