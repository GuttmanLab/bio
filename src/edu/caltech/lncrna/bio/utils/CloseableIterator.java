package edu.caltech.lncrna.bio.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This interface adds <code>AutoCloseable</code> behavior to an
 * <code>Iterator</code>.
 * @param <T> - the type of object this iterator iterates over
 */
public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {

    public void close();
    
    default public List<T> toList() {
        final List<T> list = new ArrayList<>();
        while (hasNext()) {
            list.add(next());
        }
        close();
        return list;
    }
    
    default public Stream<T> stream() {
        final Spliterator<T> s = Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED);
        return StreamSupport.stream(s, false).onClose(this::close);
    }
}