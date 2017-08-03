package edu.caltech.lncrna.bio.datastructures;

import java.util.Collection;
import java.util.Iterator;

/**
 * This interface defines the behavior of trees containing {@link Interval}
 * objects.
 */
public interface IntervalTree<T extends Interval> extends Collection<T> {
    
    /**
     * Returns an iterator over the minimum elements of this tree.
     * <p>
     * If there is no minimum element (this is, if this tree is empty),
     * this method returns an empty iterator.
     * 
     * @return an iterator over the minimum elements of this tree
     */
    public Iterator<T> minima();
    
    /**
     * Returns an iterator over the maximum elements of this tree.
     * <p>
     * If there is no maximum element (this is, if this tree is empty),
     * this method returns an empty iterator.
     * 
     * @return an iterator over the maximum elements of this tree
     */
    public Iterator<T> maxima();
    
    /**
     * Returns an iterator over all elements in this tree that overlap the
     * given interval.
     * 
     * @param i - the interval to check for overlap
     * @return an iterator over all overlapping elements
     */
    public Iterator<T> overlappers(Interval i);

    /**
     * Returns whether any element in this tree overlaps the given interval.
     * 
     * @param i - the interval to check for overlap
     * @return <code>true</code> if any element in this tree overlaps the
     * given interval; otherwise <code>false</code>
     */
    public boolean overlaps(Interval i);
    
    /**
     * Returns the number of elements in this tree that overlap the given
     * interval.
     * <p>
     * This method simply iterates over all overlappers and increments an
     * internal count. If you then need to perform some operation on these
     * intervals, it will be more efficient to retrieve an iterator over them
     * with {@link #overlappers(Interval)}.
     * 
     * @param i - the interval to check for overlap
     * @return the number of overlapping intervals
     */
    public int numOverlappers(Interval i);
    
    /**
     * Returns an iterator over the least elements that overlap the given
     * interval.
     *
     * @param i - the interval to check for overlap
     * @return an iterator over the least (or smallest) elements that overlap
     */
    public Iterator<T> minimumOverlappers(Interval i);
    
    /**
     * Returns an iterator over the elements in the node following the given
     * element's node.
     * <p>
     * If the passed interval corresponds with the last node in this tree, this
     * method returns an empty iterator.
     * <p>
     * If this tree does not contain a node that corresponds to the given
     * interval, this method returns an empty iterator.
     * 
     * @param i - the interval to check
     * @return an iterator over the following intervals
     */
    public Iterator<T> successors(Interval i);
    
    /**
     * Returns an iterator over the elements in the node preceding the given
     * element's node.
     * <p>
     * If the passed interval corresponds with the first node in this tree, this
     * method returns an empty iterator.
     * <p>
     * If this tree does not contain a node that corresponds to the given
     * interval, this method returns an empty iterator.
     * 
     * @param i - the interval to check
     * @return an iterator over the preceding intervals
     */
    public Iterator<T> predecessors(Interval i);
    
    /**
     * Removes all of the minimum elements from this tree.
     * <p>
     * If the tree does not contain any minimum elements (that is, if the tree
     * is empty), this method does nothing.
     * 
     * @return <code>true</code> if elements were removed and the tree changed;
     * <code>false</code> if the tree was empty and no elements were removed
     */
    public boolean removeMinima();
    
    /**
     * Removes all of the maximum elements from this tree.
     * <p>
     * If the tree does not contain any maximum elements (that is, if the tree
     * is empty), this method does nothing.
     * 
     * @return <code>true</code> if elements were removed and the tree changed;
     * <code>false</code> if the tree was empty and no elements were removed
     */
    public boolean removeMaxima();
    
    /**
     * Removes all elements that overlap the given interval from this tree.
     * <p>
     * If no elements overlap the interval, this method does nothing.
     * 
     * @param i - the interval to check for overlap
     * @return <code>true</code> if elements were removed and the tree changed;
     * <code>false</code> if there were no overlappers and no elements were
     * removed
     */
    public boolean removeOverlappers(Interval i);
    
}
