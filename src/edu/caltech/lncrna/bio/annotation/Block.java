package edu.caltech.lncrna.bio.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * This class represents an <code>Annotation</code> block.
 * <p>
 * A <code>Block</code> is the simplest unit of an <code>Annotation</code>. The
 * <code>Block</code> is just a contiguous annotation, and can be thought of
 * as an exon. Annotations with intronic regions are made up of multiple
 * <code>Block</code>s, one for each exon.
 */
public class Block extends Annotation implements Annotated {

    /**
     * Constructs an instance of this class with the given characteristics.
     * @param ref - the name of the reference to which this belongs
     * @param start - the start coordinate of this
     * @param end - the end coordiante of this
     * @param strand - the orientation of this
     */
    public Block(String ref, int start, int end, Strand strand) {
        super(ref, start, end, strand);
    }
    
    /**
     * Constructs an instance of this class sharing the coordinates of the
     * given <code>Annotated</code> object, but with a different orientation.
     * <p>
     * This constructor is primarily used to change the orientation of another
     * annotation without mutating it.
     * @param annot - an annotation to serve as a template for this
     * @param strand - the orientation of this
     */
    public Block(Annotated annot, Strand strand) {
        super(annot, strand);
    }
    
    public Block(Annotated annot) {
        super(annot);
    }

    @Override
    public int getNumberOfBlocks() {
        return 1;
    }
    
    @Override
    public Iterator<Annotated> getBlockIterator() {
        List<Annotated> list = new ArrayList<>();
        list.add(this);
        return list.iterator();
    }

    @Override
    public Stream<Annotated> getBlockStream() {
        List<Annotated> list = new ArrayList<>();
        list.add(this);
        return list.stream();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof Block)) {
            return false;
        }
        
        Block other = (Block) o;
        
        return ref.equals(other.ref) &&
               start == other.start &&
               end == other.end &&
               strand.equals(other.strand);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + ref.hashCode();
        hashCode = 37 * hashCode + strand.hashCode();
        hashCode = 37 * hashCode + start;
        hashCode = 37 * hashCode + end;
        return hashCode;
    }

    @Override
    public Annotated getBody() {
        return this;
    }
    
    /**
     * Tiles this <code>Block</code> with sliding windows and returns an
     * <code>Iterator</code> over them.
     * @param windowSize - the length of each tiling window
     * @param stepSize - the step size taken when advancing to the next window
     * @return an <code>Iterator</code> over the tiling windows
     * @throws IllegalArgumentException if either the step size or the window
     * size are less than one.
     */
    public Iterator<Annotated> tile(int windowSize, int stepSize) {
        return new TilingBlockIterator(this, windowSize, stepSize);
    }
    
    private class TilingBlockIterator implements Iterator<Annotated> {

        private final int windowSize;
        private final int stepSize;
        private final Annotated underlyingBlock;
        private int currentPosition;
        private Annotated nextBlock;
        
        public TilingBlockIterator(Block block, int windowSize, int stepSize) {
            if (windowSize <= 0) {
                throw new IllegalArgumentException("Attemped to construct a " +
                        "TilingBlockIterator with an illegal window size: " +
                        windowSize + ". Window size must be greater than 0.");
            }
            if (stepSize <= 0) {
                throw new IllegalArgumentException("Attemped to construct a " +
                        "TilingBlockIterator with an illegal step size: " +
                        stepSize + ". Step size must be greater than 0.");
            }
            this.windowSize = windowSize;
            this.stepSize = stepSize;
            this.underlyingBlock = block;
            currentPosition = underlyingBlock.getStart();
            nextBlock = getNext();
        }
        
        @Override
        public boolean hasNext() {
            return nextWindowIsInBounds();
        }
        
        @Override
        public Annotated next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Annotated rtrn = nextBlock;
            nextBlock = getNext();
            return rtrn;
        }
        
        private boolean nextWindowIsInBounds() {
            return underlyingBlock.contains(nextBlock);
        }

        private Annotated getNext() {
            Annotated rtrn = new Block(underlyingBlock.getReferenceName(), currentPosition,
                                   currentPosition + windowSize, underlyingBlock.getStrand());
            currentPosition += stepSize;
            return rtrn;
        }
    }
}