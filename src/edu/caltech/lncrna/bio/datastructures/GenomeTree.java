package edu.caltech.lncrna.bio.datastructures;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.utils.FilteredIterator;

/**
 * This class represents a tree structure suitable for storing annotations
 * (<code>Annotated</code> objects) across an entire genome, e.g., the records
 * from a BED file or a collection of reads from a BAM file.
 */
public class GenomeTree<T extends Annotated> implements Iterable<T> {

    private Map<String, IntervalTreeDuplicateBounds<T>> chroms;
    
    /**
     * Class constructor.
     * <p>
     * Constructs an empty tree.
     */
    public GenomeTree() {
        chroms = new HashMap<>();
    }
    
    /**
     * Whether or not this has any elements stored in it.
     */
    public boolean isEmpty() {
        return chroms.isEmpty() || getSize() == 0;
    }
    
    /**
     * Gets the total number of elements in this.
     */
    public int getSize() {
        return chroms.values()
                     .stream()
                     .mapToInt(IntervalTreeDuplicateBounds::size)
                     .sum();
    }
    
    /**
     * Inserts an <code>Annotated</code> object into this.
     * @param a - the <code>Annotated</code> object to insert
     * @return <code>true</code> if the addition resulted in a change to the
     * tree; otherwise (if the value was already present, for example),
     * <code>false</code>
     */
    public boolean insert(T a) {
        return chroms.computeIfAbsent(a.getReferenceName(), 
                t -> new IntervalTreeDuplicateBounds<>()).insert(a);
    }
    
    /**
     * Gets an <code>Iterator</code> over all elements in this which overlap
     * a given <code>Annotated</code> object.
     * @param a - the given <code>Annotated</code> object
     */
    public Iterator<T> getOverlappers(Annotated a) {
        IntervalTreeDuplicateBounds<T> tree = chroms.get(a.getReferenceName());
        
        if (tree == null) {
            return Collections.emptyIterator();
        } else {
            return new FilteredIterator<T>(tree.overlappers(a),
                    o -> a.getReferenceName().equals(o.getReferenceName())
                         && o.overlaps(a));
        }
    }
    
    /**
     * Gets an <code>Iterator</code> over all elements whose hulls overlap a
     * given <code>Annotated</code> object.
     * @param a - the given <code>Annotated</code> object
     */
    public Iterator<T> getGeneBodyOverlappers(Annotated a) {
        IntervalTreeDuplicateBounds<T> tree = chroms.get(a.getReferenceName());
        
        if (tree == null) {
            return Collections.emptyIterator();
        } else {
            return tree.overlappers(a);
        }
    }
    
    /**
     * Get the number of elements that overlap the given <code>Annotated</code>
     * object.
     * 
     * This method will iterate over all of the elements, so if you need to
     * count them in addition to performing some other operation, you probably
     * want to work with the iterator directly by calling
     * <code>getOverlappers</code>
     * @param a - the given <code>Annotated</code> object
     */
    public int getNumOverlappers(Annotated a) {
        Iterator<T> overlappers = getOverlappers(a);
        int count = 0;
        while (overlappers.hasNext()) {
            overlappers.next();
            count++;
        }
        return count;
    }
    
    /**
     * Whether a given <code>Annotated</code> overlaps any element in this.
     * @param a - the given <code>Annotated</code> object
     */
    public boolean overlaps(Annotated a) {
        return getOverlappers(a).hasNext();
    }
    
    public boolean anyGeneBodyOverlaps(Annotated a) {
        return getGeneBodyOverlappers(a).hasNext();
    }


    @Override
    public Iterator<T> iterator() {
        return new TreeIterator(chroms);
    }
    
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    
    private class ChromosomeIterator implements Iterator<IntervalTreeDuplicateBounds<T>> {

        private Iterator<IntervalTreeDuplicateBounds<T>> iter;
        private IntervalTreeDuplicateBounds<T> next;
        
        private ChromosomeIterator(Map<String, IntervalTreeDuplicateBounds<T>> chromMap) {
            iter = chromMap.values().iterator();
            findNext();
        }
        
        private void findNext() {
            next = null;
            while (next == null && iter.hasNext()) {
                next = iter.next();
                if (next.isEmpty()) {
                    next = null;
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public IntervalTreeDuplicateBounds<T> next() {
            IntervalTreeDuplicateBounds<T> rtrn = next;
            findNext();
            return rtrn;
        }
    }
    
    private class TreeIterator implements Iterator<T> {

        private final ChromosomeIterator chromIter;
        private Iterator<T> chromElements;
        
        private TreeIterator(Map<String, IntervalTreeDuplicateBounds<T>> chromMap) {
            chromIter = new ChromosomeIterator(chromMap);
            if (chromIter.hasNext()) {
                chromElements = chromIter.next().iterator();
            } else {
                chromElements = Collections.emptyIterator();
            }
        }

        @Override
        public boolean hasNext() {
            return chromElements.hasNext() || chromIter.hasNext();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else if (chromElements.hasNext()) {
                return chromElements.next();
            } else {
                assert chromIter.hasNext() :
                    "Unexpectedly ran out of chromosomes.";
                chromElements = chromIter.next().iterator();
                return chromElements.next();
            }
        }
    }
}