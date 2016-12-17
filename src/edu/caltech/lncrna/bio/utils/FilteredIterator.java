package edu.caltech.lncrna.bio.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

// TODO: Is this class necessary? Filtering can already be handled by streams.

/**
 * This class represents <code>Iterator</code>s that skip some of their underlying
 * elements if they do not pass an associated <code>Predicate</code>.
 * <p>
 * Any objects that the underlying iterator would return that do not pass the
 * predicate are skipped.
 * @param <T> - the type of object returned by this iterator
 */
public class FilteredIterator<T> implements Iterator<T> {
    private Iterator<? extends T> iterator;
    private Predicate<T> predicate;
    private T next;

    /**
     * Constructs a <code>FilteredIterator</code> by coupling the given
     * <code>Iterator</code> with a <code>Predicate</code>.
     * @param iterator - the iterator to couple
     * @param predicate - the predicate to couple
     */
    public FilteredIterator(Iterator<? extends T> iterator, Predicate<T> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;

        next = findNext();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        T rtrn = next;
        next = findNext();
        return rtrn;
    }

    private T findNext() {

        while (iterator.hasNext()) {
            T o = iterator.next();

            if (predicate.test(o)) {
                return o;
            }
        }
        
        return null;
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}