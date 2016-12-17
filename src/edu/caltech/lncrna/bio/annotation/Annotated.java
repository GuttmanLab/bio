package edu.caltech.lncrna.bio.annotation;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.datastructures.Interval;

/**
 * Objects that implement the <code>Annotated</code> interface have a defined
 * location on a reference genome.
 * <p>
 * At a minimum, an <code>Annotated</code> object has
 * <ul>
 * <li>a reference to which it aligns</li>
 * <li>a start coordinate</li>
 * <li>an end coordinate</li>
 * <li>an orientation or strandedness</li>
 * </ul>
 */
public interface Annotated extends Interval {

    /**
     * Gets the name of the reference that this annotation belongs to.
     * <p>
     * The reference name is typically a chromosome such as "chr1" or
     * "chrX".
     */
    public String getReferenceName();
    
    /**
     * Gets the start coordinate of this.
     * <p>
     * Annotation coordinates are zero-based, closed-open. 
     */
    @Override
    public int getStart();
    
    /**
     * Gets the end coordinate of this.
     * <p>
     * Annotation coordinates are zero-based, closed-open.
     */
    @Override
    public int getEnd();
    
    /**
     * Gets the size of this.
     * <p>
     * The size is the sum of the sizes of this objects's
     * blocks. In typical usage where exons are represented as blocks and
     * introns are implied as the gaps between the blocks, this method would
     * return the total exonic size.
     */
    public int getSize();
    
    /**
     * Gets the span of this.
     * <p>
     * The span is simply the distance from the 5'-end to the 3'-end of this
     * object. This method will include introns or gaps between blocks when
     * calculating the span.
     */
    public int getSpan();
    
    /**
     * Gets the {@link Strand} of this.
     */
    public Strand getStrand();

    /**
     * Gets the number of blocks making up this annotation.
     */
    public int getNumberOfBlocks();
    
    /**
     * Gets an <code>Iterator</code> over the blocks making up this annotation.
     */
    public Iterator<Block> getBlockIterator();
    
    /**
     * Gets the blocks making up this annotation as a stream.
     */
    public Stream<Block> getBlockStream();
    
    /**
     * Whether this annotation overlaps another annotation.
     * @param other - the other annotation
     */
    public boolean overlaps(Annotated other);
    
    /**
     * Whether this annotation is adjacent to another annotation.
     * <p>
     * Two annotations are considered adjacent if the two do not overlap, and
     * one begins where the other ends.
     * @param other - the other annotation
     */
    public boolean isAdjacentTo(Annotated other);
    
    /**
     * Returns the hull of this annotation.
     * <p>
     * The "hull" is the minimal contiguous annotation that contains all of
     * this annotation's exons. In other words, the hull is what one gets by
     * "filling in" all of this annotations introns.
     */
    public Annotated getHull();
    
    /**
     * Returns the difference between this annotation and another.
     * <p>
     * This object is the minuend. The object this method takes as an argument
     * is the subtrahend.
     * <p>
     * If the two annotations do not overlap, this method will simply return
     * this annotation, wrapped in an <code>Optional</code>.
     * <p>
     * This method takes strand into account when calculating the
     * difference. If two annotations are on opposite strands (positive and
     * negative), they do not overlap.
     * <p>
     * If the subtrahend completely contains the minuend, this method returns
     * an empty <code>Optional</code> to represent an "empty" annotation.
     * @param other - the annotation to be subtracted from this annotation
     * @return an <code>Annotated</code> object representing the difference
     * between these two annotations, wrapped in an <code>Optional</code> if it
     * exists; an empty <code>Optional</code> otherwise.
     */
    public Optional<Annotated> minus(Annotated other); // TODO test strandedness and clarify in doc
    
    /**
     * Returns the intersection of this annotation with another.
     * <p>
     * If the two annotations do not overlap, this method returns an empty
     * <code>Optional</code>
     * @param other - the other annotation
     */
    public Optional<Annotated> intersect(Annotated other);
    
    /**
     * If this annotation fully contains another annotation.
     * <p>
     * This method takes orientation into consideration. A positive annotation
     * cannot contain a negative annotation, regardless of their coordinates,
     * because they are on different strands.
     * @param other - the other annotation
     */
    public boolean contains(Annotated other);
    
    /**
     * Calculates the given reference coordinate relative to the five-prime
     * end of this annotation.
     * <p>
     * This method considers orientation when determining which end is the
     * five-prime end.
     * @param absolutePosition - the reference coordinate.
     */
    public int getPositionRelativeToFivePrime(int absolutePosition);
}