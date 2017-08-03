package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.datastructures.Interval;
import edu.caltech.lncrna.bio.datastructures.RedBlackIntervalTree;
import edu.caltech.lncrna.bio.datastructures.SimpleInterval;
import edu.caltech.lncrna.bio.datastructures.SimpleIntervalTree;

public class TestSimpleIntervalTree {

    // An empty tree
    private SimpleIntervalTree<Interval> emptyTree;                 // an empty tree
    
    // A tree with one node: [0, 10)
    private SimpleIntervalTree<Interval> singletonTree;
    private Interval singletonValue = new SimpleInterval(0, 10);
    private Interval copyOfSingletonValue = new SimpleInterval(singletonValue);
    
    private SimpleIntervalTree<Interval> randomTree;
    private int randomUpperBound = 3000;
    private int numRandomIntervals = 5000;
    private Set<Interval> randomIntervals;
    
    // A tree with an empty region in the middle to test overlap methods
    private SimpleIntervalTree<Interval> gappedTree;
    private int gappedUpperBound = 3000;
    private int gappedLowerBound = 4000;
    private int numGappedIntervals = 2500;  // in each section
    private Set<Interval> gappedIntervals;
    
    
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
        
        mHasValidRedColoring = RedBlackIntervalTree.class.getDeclaredMethod("hasValidRedColoring");
        mHasValidRedColoring.setAccessible(true);
        
        mHasConsistentMaxEnds = RedBlackIntervalTree.class.getDeclaredMethod("hasConsistentMaxEnds");
        mHasConsistentMaxEnds.setAccessible(true);
        
        ///////////////////////
        // Initialize trees. //
        ///////////////////////
        
        emptyTree = new SimpleIntervalTree<Interval>();
        singletonTree = new SimpleIntervalTree<Interval>(singletonValue);
        
        randomTree = new SimpleIntervalTree<Interval>();
        randomIntervals = new TreeSet<Interval>();
        Random rand = new Random();
        for (int i = 0; i < numRandomIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(randomUpperBound);
                s = rand.nextInt(randomUpperBound);
            }
            
            randomIntervals.add(new SimpleInterval(r, s));
            randomTree.add(new SimpleInterval(r, s));
        }
        
        gappedTree = new SimpleIntervalTree<Interval>();
        gappedIntervals = new TreeSet<Interval>();
        for (int i = 0; i < numGappedIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(gappedUpperBound);
                s = rand.nextInt(gappedUpperBound);
            }
            
            gappedIntervals.add(new SimpleInterval(r, s));
            gappedTree.add(new SimpleInterval(r, s));
        }
        
        for (int i = 0; i < numGappedIntervals; i++) {
            int r = 0;
            int s = 0;
            while (s <= r) {
                r = rand.nextInt(gappedUpperBound) + gappedLowerBound;
                s = rand.nextInt(gappedUpperBound) + gappedLowerBound;
            }
            
            gappedIntervals.add(new SimpleInterval(r, s));
            gappedTree.add(new SimpleInterval(r, s));
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
        assertThat(emptyTree.contains(new SimpleInterval(1, 5)), is(false));
    }
    
    @Test
    public void testEmptyTreeMinimum() {
        assertThat(emptyTree.minimum().isPresent(), is(false));
    }
    
    @Test
    public void testEmptyTreeMaximum() {
        assertThat(emptyTree.maximum().isPresent(), is(false));
    }
    
    @Test
    public void testEmptyTreeSuccessor() {
        assertThat(emptyTree.successor(new SimpleInterval(1, 2)).isPresent(),
                is(false));
    }
    
    @Test
    public void testEmptyTreePredecessor() {
        assertThat(emptyTree.predecessor(new SimpleInterval(1, 2)).isPresent(),
                is(false));
    }
    
    @Test
    public void testEmptyTreeIteratorHasNext() {
        assertThat(emptyTree.iterator().hasNext(), is(false));
    }
    
    @Test
    public void testEmptyTreeIteratorNext() {
        thrown.expect(NoSuchElementException.class);
        thrown.expectMessage("Interval tree has no more elements.");
        emptyTree.iterator().next();
    }
    
    @Test
    public void testEmptyTreeOverlaps() {
        assertThat(emptyTree.overlaps(new SimpleInterval(1, 10)), is(false));
    }
    
    @Test
    public void testEmptyTreeOverlappersHasNext() {
        assertThat(emptyTree.overlappers(new SimpleInterval(1, 3)).hasNext(),
                is(false));
    }
    
    @Test
    public void testEmptyTreeOverlappersNext() {
        thrown.expect(NoSuchElementException.class);
        thrown.expectMessage("Interval tree has no more overlapping elements.");
        emptyTree.overlappers(new SimpleInterval(1, 3)).next();
    }
    
    @Test
    public void testEmptyTreeNumOverlappers() {
        assertThat(emptyTree.numOverlappers(new SimpleInterval(1, 3)), is(0));
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
        assertThat(emptyTree.remove(new SimpleInterval(1, 2)), is(false));
    }
    
    @Test
    public void testEmptyTreeSizeAfterDelete() {
        emptyTree.remove(new SimpleInterval(1, 2));
        assertThat(emptyTree.size(), is(0));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterDelete() {
        emptyTree.remove(new SimpleInterval(1, 2));
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeDeleteMin() {
        assertThat(emptyTree.removeMinimum(), is(false));
    }
    
    @Test
    public void testEmptyTreeSizeAfterDeleteMin() {
        emptyTree.removeMinimum();
        assertThat(emptyTree.size(), is(0));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterDeleteMin() {
        emptyTree.removeMinimum();
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeDeleteMax() {
        assertThat(emptyTree.removeMaximum(), is(false));
    }
    
    @Test
    public void testEmptyTreeSizeAfterDeleteMax() {
        emptyTree.removeMaximum();
        assertThat(emptyTree.size(), is(0));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterDeleteMax() {
        emptyTree.removeMaximum();
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeDeleteOverlappers() {
        emptyTree.removeOverlappers(new SimpleInterval(1, 2));
    }
    
    @Test
    public void testEmptyTreeSizeAfterDeleteOverlappers() {
        emptyTree.removeOverlappers(new SimpleInterval(1, 2));
        assertThat(emptyTree.size(), is(0));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterDeleteOverlappers() {
        emptyTree.removeOverlappers(new SimpleInterval(1, 2));
        assertThat(emptyTree.isEmpty(), is(true));
    }
    
    @Test
    public void testEmptyTreeIsValidBSTAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        emptyTree.remove(new SimpleInterval(1, 3));
        assertThat(mIsBST.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeIsBalancedAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        emptyTree.remove(new SimpleInterval(1, 3));
        assertThat(mIsBalanced.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeHasValidRedColoringAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        emptyTree.remove(new SimpleInterval(1, 3));
        assertThat(mHasValidRedColoring.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeConsistentMaxEndsAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        emptyTree.remove(new SimpleInterval(1, 3));
        assertThat(mHasConsistentMaxEnds.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeInsertion() {
        assertThat(emptyTree.add(new SimpleInterval(1, 3)), is(true));
    }
    
    @Test
    public void testEmptyTreeSizeAfterInsertion() {
        emptyTree.add(new SimpleInterval(1, 2));
        assertThat(emptyTree.size(), is(1));
    }
    
    @Test
    public void testEmptyTreeIsEmptyAfterInsertion() {
        emptyTree.add(new SimpleInterval(1, 2));
        assertThat(emptyTree.isEmpty(), is(false));
    }
    
    @Test
    public void testEmptyTreeIsValidBSTAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        emptyTree.add(new SimpleInterval(1, 3));
        assertThat(mIsBST.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeIsBalancedAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        emptyTree.add(new SimpleInterval(1, 3));
        assertThat(mIsBalanced.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeHasValidRedColoringAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        emptyTree.add(new SimpleInterval(1, 3));
        assertThat(mHasValidRedColoring.invoke(emptyTree), is(true));
    }
    
    @Test
    public void testEmptyTreeHasConsistentMaxEndsAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        emptyTree.add(new SimpleInterval(1, 3));
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
            
            emptyTree.add(new SimpleInterval(r, s));
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
            
            emptyTree.add(new SimpleInterval(r, s));
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
            
            emptyTree.add(new SimpleInterval(r, s));
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
            
            emptyTree.add(new SimpleInterval(r, s));
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
    public void testSingletonTreeContainsNegative() {
        assertThat(singletonTree.contains(new SimpleInterval(1, 9)), is(false));
    }
    
    @Test
    public void testSingletonTreeMinimum() {
        assertThat(singletonTree.minimum()
                                .orElseThrow(() -> new IllegalStateException()),
                is(copyOfSingletonValue));
    }
    
    @Test
    public void testSingletonTreeMaximum() {
        assertThat(singletonTree.maximum()
                                .orElseThrow(() -> new IllegalStateException()),
                is(copyOfSingletonValue));
    }
    
    @Test
    public void testSingletonTreeSuccessor() {
        assertThat(singletonTree.successor(copyOfSingletonValue).isPresent(),
                is(false));
    }
    
    @Test
    public void testSingetonTreePredecessor() {
        assertThat(singletonTree.predecessor(copyOfSingletonValue).isPresent(),
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
        thrown.expectMessage("Interval tree has no more elements.");
        Iterator<Interval> i = singletonTree.iterator();
        i.next();
        i.next();
    }
    
    @Test
    public void testSingletonTreeOverlapsPositive() {
        assertThat(singletonTree.overlaps(copyOfSingletonValue), is(true));
    }
    
    @Test
    public void testSingletonTreeOverlapsNegative() {
        assertThat(singletonTree.overlaps(new SimpleInterval(20, 22)), is(false));
    }
    
    @Test
    public void testSingletonTreeOverlapsAdjacent() {
        assertThat(singletonTree.overlaps(new SimpleInterval(10, 20)), is(false));
    }
    
    @Test
    public void testSingletonTreeOverlappersHasNext() {
        assertThat(singletonTree.overlappers(new SimpleInterval(1, 3)).hasNext(), is(true));
    }

    @Test
    public void testSingletonTreeOverlappersNext() {
        assertThat(singletonTree.overlappers(new SimpleInterval(1, 3)).next(), is(copyOfSingletonValue));
    }
    
    @Test
    public void testSingletonTreeOverlappersNextTwice() {
        thrown.expect(NoSuchElementException.class);
        thrown.expectMessage("Interval tree has no more overlapping elements.");
        Iterator<Interval> i = singletonTree.overlappers(new SimpleInterval(1, 3));
        i.next();
        i.next();
    }
    
    @Test
    public void testSingletonTreeNumOverlappers() {
        assertThat(singletonTree.numOverlappers(new SimpleInterval(1, 3)), is(1));
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
        assertThat(singletonTree.remove(new SimpleInterval(1, 5)), is(false));
    }
    
    @Test
    public void testSingletonTreeSizeAfterSuccessfulDeletion() {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(singletonTree.size(), is(0));
    }
    
    @Test
    public void testSingletonTreeSizeAfterUnsuccessfulDeletion() {
        singletonTree.remove(new SimpleInterval(1, 9));
        assertThat(singletonTree.size(), is(1));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterSuccessfulDeletion() {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(singletonTree.isEmpty(), is(true));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterUnsuccessfulDeletion() {
        singletonTree.remove(new SimpleInterval(1, 9));
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeDeleteMin() {
        assertThat(singletonTree.removeMinimum(), is(true));
    }
    
    @Test
    public void testSingletonTreeSizeAfterDeleteMin() {
        singletonTree.removeMinimum();
        assertThat(singletonTree.size(), is(0));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterDeleteMin() {
        singletonTree.removeMinimum();
        assertThat(singletonTree.isEmpty(), is(true));
    }
    
    @Test
    public void testSingletonTreeDeleteMax() {
        assertThat(singletonTree.removeMaximum(), is(true));
    }
    
    @Test
    public void testSingletonTreeSizeAfterDeleteMax() {
        singletonTree.removeMaximum();
        assertThat(singletonTree.size(), is(0));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterDeleteMax() {
        singletonTree.removeMaximum();
        assertThat(singletonTree.isEmpty(), is(true));
    }
    
    @Test
    public void testSingletonTreeDeleteOverlappers() {
        assertThat(singletonTree.removeOverlappers(new SimpleInterval(1, 5)),
                is(true));
    }
    
    @Test
    public void testSingletonTreeSizeAfterDeleteOverlappersPositive() {
        singletonTree.removeOverlappers(new SimpleInterval(1, 5));
        assertThat(singletonTree.size(), is(0));
    }
    
    @Test
    public void testSingletonTreeSizeAfterDeleteOverlappersNegative() {
        singletonTree.removeOverlappers(new SimpleInterval(20, 25));
        assertThat(singletonTree.size(), is(1));
    }
    
    @Test
    public void testSingletonTreeIsEmptyAfterDeleteOverlappers() {
        singletonTree.removeOverlappers(new SimpleInterval(1, 5));
        assertThat(singletonTree.isEmpty(), is(true));
    }
    
    @Test
    public void testSingletonTreeIsNotEmptyAfterDeleteOverlappers() {
        singletonTree.removeOverlappers(new SimpleInterval(20, 25));
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeIsValidBSTAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalancedAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.remove(copyOfSingletonValue);
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
    public void testSingletonTreeConsistentMaxEndsAfterDeletion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.remove(copyOfSingletonValue);
        assertThat(mHasConsistentMaxEnds.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeInsertion() {
        assertThat(singletonTree.add(new SimpleInterval(1, 11)), is(true));
    }
    
    @Test
    public void testSingletonTreeRedundantInsertion() {
        assertThat(singletonTree.add(copyOfSingletonValue), is(false));
    }
    
    @Test
    public void testSingletonTreeSizeAfterInsertion() {
        singletonTree.add(new SimpleInterval(1, 2));
        assertThat(singletonTree.size(), is(2));
    }
    
    @Test
    public void testSingletonTreeSizeAfterRedundantInsertion() {
        singletonTree.add(copyOfSingletonValue);
        assertThat(singletonTree.size(), is(1));
    }
    
    @Test
    public void testSingletonTreeIsNotEmptyAfterInsertion() {
        singletonTree.add(new SimpleInterval(1, 2));
        assertThat(singletonTree.isEmpty(), is(false));
    }
    
    @Test
    public void testSingletonTreeIsValidBSTAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.add(new SimpleInterval(1, 3));
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsValidBSTAfterRedundantInsertion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.add(copyOfSingletonValue);
        assertThat(mIsBST.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalancedAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.add(new SimpleInterval(1, 3));
        assertThat(mIsBalanced.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeIsBalancedAfterRedundantInsertion() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        singletonTree.add(copyOfSingletonValue);
        assertThat(mIsBalanced.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeHasValidRedColoringAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(new SimpleInterval(1, 3));
        assertThat(mHasValidRedColoring.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeHasValidRedColoringAfterRedundantInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(copyOfSingletonValue);
        assertThat(mHasValidRedColoring.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeConsistentMaxEndsAfterInsertion() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        singletonTree.add(new SimpleInterval(1, 3));
        assertThat(mHasConsistentMaxEnds.invoke(singletonTree), is(true));
    }
    
    @Test
    public void testSingletonTreeConsistentMaxEndsAfterRedundantInsertion() throws
    IllegalAccessException, IllegalArgumentException,
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
        randomTree.add(new SimpleInterval(1000, 2000));
        assertThat(randomTree.contains(new SimpleInterval(1000, 2000)),
                is(true));
    }
    
    @Test
    public void testRandomTreeMinimum() {
        Interval i = randomIntervals.iterator().next();
        assertThat(randomTree.minimum()
                .orElseThrow(() -> new IllegalStateException()),
                is(i));
    }
    
    @Test
    public void testRandomTreeMaximum() {
        Iterator<Interval> iter = randomIntervals.iterator();
        Interval i = null;
        while (iter.hasNext()) {
            i = iter.next();
        }
        assertThat(randomTree.maximum()
                .orElseThrow(() -> new IllegalStateException()),
                is(i));
    }
    
    @Test
    public void testRandomTreePredecessorOfMinimum() {
        assertThat(randomTree.minimum()
                             .flatMap(t -> randomTree.predecessor(t))
                             .isPresent(), is(false));
    }
    
    @Test
    public void testRandomTreeSuccessorOfMinimum() {
        Interval successor = randomTree.minimum()
                .flatMap(t -> randomTree.successor(t))
                .orElseThrow(() -> new IllegalStateException("Can't find successor"));
        Iterator<Interval> iter = randomIntervals.iterator();
        iter.next();
        assertThat(iter.next(), is(successor));
    }
    
    @Test
    public void testRandomTreeSuccessorOfMaximum() {
        assertThat(randomTree.maximum()
                .flatMap(t -> randomTree.successor(t))
                .isPresent(), is(false));
    }
    
    @Test
    public void testRandomTreePredecessorOfMaximum() {
        Interval predecessor = randomTree.maximum()
                .flatMap(t -> randomTree.predecessor(t))
                .orElseThrow(() -> new IllegalStateException("Can't find predecessor"));
        Iterator<Interval> iter = randomIntervals.iterator();
        Interval prev = iter.next();
        Interval curr = iter.next();
        while (iter.hasNext()) {
            prev = curr;
            curr = iter.next();
        }
        assertThat(prev, is(predecessor));
    }
    
    @Test
    public void testRandomTreeIteratorNumberOfElements() {

        long count = StreamSupport.stream(randomTree.spliterator(), false)
                .count();
        
        assertThat((long) randomIntervals.size(), is(count));
    }

    @Test
    public void testRandomTreeOverlapsPositive() {
        Interval cmp = new SimpleInterval(1000, 2000);
        // Not guaranteed to overlap, but unlikely not to
        
        assertThat(randomTree.overlaps(cmp), is(true));
    }
    
    @Test
    public void testRandomTreeOverlapsNegative1() {
        Interval cmp = new SimpleInterval(randomUpperBound,
                randomUpperBound + 1000);
        assertThat(randomTree.overlaps(cmp), is(false));
    }
    
    @Test
    public void testRandomTreeOverlapsNegative2() {
        Interval cmp = new SimpleInterval(-1000, 0);
        assertThat(randomTree.overlaps(cmp), is(false));
    }
    
    @Test
    public void testRandomTreeMinOverlapperPositive() {
        Interval cmp = new SimpleInterval(1000, 2000);
        
        Interval setMin = randomIntervals.stream()
                .filter(n -> n.overlaps(cmp))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Can't find any overlapper."));
        
        Interval treeMin = randomTree.minimumOverlapper(cmp)
                .orElseThrow(() -> new IllegalStateException("Can't find any overlapper."));

        assertThat(treeMin, is(setMin));
    }
    
    @Test
    public void testRandomTreeMinOverlapperNegative() {
        Interval cmp = new SimpleInterval(-1000, 0);
        assertThat(randomTree.minimumOverlapper(cmp).isPresent(), is(false));
    }

    @Test
    public void testRandomTreeNumOverlappers() {
        Interval i = new SimpleInterval(1000, 2000);

        long count = StreamSupport.stream(randomTree.spliterator(), false)
                .filter(n -> n.overlaps(i))
                .count();
        
        assertThat((long) randomTree.numOverlappers(i), is(count));
    }
    
    @Test
    public void testRandomTreeSizeAfterDeleteOverlappers() {
        Interval i = new SimpleInterval(1000, 2000);
        
        long initSize = randomTree.size();
        long count = StreamSupport.stream(randomTree.spliterator(), false)
                .filter(n -> n.overlaps(i))
                .count();
        
        randomTree.removeOverlappers(i);
        assertThat((long) randomTree.size(), is(initSize - count));
    }
    
    @Test
    public void testRandomTreeNoOverlappersAfterDeleteOverlappers() {
        Interval i = new SimpleInterval(1000, 2000);
        assertThat(randomTree.overlaps(i), is(true));
        
        randomTree.removeOverlappers(i);
        assertThat(randomTree.overlaps(i), is(false));

        for (Interval j : randomTree) {
            assertThat(j.overlaps(i), is(false));
        }
    }

    @Test
    public void testRandomTreeSizeAfterRepeatedDeletions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<Interval> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        int count = randomIntervalList.size();
        
        for (Interval i : randomIntervalList) {
            randomTree.remove(i);
            count--;
            assertThat(randomTree.size(), is(count));
            assertThat(randomTree.contains(i), is(false));
        }
        
        assertThat(randomTree.isEmpty(), is(true));
    }
    
    @Test
    public void testRandomTreeIsValidBSTAfterRepeatedDeletions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<Interval> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        
        for (Interval i : randomIntervalList) {
            randomTree.remove(i);
            assertThat(mIsBST.invoke(randomTree), is(true));
        }
    }
    
    @Test
    public void testRandomTreeIsBalancedAfterRepeatedDeletions() throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<Interval> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        
        for (Interval i : randomIntervalList) {
            randomTree.remove(i);
            assertThat(mIsBalanced.invoke(randomTree), is(true));
        }
    }
    
    @Test
    public void testRandomTreeHasValidRedColoringAfterRepeatedDeletions() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        List<Interval> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        
        for (Interval i : randomIntervalList) {
            randomTree.remove(i);
            assertThat(mHasValidRedColoring.invoke(randomTree), is(true));
        }
    }
    
    @Test
    public void testRandomTreeConsistentMaxEndsAfterRepeatedDeletions() throws
    IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        List<Interval> randomIntervalList = new ArrayList<>(randomIntervals);
        Collections.shuffle(randomIntervalList);
        
        for (Interval i : randomIntervalList) {
            randomTree.remove(i);
            assertThat(mHasConsistentMaxEnds.invoke(randomTree), is(true));
        }
    }
    
    ///////////////////////
    // Gapped tree tests //
    ///////////////////////

    @Test
    public void testGappedTreeOverlapsPositive() {
        assertThat(gappedTree.overlaps(new SimpleInterval(0, gappedUpperBound)),
                is(true));
        assertThat(gappedTree.overlaps(new SimpleInterval(gappedLowerBound,
                gappedUpperBound + gappedLowerBound)), is(true));
        assertThat(gappedTree.overlaps(new SimpleInterval(0, gappedUpperBound +
                gappedLowerBound)), is(true));
    }
    
    @Test
    public void testGappedTreeOverlapsNegative() {
        assertThat(gappedTree.overlaps(new SimpleInterval(gappedUpperBound,
                gappedLowerBound)), is(false));
    }
    
    @Test
    public void testGappedTreeDeleteOverlappersPositive() {
        Interval firstInterval = new SimpleInterval(0, gappedUpperBound);
        Interval secondInterval = new SimpleInterval(gappedLowerBound,
                gappedUpperBound + gappedLowerBound);
        boolean first = gappedTree.removeOverlappers(firstInterval);
        boolean second = gappedTree.removeOverlappers(secondInterval);
        assertThat(first && second, is(true));
    }
    
    @Test
    public void testGappedTreeDeleteOverlappersNegative() {
        Interval interval = new SimpleInterval(gappedUpperBound,
                gappedLowerBound);
        assertThat(gappedTree.removeOverlappers(interval), is(false));
    }
}