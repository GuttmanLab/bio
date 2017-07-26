package edu.caltech.lncrna.bio.datastructures;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * A balanced binary-search tree keyed by {@link Interval} objects.
 * <p>
 * Intervals in this tree are assumed to be contiguous, i.e., with no gaps.
 * Gapped intervals can be stored in this tree, but the tree's methods
 * will only consider the start and end coordinates when looking up intervals.
 * In other words, a gapped interval will be considered equivalent with its
 * hull or gene body. Use an {@link IntervalTreeDuplicateBounds} to keep gapped
 * intervals distinct.
 * <p>
 * The underlying data-structure is a red-black tree largely implemented from
 * CLRS with the interval-tree extensions mentioned in section 14.3
 * 
 * @param <T> - the type of interval that this tree contains
 * @see Cormen, Thomas H.; Leiserson, Charles E.; Rivest, Ronald L.; Stein,
 * Clifford (2001) [1990]. Introduction to Algorithms (2nd ed.). MIT Press and
 * McGraw-Hill.
 */
public class IntervalTreeUniqueBounds<T extends Interval>
implements IntervalTree<T> {

    private Node root;  // The root node.
    private Node nil;   // The sentinel node to represent the absence of a node.
    private int size;   // Size of the tree. Updated by insert() and Node#delete()

    /**
     * Class constructor.
     * <p>
     * Constructs an empty tree.
     */
    public IntervalTreeUniqueBounds() {
        nil = new Node();
        root = nil;
        size = 0;
    }
    
    /**
     * Class constructor.
     * <p>
     * Constructs a tree containing the single, specified interval.
     * 
     * @param t - the interval to add to the tree
     */
    public IntervalTreeUniqueBounds(T t) {
        nil = new Node();
        root = new Node(t);
        root.blacken();
        size = 1;
    }

    ///////////////////////////////////
    // Tree -- General query methods //
    ///////////////////////////////////

    @Override
    public boolean isEmpty() {
        return root.isNil();
    }
    
    @Override
    public int size() {
        return size;
    }
    
    /**
     * Returns the node in this tree matching the specified interval.
     * <p>
     * The returned node will contain an interval of type <code>T</code> with
     * matching start and end coordinates. The two intervals are not
     * necessarily equal.
     * <p>
     * This method returns the sentinel node if a matching node cannot be
     * found.
     * 
     * @param i - the interval to search for
     * @return the matching node
     */
    private Node search(Interval i) {
        return root.search(i);
    }
    
    /**
     * Returns the node in this tree matching the interval defined by
     * <code>start</code> and <code>end</code>.
     * <p>
     * The returned node will contain an interval of type <code>T</code> with
     * matching start and end coordinates. The two intervals are not
     * necessarily equal.
     * <p>
     * This method returns the sentinel node if a matching node cannot be
     * found.
     * 
     * @param start - the start coordinate
     * @param end - the end coordinate
     * @return the matching node
     */
    private Node search(int start, int end) {
        return root.search(start, end);
    }

    /**
     * This method considers two intervals to be equal if they have equal start
     * coordinates and end coordinates. The two intervals themselves are not
     * necessarily equivalent in other respects.
     * 
     * @param i - the interval to search for
     * @return <code>true</code> if this tree contains a matching interval,
     * and <code>false</code> otherwise.
     */
    @Override
    public boolean contains(Interval i) {
        return !search(i).isNil();
    }
    
    /**
     * If there is no minimum element (this is, if this tree is empty),
     * this method returns an empty <code>Optional</code>.
     * 
     * @return the minimum element of this tree, wrapped in an
     * <code>Optional</code>
     */
    public Optional<T> minimum() {
        Node n = root.minimumNode();
        return n.isNil() ? Optional.empty() : Optional.of(n.interval());
    }

    /**
     * If there is no maximum element (this is, if this tree is empty),
     * this method returns an empty <code>Optional</code>.
     * 
     * @return the maximum element of this tree, wrapped in an
     * <code>Optional</code>
     */
    public Optional<T> maximum() {
        Node n = root.maximumNode();
        return n.isNil() ? Optional.empty() : Optional.of(n.interval());
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
        Node n = search(i);
        if (n.isNil()) {
            return Optional.empty();
        }

        n = n.successor();
        if (n.isNil()) {
            return Optional.empty();
        }

        return Optional.of(n.interval());
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
        Node n = search(i);
        if (n.isNil()) {
            return Optional.empty();
        }

        n = n.predecessor();
        if (n.isNil()) {
            return Optional.empty();
        }

        return Optional.of(n.interval());
    }

    @Override
    public Iterator<T> iterator() {
        return new TreeIterator(root);
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
    
    /**
     * This method returns an empty <code>Optional</code> if there are no
     * overlappers.
     *
     * @param i - the specified interval
     * @return this tree's smallest element that overlaps the specified
     * interval, wrapped in an <code>Optional</code> 
     */
    public Optional<T> minimumOverlapper(Interval i) {
        Node n = root.minimumOverlappingNode(i);
        return n.isNil() ? Optional.empty() : Optional.of(n.interval());
    }
    
    ///////////////////////////////
    // Tree -- Insertion methods //
    ///////////////////////////////

    /**
     * Inserts the given <code>T</code> into this tree.
     * <p>
     * This method constructs a new node containing the given <code>T</code>
     * and places it into this tree. If this tree already contains a
     * <code>T</code> with matching start coordinates and end coordinates, this
     * tree remains unchanged.
     * <p>
     * This method returns <code>true</code> if the value was successfully
     * inserted, that is, if the inserted value did not already exist and the
     * tree was altered. If the value was already present in the tree, the tree
     * remains unchanged and this method returns <code>false</code>.
     * 
     * @param t - the value to place into the tree
     * @return if the insertion is successful
     */
    @Override
    public boolean insert(T t) {
        
        Node z = new Node(t);
        Node y = nil;
        Node x = root;

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
            z.insertFixup();
        }

        size++;
        return true;
    }
    
    //////////////////////////////
    // Tree -- Deletion methods //
    //////////////////////////////
    
    /**
     * Deletes the element from this tree that matches the specified interval.
     * <p>
     * An element matches the specified interval if they have equal start
     * coordinates and end coordinates.
     * <p>
     * This method returns <code>true</code> if an element is deleted, thereby
     * altering the tree. If no matching element is found, the tree
     * remains unchanged and this method returns <code>false</code>.
     * 
     * @param i - the interval to delete from the tree
     * @return if the deletion is successful
     */
    @Override
    public boolean delete(Interval i) {    // Node#delete does nothing and returns
        return search(i).delete();         // false if t.isNil()
    }
    
    @Override
    public boolean delete(int start, int end) {
        return search(start, end).delete();
    }
    
    /**
     * Removes an interval with start and end values matching the specified
     * interval from this tree.
     *
     * @param t - the specified interval
     * @return the removed element, wrapped in an<code>Optional</code>; if no
     * such element, an empty <code>Optional</code>
     */
    public Optional<T> remove(Interval i) {
        Node matchingNode = search(i);

        Optional<T> matchingInterval =
                Optional.ofNullable(matchingNode.interval);

        matchingNode.delete();
        return matchingInterval;
    }
    
    /**
     * Removes an interval with matching start and end values.
     *
     * @param start - the start value
     * @param end - the end value
     * @return the removed element, wrapped in an<code>Optional</code>; if no
     * such element, an empty <code>Optional</code>
     */
    public Optional<T> remove(int start, int end) {
        return remove(new SimpleInterval(start, end));
    }
    
    @Override
    public boolean deleteMin() {            // Node#delete does nothing and
        return root.minimumNode().delete(); // returns false if t.isNil()
    }
    
    /**
     * Deletes and returns the least interval from this tree.
     * <p>
     * If there is no least interval (that is, if the tree is empty), this
     * IntervalTree remains unchanged, and an empty <code>Optional</code> is
     * returned.
     * 
     * @return an <code>Optional</code> wrapping the least interval in this
     * tree if it exists; otherwise, an empty <code>Optional</code>
     */
    public Optional<T> removeMin() {
        Node minNode = root.minimumNode();
        Optional<T> minInterval = Optional.ofNullable(minNode.interval);
        minNode.delete();
        return minInterval;
    }
    
    @Override
    public boolean deleteMax() {            // Node#delete does nothing and
        return root.maximumNode().delete(); // returns false if t.isNil()
    }
    
    /**
     * Deletes and returns the greatest interval from this tree.
     * <p>
     * If there is no greatest interval (that is, if the tree is empty), this
     * IntervalTree remains unchanged, and an empty <code>Optional</code> is
     * returned.
     * 
     * @return an <code>Optional</code> wrapping the greatest interval in this
     * tree if it exists; otherwise, an empty <code>Optional</code>
     */
    public Optional<T> removeMax() {
        Node maxNode = root.maximumNode();
        Optional<T> maxInterval = Optional.ofNullable(maxNode.interval);
        maxNode.delete();
        return maxInterval;
    }
    
    /**
     * Deletes all intervals from this tree that overlap the given interval.
     * <p>
     * If there are no overlapping intervals, this tree remains unchanged.
     *
     * @param t - the overlapping interval
     * @return whether or not an interval was removed from this tree
     */
    public boolean deleteOverlappers(T t) {
        // TODO 
        // Replacing the line
        //    s.forEach(n -> delete(n.interval()))
        // with
        //    s.forEach(n -> n.delete())
        // causes a NullPointerException in resetMaxEnd(). Why?!
        //
        // As it stands, every deletion operation causes the tree
        // to be searched. Fix this, please.

        Set<Node> s = new HashSet<>();
        Iterator<Node> iter = new OverlappingNodeIterator(root, t);
        iter.forEachRemaining(s::add);
        return s.stream()
                .map(n -> delete(n.interval))
                .reduce(false, (a, b) -> a || b);
    }

    /**
     * A representation of a node in this tree.
     */
    private class Node implements Interval {
        
        /* Most of the "guts" of the interval tree are actually methods called
         * by nodes. For example, IntervalTree#delete(val) searches up the Node
         * containing val; then that Node deletes itself with Node#delete().
         */

        private T interval;
        private Node parent;
        private Node left;
        private Node right;
        private boolean isBlack;
        private int maxEnd;

        /**
         * Constructs a node with no data.
         * <p>
         * This node has a null interval field, is black, and has all internal
         * node references pointing at itself. This is intended to be used as
         * the sentinel node in the tree ("nil" in CLRS).
         */
        private Node() {
            parent = this;
            left = this;
            right = this;
            blacken();
        }
        
        /**
         * Constructs a node containing the given element.
         * <p>
         * This node is red, and has all internal node references pointing at
         * the sentinel node.
         * 
         * @param interval - the element to be contained within this node
         */
        public Node(T element) {
            this.interval = element;
            parent = nil;
            left = nil;
            right = nil;
            maxEnd = element.getEnd();
            redden();
        }

        /**
         * @return the interval or element contained by this node
         */
        public T interval() {
            return interval;
        }

        @Override
        public int getStart() {
            return interval.getStart();
        }

        @Override
        public int getEnd() {
            return interval.getEnd();
        }
        
        ///////////////////////////////////
        // Node -- General query methods //
        ///////////////////////////////////
        
        /**
         * Searches the subtree rooted at this node for the node with the same
         * coordinates as the specified {@link Interval}.
         * 
         * @param i - the specified interval
         * @return the matching node, if it exists; otherwise, the sentinel
         * node 
         */
        private Node search(Interval i) {

            Node n = this;
            
            while (!n.isNil() && i.compareTo(n) != 0) {
                n = i.compareTo(n) == -1 ? n.left : n.right;
            }
            return n;
        }
        
        /**
         * Searches the subtree rooted at this node for the node with the
         * specified coordinates.
         * 
         * @param start - the specified start coordinate
         * @param end - the specified end coordinate
         * @return the matching node, if it exists; otherwise, the sentinel
         * node
         */
        private Node search(int start, int end) {
            return search(new SimpleInterval(start, end));
        }

        /**
         * Searches the subtree rooted at this node for the node with the
         * minimum {@link Interval}.
         * 
         * @return the node with the minimum interval, if it exists; otherwise,
         * the sentinel node
         */
        private Node minimumNode() {
            
            Node n = this;
            
            while (!n.left.isNil()) {
                n = n.left;
            }
            return n;
        }

        /**
         * Searches the subtree rooted at this node for the node with the
         * maximum {@link Interval}.
         * 
         * @return the node with the maximum interval, if it exists; otherwise
         * the sentinel node
         */
        private Node maximumNode() {
            
            Node n = this;
            
            while (!n.right.isNil()) {
                n = n.right;
            }
            return n;
        }
        
        /**
         * @return the node following this node, if it exists; otherwise the
         * sentinel node
         */
        private Node successor() {
            
            if (!right.isNil()) {
                return right.minimumNode();
            }
            
            Node x = this;
            Node y = parent;
            while (!y.isNil() && x == y.right) {
                x = y;
                y = y.parent;
            }
            
            return y;
        }

        /**
         * @return the node preceding this node, if it exists; otherwise the
         * sentinel node
         */
        private Node predecessor() {
            
            if (!left.isNil()) {
                return left.maximumNode();
            }
            
            Node x = this;
            Node y = parent;
            while (!y.isNil() && x == y.left) {
                x = y;
                y = y.parent;
            }
            
            return y;
        }
        
        ///////////////////////////////////////
        // Node -- Overlapping query methods //
        ///////////////////////////////////////
        
        /**
         * The only guarantee of this method is that the returned
         * node overlaps the specified interval. This method is meant to be a
         * quick helper method to determine if any overlap exists between an
         * interval and any of a tree's intervals. The returned node will be
         * the first overlapping one found.
         * 
         * @param i - the overlapping interval
         * @return a node from this node's subtree that overlaps the specified
         * interval, if one exists; otherwise the sentinel node
         */
        private Node anyOverlappingNode(Interval i) {
            Node x = this;
            while (!x.isNil() && !i.overlaps(x.interval)) {
                x = !x.left.isNil() && x.left.maxEnd > i.getStart() ? x.left : x.right;
            }
            return x;
        }
        
        /**
         * @param i - the specified interval
         * @return the minimum node from this node's subtree that overlaps the
         * specified interval, if one exists; otherwise, the sentinel node
         */
        private Node minimumOverlappingNode(Interval i) {

            Node result = nil;
            Node n = this;

            if (!n.isNil() && n.maxEnd > i.getStart()) {
                while (true) {
                    if (n.overlaps(i)) {

                        // This node overlaps. There may be a lesser overlapper
                        // down the left subtree. No need to consider the right
                        // as all overlappers there will be greater.

                        result = n;
                        n = n.left;

                        if (n.isNil() || n.maxEnd <= i.getStart()) {
                            // Either no left subtree, or nodes can't overlap.
                            break;
                        }
                    } else {

                        // This node doesn't overlap.
                        // Check the left subtree if an overlapper may be there

                        Node left = n.left;
                        if (!left.isNil() && left.maxEnd > i.getStart()) {
                            n = left;
                        } else {
                            
                        // Left subtree cannot contain an overlapper. Check the
                        // right sub-tree.
                        
                            if (n.getStart() >= i.getEnd()) {
                                // Nothing in the right subtree can overlap
                                break;
                            }

                            n = n.right;
                            if (n.isNil() || n.maxEnd <= i.getStart()) {
                                // No right subtree, or nodes can't overlap.
                                break;
                            }
                        }
                    }
                }
            }

            return result;
        }
        
        /**
         * @param i - the specified interval
         * @return an iterator over all values in this node's subtree that
         * overlap the specified interval
         */
        private Iterator<T> overlappers(Interval i) {
            return new OverlapperIterator(this, i);
        }
        
        /**
         * @param i - the specified interval
         * @return the next node (relative to this node) that overlaps the
         * specified interval, if one exists; otherwise, the sentinel node
         */
        private Node nextOverlappingNode(Interval i) {
            Node x = this;
            Node rtrn = nil;

            // First, check the right subtree for its minimum overlapper.
            if (!right.isNil()) {
                rtrn = x.right.minimumOverlappingNode(i);
            }
            
            // If we didn't find it in the right subtree, walk up the tree and
            // check the parents of left-children as well as their right subtrees.
            while (!x.parent.isNil() && rtrn.isNil()) {
                if (x.isLeftChild()) {
                    rtrn = x.parent.overlaps(i)
                            ? x.parent
                            : x.parent.right.minimumOverlappingNode(i);
                }
                x = x.parent;
            }
            return rtrn;
        }
        
        /**
         * Returns the number of nodes in this node's subtree that overlap the
         * specified interval.
         * <p>
         * This number includes this node if it overlaps the interval. This
         * method iterates over all overlapping nodes, so if you ultimately
         * need to inspect the nodes or do anything more than get a count, it
         * will be more efficient to simply create the iterator yourself with
         * {@link Node#overlappers}.
         * 
         * @param i - the specified interval
         * @return the number of overlapping nodes
         */
        private int numOverlappingNodes(Interval i) {
            int count = 0;
            Iterator<Node> iter = new OverlappingNodeIterator(this, i);
            
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
            return count;
        }
        
        //////////////////////////////
        // Node -- Deletion methods //
        //////////////////////////////
        
        //TODO: Should we rewire the Nodes rather than copying data?
        //      I worry this method causes some code which seems like
        //      it would otherwise work to fail.
        
        /**
         * Deletes this node from its tree.
         * <p>
         * More specifically, removes the data held within this node from the
         * tree. Depending on the structure of the tree at this node, this
         * particular <code>Node</code> instance may not be removed; rather, a
         * different node may be deleted and that node's contents copied into
         * this one, overwriting the previous contents.
         * 
         * @return <code>true</code> if the node is successfully deleted;
         * otherwise (if the node is the sentinel node) <code>false</code>
         */
        private boolean delete() {
            
            if (isNil()) {  // Can't delete the sentinel node.
                return false;
            }
            
            Node y = this;

            if (hasTwoChildren()) { // If the node to remove has two children,
                y = successor();    // copy the successor's data into it and
                copyData(y);        // remove the successor. The successor is
                maxEndFixup();      // guaranteed to both exist and have at most
            }                       // one child, so we've converted the two-
                                    // child case to a one- or no-child case.
            
            
            Node x = y.left.isNil() ? y.right : y.left;

            x.parent = y.parent;

            if (y.isRoot()) {
                root = x;
            } else if (y.isLeftChild()) {
                y.parent.left = x;
                y.maxEndFixup();
            } else {
                y.parent.right = x;
                y.maxEndFixup();
            }
            
            if (y.isBlack) {
                x.deleteFixup();
            }
            
            size--;
            return true;
        }
        
        ////////////////////////////////////////////////
        // Node -- Tree-invariant maintenance methods //
        ////////////////////////////////////////////////

        /**
         * @return whether or not this node is the root of its tree
         */
        public boolean isRoot() {
            return (!isNil() && parent.isNil());
        }
        
        /**
         * @return whether or not this node is the sentinel node
         */
        public boolean isNil() {
            return this == nil;
        }

        /**
         * @return whether or not this node is the left child of its parent
         */
        public boolean isLeftChild() {
            return this == parent.left;
        }

        /**
         * @return whether or not this node is the right child of its parent
         */
        public boolean isRightChild() {
            return this == parent.right;
        }

        /**
         * @return whether or not this node has no children, i.e., is a leaf
         */
        public boolean hasNoChildren() {
            return left.isNil() && right.isNil();
        }

        /**
         * @return whether or not this node has two children, i.e., neither of
         * its children are leaves
         */
        public boolean hasTwoChildren() {
            return !left.isNil() && !right.isNil();
        }
        
        /**
         * Sets this node's color to black.
         */
        private void blacken() {
            isBlack = true;
        }
        
        /**
         * Sets this node's color to red.
         */
        private void redden() {
            isBlack = false;
        }
        
        /**
         * @return whether or not this node's color is red
         */
        public boolean isRed() {
            return !isBlack;
        }
        
        /**
         * @return a reference to the grandparent of this node
         */
        private Node grandparent() {
            return parent.parent;
        }

        /**
         * Resets the <code>maxEnd</code> field of this node to its correct
         * value.
         * <p>
         * The value of the <code>maxEnd</code> field should be the greatest
         * of:
         * <ul>
         * <li>the end value of this node's data
         * <li>the <code>maxEnd</code> value of this node's left child, if not
         * nil
         * <li>the <code>maxEnd</code> value of this node's right child, if not
         * nil
         * </ul>
         * <p>
         * This method will be correct only if the left and right children have
         * correct <code>maxEnd</code> values.
         */
        private void resetMaxEnd() {
            int val = interval.getEnd();
            if (!left.isNil()) {
                val = Math.max(val, left.maxEnd);
            }
            if (!right.isNil()) {
                val = Math.max(val, right.maxEnd);
            }
            maxEnd = val;
        }
        
        /**
         * Sets the <code>maxEnd</code> of this node, and all nodes up to the
         * root of the tree, to the correct value.
         * 
         * @see Node#resetMaxEnd
         */
        private void maxEndFixup() {
            Node n = this;
            n.resetMaxEnd();
            while (!n.parent.isNil()) {
                n = n.parent;
                n.resetMaxEnd();
            }
        }
        
        /**
         * Performs a left-rotation on this node.
         * 
         * @see - Cormen, Thomas H.; Leiserson, Charles E.; Rivest, Ronald L.; Stein,
         * Clifford (2001) [1990]. Introduction to Algorithms (2nd ed.). MIT Press and
         * McGraw-Hill. pp. 277-279.
         */
        private void leftRotate() {
            Node y = right;
            right = y.left;

            if (!y.left.isNil()) {
                y.left.parent = this;
            }
            
            y.parent = parent;
            
            if (parent.isNil()) {
                root = y;
            } else if (isLeftChild()) {
                parent.left = y;
            } else {
                parent.right = y;
            }
            
            y.left = this;
            parent = y;
            
            resetMaxEnd();
            y.resetMaxEnd();
        }
        
        /**
         * Performs a right-rotation on this node.
         * 
         * @see - Cormen, Thomas H.; Leiserson, Charles E.; Rivest, Ronald L.; Stein,
         * Clifford (2001) [1990]. Introduction to Algorithms (2nd ed.). MIT Press and
         * McGraw-Hill. pp. 277-279.
         */
        private void rightRotate() {
            Node y = left;
            left = y.right;

            if (!y.right.isNil()) {
                y.right.parent = this;
            }
            
            y.parent = parent;
            
            if (parent.isNil()) {
                root = y;
            } else if (isLeftChild()) {
                parent.left = y;
            } else {
                parent.right = y;
            }
            
            y.right = this;
            parent = y;
            
            resetMaxEnd();
            y.resetMaxEnd();
        }

        /**
         * Copies the data from another node into this node.
         * <p>
         * Technically, no copying occurs. This method just assigns a reference
         * to the other node's data.
         * 
         * @param o - the other node containing the data to be copied
         */
        private void copyData(Node o) {
            interval = o.interval;
        }
        
        @Override
        public String toString() {
            if (isNil()) {
                return "nil";
            } else {
                String color = isBlack ? "black" : "red"; 
                return "start = " + getStart() +
                       "\nend = " + getEnd() +
                       "\nmaxEnd = " + maxEnd +
                       "\ncolor = " + color;
            }
        }
        
        /**
         * Ensures that red-black constraints and interval-tree constraints are
         * maintained after an insertion.
         */
        private void insertFixup() {
            Node z = this;
            while (z.parent.isRed()) {
                if (z.parent.isLeftChild()) {
                    Node y = z.parent.parent.right;
                    if (y.isRed()) {
                        z.parent.blacken();
                        y.blacken();
                        z.grandparent().redden();
                        z = z.grandparent();
                    } else {
                        if (z.isRightChild()) {
                            z = z.parent;
                            z.leftRotate();
                        }
                        z.parent.blacken();
                        z.grandparent().redden();
                        z.grandparent().rightRotate();
                    }
                } else {
                    Node y = z.grandparent().left;
                    if (y.isRed()) {
                        z.parent.blacken();
                        y.blacken();
                        z.grandparent().redden();
                        z = z.grandparent();
                    } else {
                        if (z.isLeftChild()) {
                            z = z.parent;
                            z.rightRotate();
                        }
                        z.parent.blacken();
                        z.grandparent().redden();
                        z.grandparent().leftRotate();
                    }
                }
            }
            root.blacken();
        }
        
        /**
         * Ensures that red-black constraints and interval-tree constraints are
         * maintained after deletion.
         */
        private void deleteFixup() {
            Node x = this;
            while (!x.isRoot() && x.isBlack) {
                if (x.isLeftChild()) {
                    Node w = x.parent.right;
                    if (w.isRed()) {
                        w.blacken();
                        x.parent.redden();
                        x.parent.leftRotate();
                        w = x.parent.right;
                    }
                    if (w.left.isBlack && w.right.isBlack) {
                        w.redden();
                        x = x.parent;
                    } else {
                        if (w.right.isBlack) {
                            w.left.blacken();
                            w.redden();
                            w.rightRotate();
                            w = x.parent.right;
                        }
                        w.isBlack = x.parent.isBlack;
                        x.parent.blacken();
                        w.right.blacken();
                        x.parent.leftRotate();
                        x = root;
                    }
                } else {
                    Node w = x.parent.left;
                    if (w.isRed()) {
                        w.blacken();
                        x.parent.redden();
                        x.parent.rightRotate();
                        w = x.parent.left;
                    }
                    if (w.left.isBlack && w.right.isBlack) {
                        w.redden();
                        x = x.parent;
                    } else {
                        if (w.left.isBlack) {
                            w.right.blacken();
                            w.redden();
                            w.leftRotate();
                            w = x.parent.left;
                        }
                        w.isBlack = x.parent.isBlack;
                        x.parent.blacken();
                        w.left.blacken();
                        x.parent.rightRotate();
                        x = root;
                    }                    
                }
            }
            x.blacken();
        }
        
        ///////////////////////////////
        // Node -- Debugging methods //
        ///////////////////////////////
        
        /**
         * Method for testing and debugging.
         *
         * @param min - a lower-bound node
         * @param max - an upper-bound node
         * @return whether or not the subtree rooted at this node is a valid
         * binary-search tree
         */
        private boolean isBST(Node min, Node max) {
            if (isNil()) {
                return true;   // Leaves are a valid BST, trivially.
            }
            if (min != null && compareTo(min) <= 0) {
                return false;  // This Node must be greater than min
            }
            if (max != null && compareTo(max) >= 0) {
                return false;  // and less than max.
            }
            
            // Children recursively call method with updated min/max.
            return left.isBST(min, this) && right.isBST(this, max);
        }
        
        /**
         * Method for testing and debugging.
         * <p>
         * Balance determination is done by calculating the black-height.
         * 
         * @param black - the expected black-height of this subtree
         * @returns whether or not the subtree rooted at this node is balanced.
         */
        private boolean isBalanced(int black) {
            if (isNil()) {
                return black == 0;  // Leaves have a black-height of zero,
            }                       // even though they are black.
            if (isBlack) {
                black--;
            }
            return left.isBalanced(black) && right.isBalanced(black);
        }
        
        /**
         * Method for testing and debugging.
         * <p>
         * A red-black tree has a valid red-coloring if every red node has two
         * black children.
         * 
         * @return whether or not the subtree rooted at this node has a valid
         * red-coloring.
         */
        private boolean hasValidRedColoring() {
            if (isNil()) {
                return true;
            } else if (isBlack) {
                return left.hasValidRedColoring() &&
                        right.hasValidRedColoring();
            } else {
                return left.isBlack && right.isBlack &&
                        left.hasValidRedColoring() &&
                        right.hasValidRedColoring();
            }
        }
        
        /**
         * Method for testing and debugging.
         * <p>
         * The <code>maxEnd</code> value of an interval-tree node is equal to
         * the maximum of the end-values of all intervals contained in the
         * node's subtree.
         * 
         * @return whether or not the constituent nodes of the subtree rooted
         * at this node have consistent values for the <code>maxEnd</code>
         * field. 
         */
        private boolean hasConsistentMaxEnds() {

            if (isNil()) {                                    // 1. sentinel node
                return true;
            }
            
            if (hasNoChildren()) {                            // 2. leaf node
                return maxEnd == getEnd();
            } else {
                boolean consistent = maxEnd >= getEnd();
                if (hasTwoChildren()) {                       // 3. two children
                    return consistent &&
                           maxEnd >= left.maxEnd &&
                           maxEnd >= right.maxEnd &&
                           left.hasConsistentMaxEnds() &&
                           right.hasConsistentMaxEnds();
                } else if (left.isNil()) {                    // 4. one child -- right
                    return consistent &&
                           maxEnd >= right.maxEnd &&
                           right.hasConsistentMaxEnds();
                } else {
                    return consistent &&                      // 5. one child -- left
                           maxEnd >= left.maxEnd &&
                           left.hasConsistentMaxEnds();
                }
            }
        }
    }
    
    ///////////////////////
    // Tree -- Iterators //
    ///////////////////////
    
    /**
     * An iterator that returns the nodes of this interval tree in ascending
     * order.
     */
    private final class TreeNodeIterator implements Iterator<Node> {

        private Node next;
        
        /**
         * Constructor.
         * <p>
         * Construct an iterator over the subtree rooted at the given node.
         * 
         * @param root - the root of the subtree
         */
        private TreeNodeIterator(Node root) {
            next = root.minimumNode();
        }
        
        @Override
        public boolean hasNext() {
            return !next.isNil();
        }

        @Override 
        public Node next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Interval tree has no more elements.");
            }
            Node rtrn = next;
            next = rtrn.successor();
            return rtrn;
        }   
    }

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
        private TreeIterator(Node root) {
            nodeIter = new TreeNodeIterator(root);
        }

        @Override
        public boolean hasNext() {
            return nodeIter.hasNext();
        }

        @Override
        public T next() {
            return nodeIter.next().interval;
        }
    }
 
    /**
     * An iterator that returns only the nodes of this tree that overlap a
     * specified interval.
     * <p>
     * The overlapping nodes are returned in ascending order.
     */
    private class OverlappingNodeIterator implements Iterator<Node> {
        
        private Node next;
        private Interval interval;

        /**
         * Constructor.
         * <p>
         * Construct an iterator over overlapping nodes of the subtree rooted
         * at the given node.
         * 
         * @param root - the root of the subtree
         * @param interval - the interval that the nodes must overlap
         */
        private OverlappingNodeIterator(Node root, Interval interval) {
            this.interval = interval;
            next = root.minimumOverlappingNode(this.interval);
        }
        
        @Override
        public boolean hasNext() {
            return !next.isNil();
        }
        
        @Override
        public Node next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Interval tree has no more overlapping elements.");
            }
            Node rtrn = next;
            next = rtrn.nextOverlappingNode(interval);
            return rtrn;
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
        private OverlapperIterator(Node root, Interval i) {
            nodeIter = new OverlappingNodeIterator(root, i);
        }

        @Override
        public boolean hasNext() {
            return nodeIter.hasNext();
        }

        @Override
        public T next() {
            return nodeIter.next().interval;
        }
    }

    ///////////////////////////////
    // Tree -- Debugging methods //
    ///////////////////////////////
    
    /**
     * This method will return <code>false</code> if any node is less than its
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
     * This method will return <code>false</code> if all of the branches (from
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
        Node x = root;
        while (!x.isNil()) {
            if (x.isBlack) {
                black++;
            }
            x = x.left;
        }
        return root.isBalanced(black);
    }
    
    /**
     * This method will return <code>false</code> if all of the branches (from
     * root to leaf) do not contain the same number of black nodes.
     * (Specifically, the black-number of each branch is compared against the
     * black-number of the left-most branch.)
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
     * This method will only return <code>true</code> if each node has a
     * <code>maxEnd</code> value equal to the greatest interval end value of
     * all the intervals in its subtree.
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