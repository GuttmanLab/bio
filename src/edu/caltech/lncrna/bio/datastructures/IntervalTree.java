package edu.caltech.lncrna.bio.datastructures;

import java.util.Iterator;

public interface IntervalTree<T extends Interval> extends Iterable<T> {

    public boolean isEmpty();
    
    /**
     * @return the number of intervals stored in this tree
     */
    public int size();
    
    public boolean contains(Interval t);
    
    /**
     * Returns an iterator over this tree's elements.
     * <p>
     * The iterator traverses the elements in ascending order.
     * 
     * @return an iterator over this tree's elements
     */
    public Iterator<T> iterator();
    
    /**
     * Returns an iterator of this tree's elements that overlap the specified
     * interval.
     * 
     * @param i - the specified interval
     * @return an iterator over this tree's overlapping elements
     */
    public Iterator<T> overlappers(Interval i);
    
    /**
     * Whether or not any intervals within this tree overlap the specified
     * interval.
     * 
     * @param i - the specified interval to check for overlap
     * @return <code>true</code> if any elements in this tree overlap the
     * specified interval; otherwise, <code>false</code>.
     */
    public boolean overlaps(Interval i);
    
    /**
     * The number of overlapping elements in this tree.
     * 
     * @param i - the specified interval to check for overlap
     * @return the number of elements in this tree that overlap the specified
     * interval
     */
    public int numOverlappers(Interval i);
    
    public boolean insert(T t);
    
    public boolean delete(T t);
    
    /**
     * Deletes all elements from this tree with matching start and end
     * coordinates.
     * <p>
     * This method returns <code>true</code> if an element is deleted, thereby
     * altering the tree. If no matching element is found, the tree
     * remains unchanged and this method returns <code>false</code>.
     * <p>
     * For some implementations of <code>IntervalTree</code>, the start and end
     * coordinates uniquely define an element. For others, the start and end
     * coordinates define multiple elements. When the latter is the case, all
     * such elements are deleted.
     * 
     * @param start - the start coordinate of the intervals to delete
     * @param end - the end coordinate of the intervals to delete
     * @return if the deletion is successful
     */
    public boolean delete(int start, int end);
    
    /**
     * Deletes the minimal interval(s) from this tree.
     * <p>
     * If there is no minimal interval (that is, if the tree is empty), this
     * tree remains unchanged.
     * <p>
     * For some implementations of <code>IntervalTree</code>, if a minimal
     * interval exists, it is unique. For others, there may be multiple minimal
     * intervals. When the latter is the case, all such intervals are deleted.
     * 
     * @return whether or not an interval was successfully removed from this
     * tree
     */
    public boolean deleteMin();
    
    /**
     * Deletes the maximal interval(s) from this tree.
     * <p>
     * If there is no maximal interval (that is, if the tree is empty), this
     * tree remains unchanged.
     * <p>
     * For some implementations of <code>IntervalTree</code>, if a maximal
     * interval exists, it is unique. For others, there may be multiple maximal
     * intervals. When the latter is the case, all such intervals are deleted.
     * 
     * @return whether or not an interval was successfully removed from this
     * tree
     */
    public boolean deleteMax();
}
