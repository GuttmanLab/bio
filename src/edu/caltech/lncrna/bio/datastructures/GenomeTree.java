package edu.caltech.lncrna.bio.datastructures;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.utils.FilteredIterator;

/**
 * This class represents a tree structure suitable for storing annotations
 * ({@link Annotated} objects) across an entire genome, e.g., the annotations
 * from a BED file or a collection of reads from a BAM file.
 */
public final class GenomeTree<T extends Annotated> implements Collection<T> {

    private final Map<String, IntervalTree<T>> chroms;
    
    /**
     * Class constructor.
     * <p>
     * Constructs an empty tree.
     */
    public GenomeTree() {
        chroms = new HashMap<>();
    }
    
    @Override
    public boolean isEmpty() {
        return chroms.isEmpty() || size() == 0;
    }
    
    @Override
    public int size() {
        return chroms.values()
                     .stream()
                     .mapToInt(IntervalTree::size)
                     .sum();
    }
    
    @Override
    public boolean contains(Object o) {
        if (o instanceof Annotated) {
            String ref = ((Annotated) o).getReferenceName();
            IntervalTree<T> chromTree = chroms.get(ref);
            if (chromTree == null) {
                return false;
            } else {
                return chromTree.contains(o);
            }
        }
        return false;
    }
    
    @Override
    public boolean containsAll(Collection<?> elements) {
        for (Object element : elements) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean add(T a) {
        return chroms.computeIfAbsent(a.getReferenceName(), 
                t -> new DegenerateIntervalTree<>()).add(a);
    }
    
    @Override
    public boolean addAll(Collection<? extends T> elements) {
        boolean rtrn = false;
        for (T element : elements) {
            rtrn = rtrn || this.add(element);
        }
        return rtrn;
    }
    
    @Override
    public boolean remove(Object o) {
        if (o instanceof Annotated) {
            String ref = ((Annotated) o).getReferenceName();
            IntervalTree<T> chromTree = chroms.get(ref);
            if (chromTree == null) {
                return false;
            } else {
                return chromTree.remove(o);
            }
        }
        return false;
    }
    
    @Override
    public boolean removeAll(Collection<?> elements) {
        boolean rtrn = false;
        for (Object element : elements) {
            rtrn = rtrn || this.remove(element);
        }
        return rtrn;
    }
    
    @Override
    public void clear() {
        chroms.clear();
    }
    
    @Override
    public Object[] toArray() {
        Object[] rtrn = new Object[size()];
        Iterator<T> elements = iterator();
        int idx = 0;
        while (idx < size()) rtrn[idx] = elements.next();
        return rtrn;
    }
    
    /**
     * Returns an iterator over all elements in this tree that overlap an
     * {@link Annotated} object.
     * <p>
     * Annotations which "overlap" only in intronic regions are not considered
     * to overlap each other, and will not be returned by this iterator. If
     * you instead want to check if two annotation bodies overlap (that is, if
     * they overlap without considering the distinction between introns and
     * exons) see {@link #bodyOverlappers(Annotated)}.
     * 
     * @param a - the annotation to check for overlap
     * @return an iterator over all overlapping elements
     */
    public Iterator<T> overlappers(Annotated a) {
        IntervalTree<T> tree = chroms.get(a.getReferenceName());
        
        if (tree == null) {
            return Collections.emptyIterator();
        } else {
            return new FilteredIterator<T>(tree.overlappers(a),
                    o -> a.getReferenceName().equals(o.getReferenceName())
                         && o.overlaps(a));
        }
    }
    
    /**
     * Returns an iterator over all elements in this tree that overlap the
     * an {@link Annotated} object.
     * <p>
     * This method checks the entire body of each annotation (that is, it
     * considers each annotation as a contiguous region with no introns) when
     * determining overlap. If you wish to consider introns, see {@link
     * #overlappers}.
     *  
     * @param a - the annotation to check for overlap
     * @return an iterator over all overlapping elements
     */
    public Iterator<T> bodyOverlappers(Annotated a) {
        IntervalTree<T> tree = chroms.get(a.getReferenceName());
        
        if (tree == null) {
            return Collections.emptyIterator();
        } else {
            return tree.overlappers(a);
        }
    }
    
    /**
     * Returns the number of elements that overlap an {@link Annotated} object.
     * <p>
     * This method simply iterates over all overlappers and increments an
     * internal count. If you then need to perform some operation on these
     * annotations, it will be more efficient to retrieve an iterator over them
     * with {@link #overlappers(Annotation)}.
     * 
     * @param i - the annotation to check for overlap
     * @return the number of overlapping elements
     */
    public int numOverlappers(Annotated a) {
        Iterator<T> overlappers = overlappers(a);
        int count = 0;
        while (overlappers.hasNext()) {
            overlappers.next();
            count++;
        }
        return count;
    }
    
    /**
     * Returns whether any element in this tree overlaps the given
     * {@link Annotated} object.
     * 
     * @param a - the annotation to check for overlap
     * @return <code>true</code> if any element in this tree overlaps the
     * given annotation; otherwise <code>false</code>
     */
    public boolean overlaps(Annotated a) {
        return overlappers(a).hasNext();
    }
    
    /**
     * Returns whether any element in this tree overlaps the given
     * {@link Annotated} object.
     * 
     * @param a - the annotation to check for overlap
     * @return <code>true</code> if any element in this tree overlaps the
     * given annotation; otherwise <code>false</code>
     */
    public boolean bodyOverlaps(Annotated a) {
        return bodyOverlappers(a).hasNext();
    }

    @Override
    public Iterator<T> iterator() {
        return new TreeIterator(chroms);
    }
    
    /**
     * An iterator over the chromosomal interval-trees that make up this
     * genome tree.
     */
    private final class ChromosomeIterator
    implements Iterator<IntervalTree<T>> {

        private Iterator<IntervalTree<T>> iter;
        private IntervalTree<T> next;
        
        private ChromosomeIterator(
                Map<String, IntervalTree<T>> chromMap) {

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
        public IntervalTree<T> next() {
            IntervalTree<T> rtrn = next;
            findNext();
            return rtrn;
        }
    }
    
    /**
     * An iterator over the elements of this genome tree.
     */
    private final class TreeIterator implements Iterator<T> {

        private final ChromosomeIterator chromIter;
        private Iterator<T> chromElements;
        
        private TreeIterator(Map<String, IntervalTree<T>> chromMap) {
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

    @Override
    public <U> U[] toArray(U[] a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}