package edu.caltech.lncrna.bio.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import edu.caltech.lncrna.bio.datastructures.SimpleIntervalTree;

/**
 * This class represents an <code>Iterator</code> that returns windows over an
 * annotation file such as a BAM file or BED file.
 * <p>
 * A <code>WindowIterator</code> streams through an annotation file. As it
 * encounters records in new parts of the reference, it
 * <li>creates windows to accommodate these records and populates them accordingly
 * <li>recognizes when previously existing windows can no longer have any
 * records assigned to them and offers them to be returned by iteration
 * <p>
 * The annotation file must be sorted by reference and start position.
 * Currently, the code does not check this.
 * @param <T> - the type of annotation to iterate over
 */
public final class WindowIterator<T extends Annotated>
implements Iterator<Populated<T>> {

    private SimpleIntervalTree<Populated<T>> windows;
    private Iterator<T> underlyingIterator;
    private Iterator<Populated<T>> fullyPopulatedWindows;
    private int windowLength;
    private int stepSize;
    
    private Populated<T> nextWindow;
    
    public WindowIterator(Iterator<T> iter, int windowLength,
            int stepSize) {
        
        windows = new SimpleIntervalTree<>();
        underlyingIterator = iter;
        fullyPopulatedWindows = Collections.emptyIterator();
        this.windowLength = windowLength;
        this.stepSize = stepSize;
        nextWindow = findNextWindow();
    }
    
    private Populated<T> findNextWindow() {

        if (fullyPopulatedWindows.hasNext()) {
            return fullyPopulatedWindows.next();
        }
        
        findMoreFullyPopulatedWindows();

        if (fullyPopulatedWindows.hasNext()) {
            return fullyPopulatedWindows.next();
        }
        
        return null;
    }
    
    private void findMoreFullyPopulatedWindows() {
        while (!fullyPopulatedWindows.hasNext() && underlyingIterator.hasNext()) {
            T read = underlyingIterator.next();
            addAnnotationToWindows(read);
            fullyPopulatedWindows = removeFullyPopulatedWindowsFromTree(read);
        }
    }
    
    private Iterator<Populated<T>> removeFullyPopulatedWindowsFromTree(T fragment) {

        // Three cases:
        // 1) Tree is already empty. Return empty iterator.

        if (!windows.minimum().isPresent()) {
            return Collections.emptyIterator();
        }
        
        // 2) We've moved to a different reference. All windows in the current
        //    tree must be fully populated. Return an iterator over them after
        //    emptying the tree.
        
        Optional<Populated<T>> minWindow = windows.minimum();
        
        if (!minWindow.get().getReferenceName().equals(fragment.getReferenceName())) {
            Iterator<Populated<T>> rtrn = windows.iterator();
            windows = new SimpleIntervalTree<>();
            return rtrn;
        }

        // 3) We're on the same reference. Start at the minimum element in the
        //    tree and remove it until you reach one beyond the start of the
        //    fragment.
        
        List<Populated<T>> fullyPopulated = new ArrayList<>();
        
        while (minWindow.isPresent() && minWindow.get().getEnd() < fragment.getStart()) {
            fullyPopulated.add(minWindow.get());
            windows.removeMinimum();
            minWindow = windows.minimum();
        }

        return fullyPopulated.iterator();
    }
    
    private void addAnnotationToWindows(T annotation) {

        Iterator<Annotated> blocks = annotation.getBlockIterator();

        while (blocks.hasNext()) {
            Annotated block = blocks.next();
            int start = Math.max(0, roundUp(block.getStart() - windowLength));
            int end = block.getEnd();
            for (int i = start; i < end; i += stepSize) {
                
                Populated<T> matchingEmptyWindow = new Window<T>(
                        annotation.getReferenceName(), i,
                        i + windowLength, Strand.BOTH);
                
                Populated<T> window = windows.popSameBounds(matchingEmptyWindow)
                                             .orElse(matchingEmptyWindow);
                
                // Check for overlap in case window falls in an insert/intron
                
                // Overlap. Add annotation to the window and re-insert the
                // window.
                if (window.overlaps(annotation)) {
                    window.add(annotation);
                    windows.add(window);
                
                // No overlap. If the window is populated, it overlaps some
                // other read, so we need to keep it. Otherwise, it doesn't
                // overlap anything, and we can discard it.
                } else if (window.getPopulationSize() > 0) {
                    windows.add(window);
                }
            }
        }
    }
    
    // So that all windows start on the expected "grid lines"
    private int roundUp(int windowStart) {

        if (stepSize == 1) {
            return windowStart;
        }
        
        return (windowStart + stepSize - 1) / stepSize * stepSize;
    }
    
    @Override
    public boolean hasNext() {
        return nextWindow != null;
    }

    @Override
    public Populated<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("WindowIterator.next() called " +
                    "with no next element.");
        }
        Populated<T> rtrn = nextWindow;
        nextWindow = findNextWindow();
        return rtrn;
    }
}
