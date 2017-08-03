package edu.caltech.lncrna.bio.datastructures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class represents an interval tree which stores simple {@link Interval}
 * objects.
 * <p>
 * This tree does not store multiple intervals with the same coordinates, even
 * if those intervals differ by an unrelated property such as a name or ID
 * number. An attempt to add a second such interval will simply leave the tree
 * unchanged.
 * <p>
 * The intervals stored in this tree should be contiguous. It is possible to
 * store gapped intervals in this tree, but the presence of an interval with
 * a start value of 5 and an end value of 10 will prevent other [5, 10)
 * intervals from being added, regardless of any difference in gaps.
 * <p>
 * See {@link DegenerateIntervalTree} for a tree structure that can handle
 * gapped intervals and intervals with identical bounds. See {@link GenomeTree}
 * for a tree-like structure that can handle alignments and annotations.
 *
 * @param <T> - the type of <code>Interval</code> contained in this tree
 */
public final class SimpleIntervalTree<T extends Interval> 
extends RedBlackIntervalTree<T, T> {
    
    /**
     * Class constructor.
     * <p>
     * Constructs an empty tree.
     */
    public SimpleIntervalTree() {
        super();
    }
    
    /**
     * Class constructor.
     * <p>
     * Constructs a tree containing the single, specified interval.
     * 
     * @param t - the interval to add to the tree
     */
    public SimpleIntervalTree(T t) {
        super(t);
    }
    
    @Override
    protected RedBlackNode<T, T> getNewNilInstance() {
        return new Node();
    }

    @Override
    protected RedBlackNode<T, T> getNewNodeInstance(T t) {
        return new Node(t);
    }
    
    @Override
    public boolean contains(Object o) {
        if (o instanceof Interval) {
            RedBlackNode<T, T> node = search((Interval) o);
            if (o.equals(node.getData())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
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
        return insertNode(new Node(e));
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Interval) {
            RedBlackNode<T, T> node = search((Interval) o);
            if (o.equals(node.getData())) {
                deleteNode(node);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * Finds the element in this tree with the same bounds as the given
     * interval, removes it from the tree, and returns it.
     * <p>
     * If no such element is found, this method returns an empty
     * <code>Optional</code> instance.
     * 
     * @param interval - the interval with matching start and end coordinate
     * @return the corresponding element, wrapped in an <code>Optional</code>
     * if present; otherwise an empty <code>Optional</code>
     * @see #popSameBounds(int, int)
     */
    public Optional<T> popSameBounds(Interval interval) {
        return popNode(search(interval));
    }
    
    /**
     * Finds the element in this tree with the given start and end coordinate,
     * removes it from the tree, and returns it.
     * <p>
     * If no such element is found, this method returns an empty
     * <code>Optional</code> instance.
     * 
     * @param start - the start coordinate
     * @param end - the end coordinate
     * @return the corresponding element, wrapped in an <code>Optional</code>
     * if present; otherwise an empty <code>Optional</code>
     * @see #popSameBounds(Interval)
     */
    public Optional<T> popSameBounds(int start, int end) {
        return popNode(search(start, end));
    }
    
    /**
     * Removes the given node from the tree and returns its data.
     * <p>
     * If the node is the sentinel node, does nothing and returns an empty
     * <code>Optional</code> instance.
     * 
     * @param node - the node to remove
     * @return the data of the removed node, wrapped in an
     * <code>Optional</code>
     */
    private Optional<T> popNode(RedBlackNode<T, T> node) {
        if (!node.isNil()) {
            T rtrn = node.getData();
            deleteNode(node);
            return Optional.of(rtrn);
        }
        return Optional.empty();
    }
    
    /**
     * Returns the minimum element of this tree.
     * <p>
     * If there is no minimum element (this is, if this tree is empty),
     * this method returns an empty <code>Optional</code>.
     * 
     * @return the minimum element of this tree, wrapped in an
     * <code>Optional</code>
     */
    public Optional<T> minimum() {
        RedBlackNode<T, T> n = root.minimumNode();
        return n.isNil() ? Optional.empty() : Optional.of(n.data);
    }

    /**
     * Returns the maximum element of this tree.
     * <p>
     * If there is no maximum element (this is, if this tree is empty),
     * this method returns an empty <code>Optional</code>.
     * 
     * @return the maximum element of this tree, wrapped in an
     * <code>Optional</code>
     */
    public Optional<T> maximum() {
        RedBlackNode<T, T> n = root.maximumNode();
        return n.isNil() ? Optional.empty() : Optional.of(n.data);
    }
    
    // TODO Should this method search for the next element regardless of
    // whether i exists?
    /**
     * This method will return an empty <code>Optional</code> if the argument
     * interval does not exist in the tree. 
     * 
     * @param i - the preceding interval
     * @return the next element in this tree, wrapped in an
     * <code>Optional</code>.
     */
    public Optional<T> successor(Interval i) {
        RedBlackNode<T, T> n = search(i);
        if (n.isNil()) {
            return Optional.empty();
        }

        n = n.successor();
        if (n.isNil()) {
            return Optional.empty();
        }

        return Optional.of(n.data);
    }

    // TODO Should this method search for the previous element regardless of
    // whether i exists?
    /**
     * This method will return an empty <code>Optional</code> if the specified
     * interval does not exist in the tree. 
     * 
     * @param i - the following interval
     * @return the previous element in this tree, wrapped in an
     * <code>Optional</code>.
     */
    public Optional<T> predecessor(Interval i) {
        RedBlackNode<T, T> n = search(i);
        if (n.isNil()) {
            return Optional.empty();
        }

        n = n.predecessor();
        if (n.isNil()) {
            return Optional.empty();
        }

        return Optional.of(n.data);
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
    
    public Optional<T> minimumOverlapper(Interval i) {
        RedBlackNode<T, T> n = root.minimumOverlappingNode(i);
        return n.isNil() ? Optional.empty() : Optional.of(n.data);
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

        Set<RedBlackNode<T, T>> s = new HashSet<>();
        Iterator<RedBlackNode<T, T>> iter = new OverlappingNodeIterator(root, i);
        iter.forEachRemaining(s::add);
        return s.stream()
                .map(n -> remove(n.data))
                .reduce(false, (a, b) -> a || b);
    }
    
    ////////////////
    // Node class //
    ////////////////
    
    private final class Node extends RedBlackNode<T, T> {
        
        public final static int NODE_SIZE = 1;

        private Node() {
            super();
        }
        
        private Node(T t) {
            super(t);
        }
        
        @Override
        public Iterator<T> iterator() {
            List<T> list = new ArrayList<>();
            list.add(data);
            return list.iterator();
        }
        
        @Override
        protected Iterator<T> overlappers(Interval i) {
            return new OverlapperIterator(this, i);
        }

        @Override
        protected int numOverlappingNodes(Interval i) {
            int count = 0;
            Iterator<RedBlackNode<T, T>> iter = new OverlappingNodeIterator(this, i);
            
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
            return count;
        }
        
        @Override
        protected RedBlackNode<T, T> nil() {
            return nil;
        }
        
        @Override
        protected int size() {
            return NODE_SIZE;
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
        
        private TreeNodeIterator nodeIter;
        
        /**
         * Constructor.
         * <p>
         * Construct an iterator over the subtree rooted at the given node.
         * 
         * @param root - the root of the subtree
         */
        private TreeIterator(RedBlackNode<T, T> root) {
            nodeIter = new TreeNodeIterator(root);
        }

        @Override
        public boolean hasNext() {
            return nodeIter.hasNext();
        }

        @Override
        public T next() {
            return nodeIter.next().data;
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
        
        private OverlappingNodeIterator nodeIter;
        
        /**
         * Constructor.
         * <p>
         * Construct an iterator over overlapping intervals of the subtree
         * rooted at the given node.
         * 
         * @param root - the root of the subtree
         * @param interval - the interval that the intervals must overlap
         */
        private OverlapperIterator(RedBlackNode<T, T> root, Interval i) {
            nodeIter = new OverlappingNodeIterator(root, i);
        }

        @Override
        public boolean hasNext() {
            return nodeIter.hasNext();
        }

        @Override
        public T next() {
            return nodeIter.next().data;
        }
    }
}
