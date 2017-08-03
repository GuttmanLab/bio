package edu.caltech.lncrna.bio.datastructures;

/**
 * An <code>Interval</code> is any object which can be represented as a
 * closed-open, or [), interval on the integer number line. 
 */
public interface Interval extends Comparable<Interval> {

    /**
     * Returns the start coordinate of this interval.
     * 
     * @return the start coordinate of this interval
     */
    int getStart();

    /**
     * Returns the end coordinate of this interval.
     * <p>
     * An interval is closed-open. It does not include the returned point.
     * 
     * @return the end coordinate of this interval
     */
    int getEnd();

    default int length() {
        return getEnd() - getStart();
    }

    /**
     * Returns whether this interval is adjacent to another.
     * <p>
     * Two intervals are adjacent if either one ends where the other starts.
     * 
     * @param interval - the interval to compare this one to
     * @return <code>true</code> if the intervals are adjacent; otherwise
     * <code>false</code>
     */
    default boolean isAdjacent(Interval other) {
        return getStart() == other.getEnd() || getEnd() == other.getStart();
    }
    
    /**
     * Returns whether this interval overlaps another.
     * 
     * This method assumes that intervals are contiguous, i.e., there are no
     * breaks or gaps in them.
     * 
     * @param o - the interval to compare this one to
     * @return <code>true</code> if the intervals overlap; otherwise
     * <code>false</code>
     */
    default boolean overlaps(Interval o) {
        return getEnd() > o.getStart() && o.getEnd() > getStart();
    }
    
    /**
     * Compares this interval with another.
     * <p>
     * Ordering of intervals is done first by start coordinate, then by end
     * coordinate.
     * 
     * @param o - the interval to compare this one to
     * @return -1 if this interval is less than the other; 1 if greater
     * than; 0 if equal
     */
    default int compareTo(Interval o) {
        if (getStart() > o.getStart()) {
            return 1;
        } else if (getStart() < o.getStart()) {
            return -1;
        } else if (getEnd() > o.getEnd()) {
            return 1;
        } else if (getEnd() < o.getEnd()) {
            return -1;
        } else {
            return 0;
        }
    }
}