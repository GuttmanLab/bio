package edu.caltech.lncrna.bio.datastructures;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A self-balancing binary-search tree that contains {@link Interval} objects.
 * <p>
 * The underlying data-structure is a red-black tree largely implemented from
 * CLRS with the interval-tree extensions mentioned in section 14.3
 * <p>
 * This class is defined by two different type variables, <code>T</code> and
 * <code>U</code>. The first, <code>T</code>, represents the type of interval
 * contained in this tree. This type should be obvious, and is analogous to the
 * <code>T</code> in <code>List&lt;T&gt;</code>. The second, <code>U</code>,
 * represents the type contained internally in each tree node. For example, if
 * a node can store multiple intervals in an internal set, <code>U</code> might
 * be <code>IntervalSet&lt;T&gt;</code>. Classes which extend
 * <code>IntervalTree</code> should define <code>U</code> in terms of
 * <code>T</code> so that this variable is invisible to the user.
 * 
 * @param <T> - the type of interval that this tree contains.
 * @param <U> - the type of interval that each node contains
 * @see Cormen, Thomas H.; Leiserson, Charles E.; Rivest, Ronald L.; Stein,
 * Clifford (2001) [1990]. Introduction to Algorithms (2nd ed.). MIT Press and
 * McGraw-Hill.
 */
public abstract class RedBlackIntervalTree<T extends Interval, U extends Interval>
implements IntervalTree<T> {

    protected RedBlackNode<T, U> root;
    protected final RedBlackNode<T, U> nil;
    protected int size;
    
    /**
     * Class constructor.
     * <p>
     * Constructs an empty <code>IntervalTree</code> instance.
     */
    public RedBlackIntervalTree() {
        nil = getNewNilInstance();
        root = nil;
        size = 0;
    }
    
    /**
     * Class constructor.
     * <p>
     * Constructs an <code>IntervalTree</code> instance containing a single
     * interval.
     * 
     * @param t - the interval to put in this tree
     */
    public RedBlackIntervalTree(T t) {
        nil = getNewNilInstance();
        root = getNewNodeInstance(t);
        root.blacken();
        size = 1;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public Object[] toArray() {
        Object[] rtrn = new Object[size];
        Iterator<T> elements = iterator();
        int idx = 0;
        while (idx < size) rtrn[idx] = elements.next();
        return rtrn;
    }
    
    /**
     * Constructs a new instance of a sentinel node for this type of interval
     * tree.
     * 
     * @return a new sentinel node instance
     */
    protected abstract RedBlackNode<T, U> getNewNilInstance();
    
    /**
     * Constructs a new instance of an internal node, containing the given
     * element, for this type of interval tree.
     * 
     * @param t - the element to store in the returned node
     * @return a new node instance
     */
    protected abstract RedBlackNode<T, U> getNewNodeInstance(T t);
    
    ///////////////////
    // Query methods //
    ///////////////////
    
    /**
     * Returns the node in this tree matching the given interval.
     * <p>
     * The returned node will not necessarily contain the given interval. The
     * only guarantee is that the start and end coordinates match.
     * <p>
     * This method returns the sentinel node if a matching node cannot be
     * found.
     * 
     * @param i - the interval to search for
     * @return the matching node
     */
    protected RedBlackNode<T, U> search(Interval i) {
        return root.search(i);
    }
    
    /**
     * Returns the node in this tree matching the given start and end
     * coordinates.
     * <p>
     * This method returns the sentinel node if a matching node cannot be
     * found.
     * 
     * @param start - the start coordinate
     * @param end - the end coordinate
     * @return the matching node
     */
    protected RedBlackNode<T, U> search(int start, int end) {
        return root.search(start, end);
    }
    
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Iterator<T> minima() {
        RedBlackNode<T, U> n = root.minimumNode();
        return n.isNil() ? Collections.emptyIterator() : n.iterator();
    }

    @Override
    public Iterator<T> maxima() {
        RedBlackNode<T, U> n = root.maximumNode();
        return n.isNil() ? Collections.emptyIterator() : n.iterator();
    }

    @Override
    public Iterator<T> minimumOverlappers(Interval i) {
        RedBlackNode<T, U> n = root.minimumOverlappingNode(i);
        return n.isNil() ? Collections.emptyIterator() : n.iterator();
    }
    
    @Override
    public Iterator<T> successors(Interval i) {
        RedBlackNode<T, U> n = search(i);
        if (n.isNil()) {
            return Collections.emptyIterator();
        }

        n = n.successor();
        if (n.isNil()) {
            return Collections.emptyIterator();
        }

        return n.iterator();
    }

    public Iterator<T> predecessors(Interval i) {
        RedBlackNode<T, U> n = search(i);
        if (n.isNil()) {
            return Collections.emptyIterator();
        }

        n = n.predecessor();
        if (n.isNil()) {
            return Collections.emptyIterator();
        }

        return n.iterator();
    }

    ///////////////////////
    // Insertion methods //
    ///////////////////////
    
    @Override
    public boolean addAll(Collection<? extends T> elements) {
        boolean rtrn = false;
        for (T element : elements) {
            rtrn = rtrn || add(element);
        }
        return rtrn;
    }
    
    /**
     * Inserts a node into this tree.
     * <p>
     * If a node already exists with the same start and end values, this method
     * does nothing.
     * 
     * @param node - the node to insert
     * @return <code>true</code> if the node was successfully inserted;
     * <code>false</code> if the tree already contains a node with the same
     * coordinates
     */
    protected boolean insertNode(RedBlackNode<T, U> node) {
        
        RedBlackNode<T, U> z = node;
        RedBlackNode<T, U> y = nil;
        RedBlackNode<T, U> x = root;

        while (!x.isNil()) {                         // Traverse the tree down to a leaf.
            y = x;
            x.maxEnd = Math.max(x.maxEnd, z.maxEnd); // Update maxEnd on the way down.
            int cmp = z.compareTo(x);
            if (cmp == 0) {
                return false;                        // Value already in tree. Do nothing.
            }
            x = cmp == -1 ? x.left : x.right;
        }

        z.parent = y;
       
        if (y.isNil()) {
            root = z;
            root.blacken();
        } else {                      // Set the parent of n.
            int cmp = z.compareTo(y);
            if (cmp == -1) {
                y.left = z;
            } else {
                assert(cmp == 1);
                y.right = z;
            }
            
            z.left = nil;
            z.right = nil;
            z.redden();
            insertFixup(z);
        }

        size += node.size();
        return true;
    }
    
    /**
     * Ensures that red-black constraints and interval-tree constraints are
     * maintained after an insertion.
     * 
     * @param z - start maintenance at this node
     */
    protected void insertFixup(RedBlackNode<T, U> z) {
        while (z.parent.isRed()) {
            if (z.parent.isLeftChild()) {
                RedBlackNode<T, U> y = z.parent.parent.right;
                if (y.isRed()) {
                    z.parent.blacken();
                    y.blacken();
                    z.grandparent().redden();
                    z = z.grandparent();
                } else {
                    if (z.isRightChild()) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.blacken();
                    z.grandparent().redden();
                    rightRotate(z.grandparent());
                }
            } else {
                RedBlackNode<T, U> y = z.grandparent().left;
                if (y.isRed()) {
                    z.parent.blacken();
                    y.blacken();
                    z.grandparent().redden();
                    z = z.grandparent();
                } else {
                    if (z.isLeftChild()) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.blacken();
                    z.grandparent().redden();
                    leftRotate(z.grandparent());
                }
            }
        }
        root.blacken();
    }
    
    //////////////////////
    // Deletion methods //
    //////////////////////
    
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean rtrn = false;
        for (Object element : c) {
            rtrn = rtrn || remove(element);
        }
        return rtrn;
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        root = nil;
        size = 0;
    }

    @Override
    public boolean removeMinima() {                // Does nothing and returns
        return deleteNode(root.minimumNode());     // false if node is nil
    }

    @Override
    public boolean removeMaxima() {                // Does nothing and returns
        return deleteNode(root.maximumNode());     // false if node is nil.
    }
    
    /**
     * Deletes a node from this tree.
     * <p>
     * More specifically, removes the data held within this node from the
     * tree. Depending on the structure of the tree at this node, this
     * particular node instance may not be removed; rather, a different node
     * may be deleted and that node's contents copied into this one,
     * overwriting the previous contents.
     * 
     * @return <code>true</code> if the node is successfully deleted;
     * otherwise (if the node is the sentinel node) <code>false</code>
     */
    protected boolean deleteNode(RedBlackNode<T, U> node) {
        
        if (node.isNil()) {  // Can't delete the sentinel node.
            return false;
        }
        
        // Adjust size now. If we wait until after copyData is colled,
        // node.size may have been updated.
        size -= node.size();
        
        RedBlackNode<T, U> y = node;

        if (node.hasTwoChildren()) { // If the node to remove has two children,
            y = node.successor();    // copy the successor's data into it and
            node.copyData(y);        // remove the successor. The successor is
            maxEndFixup(node);       // guaranteed to both exist and have at most
        }                            // one child, so we've converted the two-
                                     // child case to a one- or no-child case.
        
        
        RedBlackNode<T, U> x = y.left.isNil() ? y.right : y.left;

        x.parent = y.parent;

        if (y.isRoot()) {
            root = x;
        } else if (y.isLeftChild()) {
            y.parent.left = x;
            maxEndFixup(y);
        } else {
            y.parent.right = x;
            maxEndFixup(y);
        }
        
        if (y.isBlack) {
            deleteFixup(x);
        }

        return true;
    }
    
    /**
     * Ensures that red-black constraints and interval-tree constraints are
     * maintained after deletion.
     * <p>
     * @param node - start maintenance at this node
     */
    private void deleteFixup(RedBlackNode<T, U> node) {
        RedBlackNode<T, U> x = node;
        while (!x.isRoot() && x.isBlack) {
            if (x.isLeftChild()) {
                RedBlackNode<T, U> w = x.parent.right;
                if (w.isRed()) {
                    w.blacken();
                    x.parent.redden();
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (w.left.isBlack && w.right.isBlack) {
                    w.redden();
                    x = x.parent;
                } else {
                    if (w.right.isBlack) {
                        w.left.blacken();
                        w.redden();
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.isBlack = x.parent.isBlack;
                    x.parent.blacken();
                    w.right.blacken();
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                RedBlackNode<T, U> w = x.parent.left;
                if (w.isRed()) {
                    w.blacken();
                    x.parent.redden();
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (w.left.isBlack && w.right.isBlack) {
                    w.redden();
                    x = x.parent;
                } else {
                    if (w.left.isBlack) {
                        w.right.blacken();
                        w.redden();
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.isBlack = x.parent.isBlack;
                    x.parent.blacken();
                    w.left.blacken();
                    rightRotate(x.parent);
                    x = root;
                }                    
            }
        }
        x.blacken();
    }
    
    //////////////////////////
    // Other fix-up methods //
    //////////////////////////
    
    /**
     * Performs a left-rotation on a node.
     * 
     * @param z - the node to rotate about
     * @see - Cormen, Thomas H.; Leiserson, Charles E.; Rivest, Ronald L.; Stein,
     * Clifford (2001) [1990]. Introduction to Algorithms (2nd ed.). MIT Press and
     * McGraw-Hill. pp. 277-279.
     */
    private void leftRotate(RedBlackNode<T, U> z) {
        RedBlackNode<T, U> y = z.right;
        z.right = y.left;

        if (!y.left.isNil()) {
            y.left.parent = z;
        }
        
        y.parent = z.parent;
        
        if (z.parent.isNil()) {
            root = y;
        } else if (z.isLeftChild()) {
            z.parent.left = y;
        } else {
            z.parent.right = y;
        }
        
        y.left = z;
        z.parent = y;
        
        z.resetMaxEnd();
        y.resetMaxEnd();
    }
    
    /**
     * Performs a right-rotation on a node.
     * 
     * @param z - the node to rotate about
     * @see - Cormen, Thomas H.; Leiserson, Charles E.; Rivest, Ronald L.; Stein,
     * Clifford (2001) [1990]. Introduction to Algorithms (2nd ed.). MIT Press and
     * McGraw-Hill. pp. 277-279.
     */
    private void rightRotate(RedBlackNode<T, U> z) {
        RedBlackNode<T, U> y = z.left;
        z.left = y.right;

        if (!y.right.isNil()) {
            y.right.parent = z;
        }
        
        y.parent = z.parent;
        
        if (z.parent.isNil()) {
            root = y;
        } else if (z.isLeftChild()) {
            z.parent.left = y;
        } else {
            z.parent.right = y;
        }
        
        y.right = z;
        z.parent = y;
        
        z.resetMaxEnd();
        y.resetMaxEnd();
    }
    
    /**
     * Sets the <code>maxEnd</code> of a node, and all nodes up to the
     * root of this tree, to the correct value.
     * 
     * @param n - start maintenance from this node
     * @see RedBlackNode#resetMaxEnd
     */
    private void maxEndFixup(RedBlackNode<T, U> n) {
        n.resetMaxEnd();
        while (!n.parent.isNil()) {
            n = n.parent;
            n.resetMaxEnd();
        }
    }
    
    ///////////////
    // Iterators //
    ///////////////
    
    /**
     * An iterator that returns the nodes of this interval tree in ascending
     * order.
     */
    final class TreeNodeIterator implements Iterator<RedBlackNode<T, U>> {

        private RedBlackNode<T, U> next;
        
        /**
         * Class constructor.
         * <p>
         * Construct an iterator over the subtree rooted at the given node.
         * 
         * @param root - the root of the subtree
         */
        TreeNodeIterator(RedBlackNode<T, U> root) {
            next = root.minimumNode();
        }
        
        @Override
        public boolean hasNext() {
            return !next.isNil();
        }

        @Override 
        public RedBlackNode<T, U> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Interval tree has no more elements.");
            }
            RedBlackNode<T, U> rtrn = next;
            next = rtrn.successor();
            return rtrn;
        }   
    }
    
    /**
     * An iterator that returns only the nodes of this tree that overlap a
     * specified interval.
     * <p>
     * The overlapping nodes are returned in ascending order.
     */
    protected final class OverlappingNodeIterator implements Iterator<RedBlackNode<T, U>> {
        
        private RedBlackNode<T, U> next;
        private final Interval interval;

        /**
         * Class constructor.
         * <p>
         * Construct an iterator over overlapping nodes of the subtree rooted
         * at the given node.
         * 
         * @param root - the root of the subtree
         * @param interval - the interval that the nodes must overlap
         */
        protected OverlappingNodeIterator(RedBlackNode<T, U> root, Interval interval) {
            this.interval = interval;
            next = root.minimumOverlappingNode(this.interval);
        }
        
        @Override
        public boolean hasNext() {
            return !next.isNil();
        }
        
        @Override
        public RedBlackNode<T, U> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Interval tree has no more overlapping elements.");
            }
            RedBlackNode<T, U> rtrn = next;
            next = rtrn.nextOverlappingNode(interval);
            return rtrn;
        }
    }


    
    ///////////////////////////////
    // Tree -- Debugging methods //
    ///////////////////////////////
    
    /**
     * Returns <code>false</code> if any node in this tree is less than its
     * left child or greater than its right child.
     * <p>
     * This method is used for debugging only, and its access is changed in
     * testing.
     * 
     * @return whether or not this interval tree is a valid binary-search tree.
     */
    @SuppressWarnings("unused")
    private boolean isBST() {
        return root.isBST(null, null);
    }

    /**
     * Returns <code>false</code> if all of the branches of this tree (from
     * root to leaf) do not contain the same number of black nodes.
     * (Specifically, the black-number of each branch is compared against the
     * black-number of the left-most branch.)
     * <p>
     * This method is used for debugging only, and its access is changed in
     * testing.
     * 
     * @return whether or not this interval tree is balanced
     */
    @SuppressWarnings("unused")
    private boolean isBalanced() {
        int black = 0;
        RedBlackNode<T, U> x = root;
        while (!x.isNil()) {
            if (x.isBlack) {
                black++;
            }
            x = x.left;
        }
        return root.isBalanced(black);
    }
    
    /**
     * Returns <code>true</code> if this tree has a valid red-coloring.
     * <p>
     * A red-black tree has a valid red-coloring if every red node has two
     * black children. The sentinel node is black.
     * <p>
     * This method is used for debugging only, and its access is changed in
     * testing.
     * 
     * @return whether or not this interval tree has a valid red coloring
     */
    @SuppressWarnings("unused")
    private boolean hasValidRedColoring() {
        return root.hasValidRedColoring();
    }
    
    /**
     * Returns <code>true</code> if each node in this tree has a
     * <code>maxEnd</code> value equal to the greatest interval end value of
     * all the intervals in its subtree.
     * <p>
     * The <code>maxEnd</code> value of an interval-tree node is equal to
     * the maximum of the end-values of all intervals contained in the
     * node's subtree.
     * <p>
     * This method is used for debugging only, and its access is changed in
     * testing.
     * 
     * @return whether or not this interval tree has consistent
     * <code>maxEnd</code> values
     */
    @SuppressWarnings("unused")
    private boolean hasConsistentMaxEnds() {
        return root.hasConsistentMaxEnds();
    }
}
