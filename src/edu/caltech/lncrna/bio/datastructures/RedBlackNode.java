package edu.caltech.lncrna.bio.datastructures;

import java.util.Iterator;

/**
 * This class represents a node of a red-black interval tree.
 * <p>
 * This class is defined by two different type variables, <code>T</code> and
 * <code>U</code>. The first, <code>T</code>, represents the type of interval
 * contained in this node's corresponding tree. This type should be obvious,
 * and is analogous to the <code>T</code> in <code>List&lt;T&gt;</code>. The
 * second, <code>U</code>, represents the type contained internally in each
 * node. For example, if a node store's multiple intervals in an internal set,
 * <code>U</code> might be <code>IntervalSet&lt;T&gt;</code>.

 * @param <T> - the type of interval that this node's tree contains
 * @param <U> - the type of interval or data structure that the node itself
 * contains
 */
public abstract class RedBlackNode<T extends Interval, U extends Interval>
implements Interval, Iterable<T> {

    protected U data;
    protected RedBlackNode<T, U> parent;
    protected RedBlackNode<T, U> left;
    protected RedBlackNode<T, U> right;
    protected boolean isBlack;
    protected int maxEnd;
    
    /**
     * Class constructor.
     * <p>
     * Constructs a new, empty <code>RedBlackNode</code> instance.
     * <p>
     * This constructor is meant to be called when instantiating the sentinel
     * node of a tree.
     */
    protected RedBlackNode() {
        parent = this;
        left = this;
        right = this;
        blacken();
    }
    
    /**
     * Class constructor.
     * <p>
     * Constructs a new <code>RedBlackNode</code> instance containing the given
     * element.
     * 
     * @param element - the element to be contained by the resulting node
     */
    protected RedBlackNode(U element) {
        data = element;
        parent = nil();
        left = nil();
        right = nil();
        maxEnd = data.getEnd();
        redden();
    }
    
    /**
     * Returns the data contained by this node.
     * 
     * @return the data contained by this node
     */
    protected U getData() {
        return data;
    }
    
    @Override
    public int getStart() {
        return data.getStart();
    }
    
    @Override
    public int getEnd() {
        return data.getEnd();
    }
    
    /**
     * Searches this node's tree for the next node.
     * 
     * @return the node following this node, if it exists; otherwise the
     * sentinel node
     */
    protected RedBlackNode<T, U> successor() {
        
        if (!right.isNil()) {
            return right.minimumNode();
        }
        
        RedBlackNode<T, U> x = this;
        RedBlackNode<T, U> y = parent;
        while (!y.isNil() && x == y.right) {
            x = y;
            y = y.parent;
        }
        
        return y;
    }

    /**
     * Searches this node's tree for the previous node.
     * 
     * @return the node preceding this node, if it exists; otherwise the
     * sentinel node
     */
    protected RedBlackNode<T, U> predecessor() {
        
        if (!left.isNil()) {
            return left.maximumNode();
        }
        
        RedBlackNode<T, U> x = this;
        RedBlackNode<T, U> y = parent;
        while (!y.isNil() && x == y.left) {
            x = y;
            y = y.parent;
        }
        
        return y;
    }
    
    /**
     * Searches the subtree rooted at this node for the node with the same
     * coordinates as the specified {@link Interval}.
     * 
     * @param i - the specified interval
     * @return the matching node, if it exists; otherwise, the sentinel
     * node 
     */
    protected RedBlackNode<T, U> search(Interval i) {

        RedBlackNode<T, U> n = this;
        
        while (!n.isNil() && i.compareTo(n) != 0) {
            n = i.compareTo(n) == -1 ? n.left : n.right;
        }
        return n;
    }
    
    /**
     * Searches the subtree rooted at this node for the node with the
     * passed start and end coordinates.
     * 
     * @param start - the start coordinate
     * @param end - the end coordinate
     * @return the matching node, if it exists; otherwise, the sentinel
     * node 
     */
    protected RedBlackNode<T, U> search(int start, int end) {
        return search(new SimpleInterval(start, end));
    }
    
    /**
     * Searches the subtree rooted at this node for the node with the
     * minimum {@link Interval}.
     * 
     * @return the node with the minimum interval, if it exists; otherwise,
     * the sentinel node
     */
    protected RedBlackNode<T, U> minimumNode() {
        
        RedBlackNode<T, U> n = this;
        
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
    protected RedBlackNode<T, U> maximumNode() {
        
        RedBlackNode<T, U> n = this;
        
        while (!n.right.isNil()) {
            n = n.right;
        }
        return n;
    }
    
    /**
     * Returns whether this node is the root of its tree.
     * 
     * @return <code>true</code> if this node is the root
     */
    public boolean isRoot() {
        return (!isNil() && parent.isNil());
    }
    
    /**
     * Returns whether this node is the sentinel node.
     * 
     * @return <code>true</code> if this node is the sentinel node
     */
    public boolean isNil() {
        return this == nil();
    }

    /**
     * Returns whether this node is the left child of its parent.
     * 
     * @return <code>true</code> if this node is a left child
     */
    public boolean isLeftChild() {
        return this == parent.left;
    }

    /**
     * Returns whether this node is the right child of its parent.
     * 
     * @return <code>true</code> if this node is a right child
     */
    public boolean isRightChild() {
        return this == parent.right;
    }

    /**
     * Returns whether this node has no children, i.e., is a leaf.
     * 
     * @return <code>true</code> if this node has no children
     */
    public boolean hasNoChildren() {
        return left.isNil() && right.isNil();
    }

    /**
     * Returns whether or not this node has two children, i.e., neither of
     * its children are leaves
     * 
     * @return <code>true</code> if this node has two children
     */
    public boolean hasTwoChildren() {
        return !left.isNil() && !right.isNil();
    }
    
    /**
     * Sets this node's color to black.
     */
    protected void blacken() {
        isBlack = true;
    }
    
    /**
     * Sets this node's color to red.
     */
    protected void redden() {
        isBlack = false;
    }
    
    /**
     * Returns if this node is red.
     * 
     * @return if this node's color is red
     */
    public boolean isRed() {
        return !isBlack;
    }
    
    /**
     * Returns this node's grandparent.
     * 
     * @return a reference to the grandparent of this node
     */
    protected RedBlackNode<T, U> grandparent() {
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
    protected void resetMaxEnd() {
        int val = data.getEnd();
        if (!left.isNil()) {
            val = Math.max(val, left.maxEnd);
        }
        if (!right.isNil()) {
            val = Math.max(val, right.maxEnd);
        }
        maxEnd = val;
    }
    
    /**
     * Copies the data from another node into this node.
     * <p>
     * Technically, no copying occurs. This method just assigns a reference
     * to the other node's data.
     * 
     * @param o - the other node containing the data to be copied
     */
    protected void copyData(RedBlackNode<T, U> o) {
        data = o.data;
    }
    
    protected abstract RedBlackNode<T, U> nil();
    protected abstract int size();
    
    ///////////////////////////////////////
    // Node -- Overlapping query methods //
    ///////////////////////////////////////
    
    /**
     * Returns a node in this node's subtree that overlaps the given interval.
     * <p>
     * The only guarantee of this method is that the returned
     * node overlaps the specified interval. This method is meant to be a
     * quick helper method to determine if any overlap exists between an
     * interval and any of a tree's intervals. The returned node will be
     * the first overlapping one found.
     * 
     * @param i - the overlapping interval
     * @return a node from this node's subtree that overlaps the given
     * interval, if one exists; otherwise the sentinel node
     */
    protected RedBlackNode<T, U> anyOverlappingNode(Interval i) {
        RedBlackNode<T, U> x = this;
        while (!x.isNil() && !i.overlaps(x.data)) {
            x = !x.left.isNil() && x.left.maxEnd > i.getStart() ? x.left : x.right;
        }
        return x;
    }
    
    /**
     * Returns the minimum node in this node's subtree that overlaps the given
     * interval.
     * 
     * @param i - the specified interval
     * @return the minimum node from this node's subtree that overlaps the
     * given interval, if one exists; otherwise, the sentinel node
     */
    RedBlackNode<T, U> minimumOverlappingNode(Interval i) {

        RedBlackNode<T, U> result = nil();
        RedBlackNode<T, U> n = this;

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

                    RedBlackNode<T, U> left = n.left;
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
     * Returns the next node in this node's subtree that overlaps the given
     * interval.
     * 
     * @param i - the given interval
     * @return the next node (relative to this node) that overlaps the
     * given interval; if one does not exist, the sentinel node
     */
    protected RedBlackNode<T, U> nextOverlappingNode(Interval i) {
        RedBlackNode<T, U> x = this;
        RedBlackNode<T, U> rtrn = nil();

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
    protected abstract int numOverlappingNodes(Interval i);
    
    /**
     * @param i - the specified interval
     * @return an iterator over all values in this node's subtree that
     * overlap the specified interval
     */
    protected abstract Iterator<T> overlappers(Interval i);
    
    ///////////////////////////////
    // Node -- Debugging methods //
    ///////////////////////////////
    
    /**
     * Returns <code>false</code> if any node in this node's subtree is less
     * than its left child or greater than its right child.
     * <p>
     * Method for testing and debugging.
     *
     * @param min - a lower-bound node
     * @param max - an upper-bound node
     * @return whether or not the subtree rooted at this node is a valid
     * binary-search tree
     */
    protected boolean isBST(RedBlackNode<T, U> min, RedBlackNode<T, U> max) {
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
     * Returns <code>false</code> if all of the branches of this node's subtree
     * (from root to leaf) do not contain the same number of black nodes.
     * (Specifically, the black-number of each branch is compared against the
     * black-number of the left-most branch.)
     * <p>
     * Method for testing and debugging.
     * <p>
     * Balance determination is done by calculating the black-height.
     * 
     * @param black - the expected black-height of this subtree
     * @returns whether or not the subtree rooted at this node is balanced.
     */
    protected boolean isBalanced(int black) {
        if (isNil()) {
            return black == 0;  // Leaves have a black-height of zero,
        }                       // even though they are black.
        if (isBlack) {
            black--;
        }
        return left.isBalanced(black) && right.isBalanced(black);
    }
    
    /**
     * Returns <code>true</code> if this node's subtree has a valid
     * red-coloring.
     * <p>
     * Method for testing and debugging.
     * <p>
     * A red-black tree has a valid red-coloring if every red node has two
     * black children. The sentinel node is black.
     * 
     * @return whether or not the subtree rooted at this node has a valid
     * red-coloring.
     */
    protected boolean hasValidRedColoring() {
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
     * Returns <code>true</code> if each node in this node's subtree has a
     * <code>maxEnd</code> value equal to the greatest interval end value of
     * all the intervals in its subtree.
     * <p>
     * The <code>maxEnd</code> value of an interval-tree node is equal to
     * the maximum of the end-values of all intervals contained in the
     * node's subtree.
     * <p>
     * Method for testing and debugging.
     * 
     * @return whether or not the constituent nodes of the subtree rooted
     * at this node have consistent values for the <code>maxEnd</code>
     * field. 
     */
    protected boolean hasConsistentMaxEnds() {

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
    
    /**
     * Returns the string representation of this node.
     * <p>
     * The exact details of this representation are unspecified and subject to
     * change, but it will typically contain information about this node's
     * start and end values, as well as the values of its parent and children.
     * <p>
     * This method should be used solely for debugging and testing.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.isNil()) {
            sb.append("this: nil\n");
        } else {
            sb.append("this: [" + getStart() + ", " + getEnd() + ")\n");
        }
        
        if (parent.isNil()) {
            sb.append("parent: nil\n");
        } else {
            sb.append("parent: [" + parent.getStart() + ", " +
            parent.getEnd() + ")\n");
        }
        
        if (left.isNil()) {
            sb.append("left: nil\n");
        } else {
            sb.append("left: [" + left.getStart() + ", " +
            left.getEnd() + ")\n");
        }
        
        if (right.isNil()) {
            sb.append("right: nil\n");
        } else {
            sb.append("right: [" + left.getStart() + ", " +
            right.getEnd() + ")\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object other) {
        // No need for null check. The instanceof operator returns false
        // if (other == null).
        if (!(other instanceof RedBlackNode<?, ?>)) {
            return false;
        }

        RedBlackNode<?, ?> o = (RedBlackNode<?, ?>) other;
        
        return data.equals(o.data) &&
               isBlack == o.isBlack &&
               maxEnd == o.maxEnd &&
               parent == o.parent &&
               left == o.left &&
               right == o.right;
    }
    
    @Override
    public int hashCode() {
        // TODO: What to do about node references? Does this even matter?
        int result = 17;
        result = 31 * result + data.hashCode();
        result = 31 * result + (isBlack ? 1 : 0);
        result = 31 * result + maxEnd;
        return result;
    }
}
