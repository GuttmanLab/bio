package edu.caltech.lncrna.bio.datastructures;

/**
 * A very simple implementation of an {@link Interval} when one is needed
 * the code of various tree classes.
 */
public class SimpleInterval implements Interval {

    private final int start;
    private final int end;
    
    /**
     * Class constructor.
     * <p>
     * Constructs an interval with the given start and end.
     * 
     * @param start - the start
     * @param end - the end
     */
    public SimpleInterval(int start, int end) {
        assert start < end;
        this.start = start;
        this.end = end;
    }
    
    /**
     * Class constructor.
     * <p>
     * Constructs an interval with the same start and end values as teh given
     * interval.
     * 
     * @param i - the template interval
     */
    public SimpleInterval(Interval i) {
        this.start = i.getStart();
        this.end = i.getEnd();
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
    public String toString() {
        return "[" + start + ", " + end + ")";
    }
    
    @Override
    public boolean equals(Object other) {
        // No need for null check. The instanceof operator returns false
        // if (other == null).
        if (!(other instanceof SimpleInterval)) {
            return false;
        }

        return start == ((SimpleInterval) other).start &&
                end == ((SimpleInterval) other).end;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }
}
