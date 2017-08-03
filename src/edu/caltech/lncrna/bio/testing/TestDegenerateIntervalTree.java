package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.datastructures.RedBlackIntervalTree;
import edu.caltech.lncrna.bio.datastructures.DegenerateIntervalTree;
import edu.caltech.lncrna.bio.datastructures.SimpleInterval;

public class TestDegenerateIntervalTree {

    // Intervals have ID numbers, [0, 5)
    private int intervalIdCap = 5;
    
    private DegenerateIntervalTree<Impl> emptyTree;
    
    // A tree with one node, [0, 10)
    private DegenerateIntervalTree<Impl> singletonTree;
    private Impl singletonValue = new Impl(0, 10);
    private Impl copyOfSingletonValue = new Impl(singletonValue);
    private Impl singletonValueDifferentId = new Impl(0, 10, 1);
    private Impl notSingletonValue = new Impl(0, 1);
    private Impl overlapsSingletonValue = new Impl(0, 3);
    private Impl adjacentSingletonValue = new Impl(singletonValue.getEnd(),
            singletonValue.getEnd() + 10);
    private Impl noOverlapSingletonValue = new Impl(20, 22);
    
    private DegenerateIntervalTree<Impl> randomTree;
    private int randomUpperBound = 100;
    private int numRandomIntervals = 2000;
    private Set<Impl> randomIntervals;
    private Impl notRandomValue = new Impl(5000, 10000);
    private Impl overlapsRandomTree = new Impl(20, 40);
    
    // A tree with a dead-zone in the middle to test overlap methods.
    private DegenerateIntervalTree<Impl> gappedTree;
    private int gappedUpperBound = 3000;
    private int gappedLowerBound = 4000;             
    private int numGappedIntervals = 2500;
    private Set<Impl> gappedIntervals;
   
    
    // Private debugging methods.
    private Method mIsBST;
    private Method mHasValidRedColoring;
    private Method mIsBalanced;
    private Method mHasConsistentMaxEnds;

    @Before
    public void setup() throws NoSuchMethodException, SecurityException,
    NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        /////////////////////////////////////////////////////////
        // Make private methods accessible for easier testing. //
        /////////////////////////////////////////////////////////
        
        mIsBST = RedBlackIntervalTree.class.getDeclaredMethod("isBST");
        mIsBST.setAccessible(true);
        
        mIsBalanced = RedBlackIntervalTree.class.getDeclaredMethod("isBalanced");
        mIsBalanced.setAccessible(true);
        
        mHasValidRedColoring = RedBlackIntervalTree.class
                .getDeclaredMethod("hasValidRedColoring");
        mHasValidRedColoring.setAccessible(true);
        
        mHasConsistentMaxEnds = RedBlackIntervalTree.class
                .getDeclaredMethod("hasConsistentMaxEnds");
        mHasConsistentMaxEnds.setAccessible(true);
        
        emptyTree = new DegenerateIntervalTree<Impl>();
        singletonTree = new DegenerateIntervalTree<Impl>(singletonValue);
        
        randomTree = new DegenerateIntervalTree<Impl>();
        randomIntervals = new HashSet<Impl>();
        
        // Initialize random tree with random intervals.
        // Randomize start, end and id fields.
        Random rand = new Random();
        for (int i = 0; i < numRandomIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(randomUpperBound);
                s = rand.nextInt(randomUpperBound);
            }
            int n = rand.nextInt(intervalIdCap);
            
            randomIntervals.add(new Impl(r, s, n));
            randomTree.add(new Impl(r, s, n));
        }
        
        gappedTree = new DegenerateIntervalTree<Impl>();
        gappedIntervals = new HashSet<Impl>();

        // Initialize first half of gapped tree
        for (int i = 0; i < numGappedIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(gappedUpperBound);
                s = rand.nextInt(gappedUpperBound);
            }
            int n = rand.nextInt(intervalIdCap);
            
            gappedIntervals.add(new Impl(r, s, n));
            gappedTree.add(new Impl(r, s, n));
        }
        
        // Initialize second half of gapped tree
        for (int i = 0; i < numGappedIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(gappedUpperBound) + gappedLowerBound;
                s = rand.nextInt(gappedUpperBound) + gappedLowerBound;
            }
            int n = rand.nextInt(intervalIdCap);
            
            gappedIntervals.add(new Impl(r, s, n));
            gappedTree.add(new Impl(r, s, n));
        }
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    //////////////////////
    // Empty tree tests //
    //////////////////////

    @Test
    public void testEmptyTreeIsEmpty() {
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeSize() {
        assertThat(emptyTree.size(), is(0));
    }

    @Test
    public void testEmptyTreeContains() {
        assertThat(emptyTree.contains(new Impl(1, 5)), is(false));
    }
    
    @Test
    public void testEmptyTreeMinima() {
        assertThat(emptyTree.minima().hasNext(), is(false));
    }
    
    @Test
    public void testEmptyTreeMaxima() {
        assertThat(emptyTree.maxima().hasNext(), is(false));
    }

    
    @Test
    public void testEmptyTreeSuccessor() {
        assertThat(emptyTree.successors(new Impl(1, 2)).hasNext(), is(false));
    }
    
    @Test
    public void testEmptyTreePredecessor() {
        assertThat(emptyTree.predecessors(new Impl(1, 2)).hasNext(), is(false));
    }
    
    @Test
    public void testEmptyTreeIteratorHasNext() {
        assertThat(emptyTree.iterator().hasNext(), is(false));
    }

    @Test
    public void testEmptyTreeIteratorNext() {
        thrown.expect(NoSuchElementException.class);
        emptyTree.iterator().next();
    }
    
    @Test
    public void testEmptyTreeOverlaps() {
        assertThat(emptyTree.overlaps(new Impl(1, 10)), is(false));
    }
    
    @Test
    public void testEmptyTreeOverlappersHasNext() {
        assertThat(emptyTree.overlappers(new Impl(1, 3)).hasNext(), is(false));
    }
    
    @Test
    public void testEmptyTreeOverlappersNext() {
        thrown.expect(NoSuchElementException.class);
        emptyTree.overlappers(new Impl(1, 3)).next();
    }
    
    @Test
    public void testEmptyTreeNumOverlappers() {
        assertThat(emptyTree.numOverlappers(new Impl(1, 3)), is(0));
    }

    @Test
    public void testEmptyTreeIsValidBST() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        assertThat(mIsBST.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeIsBalanced() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        assertThat(mIsBalanced.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeHasValidRedColoring() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        assertThat(mHasValidRedColoring.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeConsistentMaxEnds() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        assertThat(mHasConsistentMaxEnds.invoke(emptyTree), is(true));
    }

    @Test
    public void testEmptyTreeDelete() {
        assertThat(emptyTree.remove(new Impl(1, 2)), is(false));
    }
    
    @Test
    public void testEmptyTreeSizeAfterDelete() {
        emptyTree.remove(new Impl(1, 2));
        assertThat(emptyTree.size(), is(0));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterDelete() {
        emptyTree.remove(new Impl(1, 2));
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeDeleteMin() {
        assertThat(emptyTree.removeMinima(), is(false));
    }
    
    @Test
    public void testEmptyTreeSizeAfterDeleteMin() {
        emptyTree.removeMinima();
        assertThat(emptyTree.size(), is(0));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterDeleteMin() {
        emptyTree.removeMinima();
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeDeleteMax() {
        assertThat(emptyTree.removeMaxima(), is(false));
    }
    
    @Test
    public void testEmptyTreeSizeAfterDeleteMax() {
        emptyTree.removeMaxima();
        assertThat(emptyTree.size(), is(0));
    }

    @Test
    public void testEmptyTreeIsEmptyAfterDeleteMax() {
        emptyTree.removeMaxima();
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeDeleteOverlappers() {
        emptyTree.removeOverlappers(new Impl(1, 2));
    }
    
    @Test
    public void testEmptyTreeSizeAfterDeleteOverlappers() {
        emptyTree.removeOverlappers(new Impl(1, 2));
        assertThat(emptyTree.size(), is(0));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterDeleteOverlappers() {
        emptyTree.removeOverlappers(new Impl(1, 2));
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeIsValidBSTAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        emptyTree.remove(new Impl(1, 3));
        assertThat(mIsBST.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeIsBalancedAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        emptyTree.remove(new Impl(1, 3));
        assertThat(mIsBalanced.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeHasValidRedColoringAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        emptyTree.remove(new Impl(1, 3));
        assertThat(mHasValidRedColoring.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeConsistentMaxEndsAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        emptyTree.remove(new Impl(1, 3));
        assertThat(mHasConsistentMaxEnds.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeInsertion() {
        assertThat(emptyTree.add(new Impl(1, 3)), is(true));
    }
    
    @Test
    public void testEmptyTreeSizeAfterInsertion() {
        emptyTree.add(new Impl(1, 2));
        assertThat(emptyTree.size(), is(1));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterInsertion() {
        emptyTree.add(new Impl(1, 2));
        assertThat(emptyTree.isEmpty(), is(false));
    }
    
    @Test
    public void testEmptyTreeIsValidBSTAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        emptyTree.add(new Impl(1, 3));
        assertThat(mIsBST.invoke(emptyTree), is(true));
    }

    @Test
    public void testEmptyTreeIsBalancedAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        emptyTree.add(new Impl(1, 3));
        assertThat(mIsBalanced.invoke(emptyTree), is(true));
    }

    @Test
    public void testEmptyTreeHasValidRedColoringAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        emptyTree.add(new Impl(1, 3));
        assertThat(mHasValidRedColoring.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeHasConsistentMaxEndsAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        emptyTree.add(new Impl(1, 3));
        assertThat(mHasConsistentMaxEnds.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeIsValidBSTAfterRepeatedInsertions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Random rand = new Random();
        for (int i = 0; i < numRandomIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(randomUpperBound);
                s = rand.nextInt(randomUpperBound);
            }
            
            emptyTree.add(new Impl(r, s));
            assertThat(mIsBST.invoke(emptyTree), is(true));
        }
    }
    
    @Test
    public void testEmptyTreeIsBalancedAfterRepeatedInsertions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Random rand = new Random();
        for (int i = 0; i < numRandomIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(randomUpperBound);
                s = rand.nextInt(randomUpperBound);
            }
            
            emptyTree.add(new Impl(r, s));
            assertThat(mIsBalanced.invoke(emptyTree), is(true));
        }
    }
    
    @Test
    public void testEmptyTreeHasValidRedColoringAfterRepeatedInsertions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Random rand = new Random();
        for (int i = 0; i < numRandomIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(randomUpperBound);
                s = rand.nextInt(randomUpperBound);
            }
            
            emptyTree.add(new Impl(r, s));
            assertThat(mHasValidRedColoring.invoke(emptyTree), is(true));
        }
    }
    
    @Test
    public void testEmptyTreeHasConsistentMaxEndsAfterRepeatedInsertions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Random rand = new Random();
        for (int i = 0; i < numRandomIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(randomUpperBound);
                s = rand.nextInt(randomUpperBound);
            }
            
            emptyTree.add(new Impl(r, s));
            assertThat(mHasConsistentMaxEnds.invoke(emptyTree), is(true));
        }
    }
    

    //////////////////////////
    // Singleton tree tests //
    //////////////////////////
    
    @Test
    public void testSingletonTreeIsEmpty() {
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeSize() {
        assertThat(singletonTree.size(), is(1));
    }
    
    @Test
    public void testSingletonTreeContainsPositive() {
        assertThat(singletonTree.contains(copyOfSingletonValue), is(true));
    }
    
    @Test
    public void testSingletonTreeContainsDifferentBoundsNegative() {
        assertThat(singletonTree.contains(notSingletonValue), is(false));
    }
    
    @Test
    public void testSingletonTreeContainsSameBoundsNegative() {
        assertThat(singletonTree.contains(singletonValueDifferentId), is(false));
    }
    
    @Test
    public void testSingletonTreeMinima() {
        assertThat(singletonTree.minima().next(),
                is(copyOfSingletonValue));
    }
    
    @Test
    public void testSingletonTreeOnlyOneMinima() {
        Iterator<Impl> i = singletonTree.minima();
        i.next();
        assertThat(i.hasNext(), is(false));
    }
    
    @Test
    public void testSingletonTreeMaxima() {
        assertThat(singletonTree.maxima().next(),
                is(copyOfSingletonValue));
    }
    
    @Test
    public void testSingletonTreeOnlyOneMaxima() {
        Iterator<Impl> i = singletonTree.maxima();
        i.next();
        assertThat(i.hasNext(), is(false));
    }

    @Test
    public void testSingletonTreeSuccessor() {
        assertThat(singletonTree.successors(copyOfSingletonValue).hasNext(),
                is(false));
    }
    
    @Test
    public void testSingetonTreePredecessor() {
        assertThat(singletonTree.predecessors(copyOfSingletonValue).hasNext(),
                is(false));
    }
    
    @Test
    public void testSingletonTreeIteratorHasNext() {
        assertThat(singletonTree.iterator().hasNext(), is(true));
    }
    
    @Test
    public void testSingletonTreeIteratorNext() {
        assertThat(singletonTree.iterator().next(), is(copyOfSingletonValue));
    }
    
    @Test
    public void testSingletonTreeIteratorNextTwice() {
        thrown.expect(NoSuchElementException.class);
        Iterator<Impl> i = singletonTree.iterator();
        i.next();
        i.next();
    }
    
    @Test
    public void testSingletonTreeOverlapsPositive() {
        assertThat(singletonTree.overlaps(copyOfSingletonValue), is(true));
    }
    
    @Test
    public void testSingletonTreeOverlapsDifferentIdPositive() {
        assertThat(singletonTree.overlaps(singletonValueDifferentId),
                is(true));
    }
     
    @Test
    public void testSingletonTreeOverlapsNegative() {
        assertThat(singletonTree.overlaps(noOverlapSingletonValue), is(false));
    }
    
    @Test
    public void testSingletonTreeOverlapsAdjacent() {
        assertThat(singletonTree.overlaps(adjacentSingletonValue), is(false));
    }
    
    @Test
    public void testSingletonTreeOverlappersHasNext() {
        assertThat(singletonTree.overlappers(overlapsSingletonValue).hasNext(),
                is(true));
    }

    @Test
    public void testSingletonTreeOverlappersNext() {
        assertThat(singletonTree.overlappers(overlapsSingletonValue).next(),
                is(copyOfSingletonValue));
    }
    
    @Test
    public void testSingletonTreeOverlappersNextTwice() {
        thrown.expect(NoSuchElementException.class);
        Iterator<Impl> i = singletonTree.overlappers(overlapsSingletonValue);
        i.next();
        i.next();
    }
    
    @Test
    public void testSingletonTreeNumOverlappers() {
        assertThat(singletonTree.numOverlappers(overlapsSingletonValue),
                is(1));
    }
    
    @Test
    public void testSingletonTreeIsValidBST() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalanced() throws IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {
        assertThat(mIsBalanced.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeHasValidRedColoring() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        assertThat(mHasValidRedColoring.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeConsistentMaxEnds() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        assertThat(mHasConsistentMaxEnds.invoke(singletonTree), is(true));
    }

    @Test
    public void testSingletonTreeDeletePositive() {
        assertThat(singletonTree.remove(copyOfSingletonValue), is(true));
    }
    
    @Test
    public void testSingletonTreeDeleteNegative() {
        assertThat(singletonTree.remove(new Impl(1, 5)), is(false));
    }
    
    @Test
    public void testSingletonTreeDeleteDifferentIdNegative() {
        assertThat(singletonTree.remove(singletonValueDifferentId), is(false));
    }
    
    @Test
    public void testSingletonTreeSizeAfterSuccessfulDeletion() {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(singletonTree.size(), is(0));
    }
    
    @Test
    public void testSingletonTreeSizeAfterUnsuccessfulDeletion() {
        singletonTree.remove(noOverlapSingletonValue);
        assertThat(singletonTree.size(), is(1));
    }
    
    @Test
    public void testSingletonTreeSizeAfterUnsuccessfulDeletionDifferentId() {
        singletonTree.remove(singletonValueDifferentId);
        assertThat(singletonTree.size(), is(1));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterSuccessfulDeletion() {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(singletonTree.isEmpty(), is(true));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterUnsuccessfulDeletion() {
        singletonTree.remove(noOverlapSingletonValue);
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterUnsuccessfulDeletionDifferentId() {
        singletonTree.remove(singletonValueDifferentId);
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeDeleteMinima() {
        assertThat(singletonTree.removeMinima(), is(true));
    }
    
    @Test
    public void testSingletonTreeSizeAfterDeleteMin() {
        singletonTree.removeMinima();
        assertThat(singletonTree.size(), is(0));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterDeleteMin() {
        singletonTree.removeMinima();
        assertThat(singletonTree.isEmpty(), is(true));
    }
    
    @Test
    public void testSingletonTreeDeleteMax() {
        assertThat(singletonTree.removeMaxima(), is(true));
    }
    
    @Test
    public void testSingletonTreeSizeAfterDeleteMax() {
        singletonTree.removeMaxima();
        assertThat(singletonTree.size(), is(0));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterDeleteMax() {
        singletonTree.removeMaxima();
        assertThat(singletonTree.isEmpty(), is(true));
    }
    
    @Test
    public void testSingletonTreeDeleteOverlappersPositive() {
        assertThat(singletonTree.removeOverlappers(overlapsSingletonValue),
                is(true));
    }
    
    @Test
    public void testSingletonTreeDeleteOverlappersNegative() {
        assertThat(singletonTree.removeOverlappers(noOverlapSingletonValue),
                is(false));
    }
    
    @Test
    public void testSingletonTreeSizeAfterDeleteOverlappersPositive() {
        singletonTree.removeOverlappers(overlapsSingletonValue);
        assertThat(singletonTree.size(), is(0));
    }
    
    @Test
    public void testSingletonTreeSizeAfterDeleteOverlappersNegative() {
        singletonTree.removeOverlappers(noOverlapSingletonValue);
        assertThat(singletonTree.size(), is(1));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterDeleteOverlappers() {
        singletonTree.removeOverlappers(overlapsSingletonValue);
        assertThat(singletonTree.isEmpty(), is(true));
    }
    
    @Test
    public void testSingletonTreeIsNotEmptyAfterDeleteOverlappers() {
        singletonTree.removeOverlappers(noOverlapSingletonValue);
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeIsValidBSTAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsValidBSTAfterFailedDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.remove(singletonValueDifferentId);
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalancedAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(mIsBalanced.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalancedAfterFailedDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.remove(singletonValueDifferentId);
        assertThat(mIsBalanced.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeHasValidRedColoringAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(mHasValidRedColoring.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeHasValidRedColoringAfterFailedDeletion()
    throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.remove(singletonValueDifferentId);
        assertThat(mHasValidRedColoring.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeConsistentMaxEndsAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(mHasConsistentMaxEnds.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeConsistentMaxEndsAfterFailedDeletion()
    throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.remove(singletonValueDifferentId);
        assertThat(mHasConsistentMaxEnds.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeInsertion() {
        assertThat(singletonTree.add(noOverlapSingletonValue), is(true));
    }
    
    @Test
    public void testSingletonTreeRedundantInsertion() {
        assertThat(singletonTree.add(copyOfSingletonValue), is(false));
    }
    
    @Test
    public void testSingletonTreeInsertionDifferentId() {
        assertThat(singletonTree.add(singletonValueDifferentId), is(true));
    }
    
    @Test
    public void testSingletonTreeSizeAfterInsertion() {
        singletonTree.add(noOverlapSingletonValue);
        assertThat(singletonTree.size(), is(2));
    }
    
    @Test
    public void testSingletonTreeSizeAfterRedundantInsertion() {
        singletonTree.add(copyOfSingletonValue);
        assertThat(singletonTree.size(), is(1));
    }
    
    @Test
    public void testSingletonTreeSizeAfterInsertionDifferentId() {
        singletonTree.add(singletonValueDifferentId);
        assertThat(singletonTree.size(), is(2));
    }
    
    @Test
    public void testSingletonTreeIsNotEmptyAfterInsertion() {
        singletonTree.add(noOverlapSingletonValue);
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeIsValidBSTAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(noOverlapSingletonValue);
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsValidBSTAfterInsertionDifferentId() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(singletonValueDifferentId);
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsValidBSTAfterRedundantInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(copyOfSingletonValue);
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalancedAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(noOverlapSingletonValue);
        assertThat(mIsBalanced.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalancedAfterInsertionDifferentId() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(singletonValueDifferentId);
        assertThat(mIsBalanced.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalancedAfterRedundantInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(copyOfSingletonValue);
        assertThat(mIsBalanced.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeHasValidRedColoringAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(noOverlapSingletonValue);
        assertThat(mHasValidRedColoring.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeHasValidRedColoringAfterInsertionDifferentId()
    throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(singletonValueDifferentId);
        assertThat(mHasValidRedColoring.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeHasValidRedColoringAfterRedundantInsertion()
    throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(copyOfSingletonValue);
        assertThat(mHasValidRedColoring.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeConsistentMaxEndsAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(noOverlapSingletonValue);
        assertThat(mHasConsistentMaxEnds.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeConsistentMaxEndsAfterInsertionDifferentId()
    throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(singletonValueDifferentId);
        assertThat(mHasConsistentMaxEnds.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeConsistentMaxEndsAfterRedundantInsertion()
    throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(copyOfSingletonValue);
        assertThat(mHasConsistentMaxEnds.invoke(singletonTree), is(true));
    }
    
    ///////////////////////
    // Random tree tests //
    ///////////////////////
    
    @Test
    public void testRandomTreeIsNotEmpty() {
        assertThat(randomTree.isEmpty(), is(false));
    }
    
    @Test
    public void testRandomTreeSize() {
        assertThat(randomTree.size(), is(randomIntervals.size()));
    }
    
    @Test
    public void testRandomTreeContainsPositive() {
        randomTree.add(new Impl(1000, 2000));
        assertThat(randomTree.contains(new Impl(1000, 2000)), is(true));
    }
    
    @Test
    public void testRandomTreeContainsNegative() {
        assertThat(randomTree.contains(notRandomValue), is(false));
    }
    
    @Test
    public void testRandomTreeContainsAllIntervals() {
        for (Impl i : randomIntervals) {
            assertThat(randomTree.contains(i), is(true));
        }
    }
    
    @Test
    public void testRandomTreeMinimum() {
        Set<Impl> treeMins = new HashSet<>();
        randomTree.minima().forEachRemaining(treeMins::add);

        Impl firstTreeMin = treeMins.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("treeMins is empty"));
        
        Set<Impl> setMins = randomIntervals.stream()
                .filter((i) -> i.compareTo(firstTreeMin) == 0)
                .collect(Collectors.toCollection(HashSet::new));
        
        assertThat(setMins, is(treeMins));
    }
    
    @Test
    public void testRandomTreeMaximum() {
        Set<Impl> treeMaxes = new HashSet<>();
        randomTree.maxima().forEachRemaining(treeMaxes::add);

        Impl firstTreeMax = treeMaxes.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("treeMaxes is empty"));
        
        Set<Impl> setMaxes = randomIntervals.stream()
                .filter((i) -> i.compareTo(firstTreeMax) == 0)
                .collect(Collectors.toCollection(HashSet::new));
        
        assertThat(setMaxes, is(treeMaxes));
    }
    
    @Test
    public void testRandomTreePredecessorOfMinimum() {
        Impl minimum = randomTree.minima().next();
        assertThat(randomTree.predecessors(minimum).hasNext(), is(false));
    }
    
    @Test
    public void testRandomTreePredecessorOfMaximum() {
        Set<Impl> treeMaxes = new HashSet<>();
        randomTree.maxima().forEachRemaining(treeMaxes::add);

        Impl firstTreeMax = treeMaxes.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("treeMaxes is empty"));
        
        Set<Impl> treePreds = new HashSet<>();
        randomTree.predecessors(firstTreeMax).forEachRemaining(treePreds::add);
        
        Impl firstTreePred = treePreds.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("treePreds is empty"));
        
        Set<Impl> setPreds = randomIntervals.stream()
                .filter((i) -> i.compareTo(firstTreePred) == 0)
                .collect(Collectors.toCollection(HashSet::new));
        
        assertThat(setPreds, is(treePreds));
    }
    
    @Test
    public void testRandomTreeSuccessorOfMaximum() {
        Impl maximum = randomTree.maxima().next();
        assertThat(randomTree.successors(maximum).hasNext(), is(false));
    }
    
    @Test
    public void testRandomTreeSuccessorOfMinimum() {
        Set<Impl> treeMins = new HashSet<>();
        randomTree.minima().forEachRemaining(treeMins::add);

        Impl firstTreeMin = treeMins.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("treeMins is empty"));
        
        Set<Impl> treeSuccs = new HashSet<>();
        randomTree.successors(firstTreeMin).forEachRemaining(treeSuccs::add);
        
        Impl firstTreeSucc = treeSuccs.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("treeSuccs is empty"));
        
        Set<Impl> setSuccs = randomIntervals.stream()
                .filter((i) -> i.compareTo(firstTreeSucc) == 0)
                .collect(Collectors.toCollection(HashSet::new));
        
        assertThat(setSuccs, is(treeSuccs));
    }
    
    @Test
    public void testRandomTreeIteratorNumberOfElements() {

        long count = StreamSupport.stream(randomTree.spliterator(), false)
                .count();
        
        assertThat((long) randomIntervals.size(), is(count));
    }

    @Test
    public void testRandomTreeIterable() {
        Set<Impl> s = new HashSet<>();
        randomTree.iterator().forEachRemaining(s::add);
        assertThat(s, is(randomIntervals));
    }
    
    @Test
    public void testRandomTreeOverlapsPositive() {        
        assertThat(randomTree.overlaps(overlapsRandomTree), is(true));
    }
    
    @Test
    public void testRandomTreeOverlapsNegative1() {
        Impl cmp = new Impl(randomUpperBound, randomUpperBound + 1000);
        assertThat(randomTree.overlaps(cmp), is(false));
    }
    
    @Test
    public void testRandomTreeOverlapsNegative2() {
        Impl cmp = new Impl(-1000, 0);
        assertThat(randomTree.overlaps(cmp), is(false));
    }
    
    @Test
    public void testRandomTreeMinOverlapperPositive() {

        Impl setMin = randomIntervals.stream()
                .filter(n -> n.overlaps(overlapsRandomTree))
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalStateException("Can't find any overlapper."));
        
        Set<Impl> setMins = randomIntervals.stream()
                .filter(n -> n.compareTo(setMin) == 0)
                .collect(Collectors.toCollection(HashSet::new));
        
        Set<Impl> treeMins = new HashSet<>();
        randomTree.minimumOverlappers(overlapsRandomTree)
                .forEachRemaining(treeMins::add);

        assertThat(treeMins, is(setMins));
    }
    
    @Test
    public void testRandomTreeMinOverlapperNegative() {
        Impl cmp = new Impl(-1000, 0);
        assertThat(randomTree.minimumOverlappers(cmp).hasNext(), is(false));
    }

    @Test
    public void testRandomTreeNumOverlappers() {
        Impl i = new Impl(1000, 2000);

        long count = StreamSupport.stream(randomTree.spliterator(), false)
                .filter(n -> n.overlaps(i))
                .count();
        
        assertThat((long) randomTree.numOverlappers(i), is(count));
    }
    
    @Test
    public void testRandomTreeSizeAfterDeleteOverlappers() {
        Impl i = new Impl(1000, 2000);
        
        long initSize = randomTree.size();
        long count = StreamSupport.stream(randomTree.spliterator(), false)
                .filter(n -> n.overlaps(i))
                .count();
        
        randomTree.removeOverlappers(i);
        assertThat((long) randomTree.size(), is(initSize - count));
    }
    
    @Test
    public void testRandomTreeNoOverlappersAfterDeleteOverlappers() {

        assertThat(randomTree.overlaps(overlapsRandomTree), is(true));
        
        randomTree.removeOverlappers(overlapsRandomTree);
        assertThat(randomTree.overlaps(overlapsRandomTree), is(false));

        for (Impl j : randomTree) {
            assertThat(j.overlaps(overlapsRandomTree), is(false));
        }
    }

    @Test
    public void testRandomTreeSizeAfterRepeatedDeletions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<Impl> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
          
        int count = randomIntervalList.size();        
        assertThat(randomTree.size(), is(count));
                
        for (Impl i : randomIntervalList) {
            if (randomTree.remove(i)) {
                count--;
            }
            assertThat(randomTree.size(), is(count));
        }
        assertThat(randomTree.isEmpty(), is(true));
    }
    
    @Test
    public void testRandomTreeIsValidBSTAfterRepeatedDeletions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<Impl> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        
        for (Impl i : randomIntervalList) {
            randomTree.remove(i);
            assertThat(mIsBST.invoke(randomTree), is(true));
        }
    }
    
    @Test
    public void testRandomTreeIsBalancedAfterRepeatedDeletions() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        List<Impl> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        
        for (Impl i : randomIntervalList) {
            randomTree.remove(i);
            assertThat(mIsBalanced.invoke(randomTree), is(true));
        }
    }
    
    @Test
    public void testRandomTreeHasValidRedColoringAfterRepeatedDeletions()
    throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        List<Impl> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        
        for (Impl i : randomIntervalList) {
            randomTree.remove(i);
            assertThat(mHasValidRedColoring.invoke(randomTree), is(true));
        }
    }
    
    @Test
    public void testRandomTreeConsistentMaxEndsAfterRepeatedDeletions()
    throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        List<Impl> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        
        for (Impl i : randomIntervalList) {
            randomTree.remove(i);
            assertThat(mHasConsistentMaxEnds.invoke(randomTree), is(true));
        }
    }
    
    ///////////////////////
    // Gapped tree tests //
    ///////////////////////

    @Test
    public void testGappedTreeOverlapsPositive() {
        assertThat(gappedTree.overlaps(new Impl(0, gappedUpperBound)),
                is(true));
        assertThat(gappedTree.overlaps(new Impl(gappedLowerBound,
                gappedUpperBound + gappedLowerBound)), is(true));
        assertThat(gappedTree.overlaps(new Impl(0, gappedUpperBound +
                gappedLowerBound)), is(true));
    }
    
    @Test
    public void testGappedTreeOverlapsNegative() {
        assertThat(gappedTree.overlaps(new Impl(gappedUpperBound,
                gappedLowerBound)), is(false));
    }
    
    @Test
    public void testGappedTreeDeleteOverlappersPositive() {
        Impl firstInterval = new Impl(0, gappedUpperBound);
        Impl secondInterval = new Impl(gappedLowerBound, gappedUpperBound +
                gappedLowerBound);
        boolean first = gappedTree.removeOverlappers(firstInterval);
        boolean second = gappedTree.removeOverlappers(secondInterval);
        assertThat(first && second, is(true));
    }
    
    @Test
    public void testGappedTreeDeleteOverlappersNegative() {
        Impl interval = new Impl(gappedUpperBound, gappedLowerBound);
        assertThat(gappedTree.removeOverlappers(interval), is(false));
    }
   
    /**
     * Simple implementation of an interval with an ID field
     */
    private static class Impl extends SimpleInterval {

        private final int id;
        
        public Impl(int start, int end) {
            super(start, end);
            this.id = 0;  // Default ID is 0
        }
        
        public Impl(int start, int end, int id) {
            super(start, end);
            this.id = id;
        }
        
        public Impl(Impl i) {
            super(i.getStart(), i.getEnd());
            this.id = i.id;
        }
        
        @Override
        public String toString() {
            return "start: " + this.getStart() + " end: " + this.getEnd() +
                    " id: " + id;
        }
        
        @Override
        public boolean equals(Object other) {

            if (!(other instanceof Impl)) {
                return false;
            }

            return super.equals(other) &&
                   id == ((Impl) other).id;
        }
        
        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + id;
            return result;
        }
    }
}