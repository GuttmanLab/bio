package edu.caltech.lncrna.bio.datastructures;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class represents an interval tree which stores {@link Interval}
 * objects.
 * <p>
 * This tree can store multiple intervals with the same coordinates, as long as
 * those intervals differ in some manner according to their <code>equals</code>
 * method. An attempt to add a second, truly equal interval will simply leave
 * the tree unchanged.
 * <p>
 * This tree should not be used to store genomic intervals unless you can
 * restrict the intervals to a single chromosome. See {@link GenomeTree} for a
 * tree-like structure that can handle alignments and annotations.
 *
 * @param <T> - the type of <code>Interval</code> contained in this tree
 */
public final class DegenerateIntervalTree<T extends Interval> 
extends RedBlackIntervalTree<T, IntervalSet<T>> {
    
    /**
     * Class constructor.
     * <p>
     * Constructs an empty tree.
     */
    public DegenerateIntervalTree() {
        super();
    }
    
    /**
     * Class constructor.
     * <p>
     * Constructs a tree containing the single, specified interval.
     * 
     * @param t - the interval to add to the tree
     */
    public DegenerateIntervalTree(T t) {
        super(t);
    }
    
    @Override
    protected RedBlackNode<T, IntervalSet<T>> getNewNilInstance() {
        return new Node();
    }

    @Override
    protected RedBlackNode<T, IntervalSet<T>> getNewNodeInstance(T t) {
        return new Node(t);
    }
    
    @Override
    public boolean contains(Object o) {
        if (o instanceof Interval) {
            RedBlackNode<T, IntervalSet<T>> node = search((Interval) o);
            return node.data.contains(o);
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new TreeIterator(root);
    }

    @Override
    public <U> U[] toArray(U[] a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T e) {
        RedBlackNode<T, IntervalSet<T>> matchingNode = search(e);
        if (matchingNode.isNil()) {
            // Size update for a new node should happen in insertNode
            return insertNode(new Node(e));
        } else {
            boolean addSuccessful = matchingNode.data.add(e);
            if (addSuccessful) size++;
            return addSuccessful;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Interval) {
            RedBlackNode<T, IntervalSet<T>> node = search((Interval) o);
            boolean rtrn = node.data.remove(o);
            if (rtrn) {
                size--;
                // Size update also occurs in deleteNode, but node.size() == 0
                if (node.data.isEmpty()) deleteNode(node);
            }
            return rtrn;
        } else {
            return false;
        }
    }

    @Override
    public Iterator<T> overlappers(Interval i) {
        return root.overlappers(i);
    }
    
    @Override
    public boolean overlaps(Interval i) {
        return !root.anyOverlappingNode(i).isNil();
    }
    
    @Override
    public int numOverlappers(Interval i) {
        return root.numOverlappingNodes(i);
    }
    
    //////////////////////
    // Deletion methods //
    //////////////////////
    
    public boolean removeMinimum() {
        return removeMinima();
    }
    
    public boolean removeMaximum() {
        return removeMaxima();
    }
    
    /**
     * Deletes all intervals from this tree that overlap the given interval.
     * <p>
     * If there are no overlapping intervals, this tree remains unchanged.
     *
     * @param t - the overlapping interval
     * @return whether or not an interval was removed from this tree
     */
    @Override
    public boolean removeOverlappers(Interval i) {
        // TODO 
        // Replacing the line
        //    s.forEach(n -> remove(n.data()))
        // with
        //    s.forEach(n -> deleteNode(n))
        // causes a NullPointerException in resetMaxEnd(). Why?!
        //
        // As it stands, every deletion operation causes the tree
        // to be searched. Fix this, please.

        Set<T> s = new HashSet<>();
        Iterator<T> iter = new OverlapperIterator(root, i);
        iter.forEachRemaining(s::add);
        return s.stream()
                .map(x -> remove(x))
                .reduce(false, (a, b) -> a || b);
    }
    
    ////////////////
    // Node class //
    ////////////////
    
    private final class Node extends RedBlackNode<T, IntervalSet<T>> {

        private Node() {
            super();
            data = new IntervalSet<T>();
        }
        
        private Node(T t) {
            super(new IntervalSet<T>(t));
        }
        
        public Iterator<T> iterator() {
            return data.iterator();
        }
        
        @Override
        protected Iterator<T> overlappers(Interval i) {
            return new OverlapperIterator(this, i);
        }

        @Override
        protected int numOverlappingNodes(Interval i) {
            int count = 0;
            Iterator<RedBlackNode<T, IntervalSet<T>>> iter =
                    new OverlappingNodeIterator(this, i);
            
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
            return count;
        }
        
        @Override
        protected RedBlackNode<T, IntervalSet<T>> nil() {
            return nil;
        }
        
        @Override
        protected int size() {
            return data.size();
        }
    }
    
    ///////////////
    // Iterators //
    ///////////////
    
    /**
     * An iterator that returns intervals from this interval tree in ascending
     * order.
     * <p>
     * This class simply wraps a <code>TreeNodeIterator</code> and extracts each
     * node's interval.
     */
    private class TreeIterator implements Iterator<T> {
        
        private Iterator<T> iter;
        private RedBlackNode<T, IntervalSet<T>> currentNode;
        private RedBlackNode<T, IntervalSet<T>> nextNode;
        
        /**
         * Constructor.
         * <p>
         * Construct an iterator over the subtree rooted at the given node.
         * 
         * @param root - the root of the subtree
         */
        private TreeIterator(RedBlackNode<T, IntervalSet<T>> root) {
            currentNode = root.minimumNode();
            nextNode = currentNode.successor();
            iter = currentNode.iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext() || !nextNode.isNil();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Interval tree has no more elements.");
            }
            if (iter.hasNext()) {
                return iter.next();
            } else {
                currentNode = nextNode;
                nextNode = currentNode.successor();
                iter = currentNode.iterator();
                return iter.next();
            }
        }
    }
    
    /**
     * An iterator which returns only intervals of this tree that overlap a
     * specified interval.
     * <p>
     * The overlapping intervals are returned in ascending order.
     * <p>
     * This class simply wraps an {@link OverlappingNodeIterator} and extracts
     * each node's interval.
     */
    private class OverlapperIterator implements Iterator<T> {
        
        private Iterator<T> iter;
        private RedBlackNode<T, IntervalSet<T>> currentNode;
        private RedBlackNode<T, IntervalSet<T>> nextNode;
        private final Interval interval;
        
        /**
         * Constructor.
         * <p>
         * Construct an iterator over overlapping intervals of the subtree
         * rooted at the given node.
         * 
         * @param root - the root of the subtree
         * @param interval - the interval that the intervals must overlap
         */
        private OverlapperIterator(RedBlackNode<T, IntervalSet<T>> root,
                Interval i) {

            interval = i;
            currentNode = root.minimumOverlappingNode(interval);
            nextNode = currentNode.nextOverlappingNode(interval);
            iter = currentNode.iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext() || !nextNode.isNil();
        }

        @Override
        public T next() {
            if (iter.hasNext()) {
                return iter.next();
            } else {
                currentNode = nextNode;
                nextNode = currentNode.nextOverlappingNode(interval);
                iter = currentNode.iterator();
                return iter.next();
            }
        }
    }
}
