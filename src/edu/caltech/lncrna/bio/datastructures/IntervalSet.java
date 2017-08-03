package edu.caltech.lncrna.bio.datastructures;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class represents a <code>Set</code> of {@link Interval} objects.
 * <p>
 * All of the intervals in this object should share the same start and end
 * coordinates. Since these coordinates are defined, <code>IntervalSet</code>
 * is also an <code>Interval</code>.
 * <p>
 * This class is exclusively used to store data in the nodes of
 * {@link DegenerateIntervalTree}.
 *
 * @param <T> - the type of interval stored in this set
 */
public final class IntervalSet<T extends Interval>
implements Interval, Set<T> {

    private final int start;
    private final int end;
    private Set<T> internal;
    
    /**
     * Class constructor.
     * <p>
     * Construct an empty <code>IntervalSet</code> object. No intervals can be
     * added to this instance. (This is used solely for making an empty
     * sentinel node in certain types of {@link RedBlackIntervalTree}.
     */
    public IntervalSet() {
        this.start = 0;
        this.end = 0;
        internal = Collections.emptySet();
    }

    /**
     * Class constructor.
     * <p>
     * Construct an empty <code>IntervalSet</code> object with the given
     * <code>start</code> and <code>end</code> values.
     * 
     * @param start - the start coordinate
     * @param end - the end coordinate
     */
    public IntervalSet(int start, int end) {
        this.start = start;
        this.end = end;
        internal = new HashSet<>();
    }
    
    /**
     * Class constructor.
     * <p>
     * Construct an <code>IntervalSet</code> object containing the single given
     * interval.
     * 
     * @param t - the interval in this set
     */
    public IntervalSet(T t) {
        this.start = t.getStart();
        this.end = t.getEnd();
        internal = new HashSet<>();
        internal.add(t);
    }
    
    @Override
    public int size() {
        return internal.size();
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return internal.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return internal.iterator();
    }

    @Override
    public Object[] toArray() {
        return internal.toArray();
    }

    @Override
    public <U> U[] toArray(U[] a) {
        return internal.toArray(a);
    }

    @Override
    public boolean add(T e) {
        assert e.getStart() == this.start;
        assert e.getEnd() == this.end;
        return internal.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return internal.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return internal.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T element : c) {
            changed = changed || add(element);
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return internal.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return internal.removeAll(c);
    }

    @Override
    public void clear() {
        internal.clear();
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof IntervalSet<?>)) {
            return false;
        }

        IntervalSet<?> o = (IntervalSet<?>) other;
        
        return start == o.start &&
               end == o.end &&
               internal.equals(o.internal);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + start;
        hashCode = 37 * hashCode + end;
        hashCode = 37 * internal.hashCode();
        return hashCode;
    }
}
