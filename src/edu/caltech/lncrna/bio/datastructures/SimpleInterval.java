package edu.caltech.lncrna.bio.datastructures;

/**
 * A very simple implementation of an {@link Interval} when one is needed
 * the code of various <code>Tree</code> classes.
 */
class SimpleInterval implements Interval {

    private final int start;
    private final int end;
    
    SimpleInterval(int start, int end) {
        assert start < end;
        this.start = start;
        this.end = end;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }
}
