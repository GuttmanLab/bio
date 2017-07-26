package edu.caltech.lncrna.bio.datastructures;

/**
 * An <code>Interval</code> is any object which can be represented as a
 * closed-open, or [), interval on the integer number line. 
 */
public interface Interval extends Comparable<Interval> {

    int getStart();

    /**
     * An <code>Interval</code> is closed-open. It does not include the
     * returned point.
     */
    int getEnd();

    default int length() {
        return getEnd() - getStart();
    }

    /**
     * Two intervals are adjacent if either one ends where the other starts.
     * 
     * @param interval - the interval to compare this one to
     * @return if this interval is adjacent to the specified interval.
     */
    default boolean isAdjacent(Interval other) {
        return getStart() == other.getEnd() || getEnd() == other.getStart();
    }
    
    /**
     * This method assumes that intervals are contiguous, i.e., there are no
     * breaks or gaps in them.
     * 
     * @param o - the interval to compare this one to
     * @return if this interval overlaps the specified interval
     */
    default boolean overlaps(Interval o) {
        return getEnd() > o.getStart() && o.getEnd() > getStart();
    }
    
    /**
     * Ordering of intervals is done first by start coordinate, then by end
     * coordinate.
     * 
     * @param o - the interval to compare this one to
     * @return -1 if this interval is less than <code>o</code>; 1 if greater
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