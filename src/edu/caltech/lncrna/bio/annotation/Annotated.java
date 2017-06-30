package edu.caltech.lncrna.bio.annotation;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.datastructures.Interval;
import edu.caltech.lncrna.bio.io.FormattableWithFields;

/**
 * Defines the behavior of an object that has a location on a reference genome.
 * <p>
 * At a minimum, an <code>Annotated</code> object has
 * <ul>
 * <li>a reference to which it aligns</li>
 * <li>a start coordinate</li>
 * <li>an end coordinate</li>
 * <li>an orientation or strandedness</li>
 * </ul>
 */
public interface Annotated extends FormattableWithFields, Interval,
Iterable<Annotated> {

    /**
     * Returns the name of the reference that this annotation belongs to.
     * <p>
     * The reference name is typically a chromosome such as "chr1" or
     * "chrX".
     * 
     * @return the reference name of this annotation
     */
    public String getReferenceName();
    
    /**
     * The start coordinate of an annotation corresponds to the end of this
     * annotation closest to the start of the reference. To get the 5'-end or
     * the 3'-end of this annotation, use the {@link #getFivePrimePosition()}
     * method or the {@link #getThreePrimePosition()} method.
     * <p>
     * Annotation coordinates are zero-based, closed-open.
     */
    @Override
    public int getStart();
    
    /**
     * The end coordinate of an annotation corresponds to the end of this
     * annotation closest to the end of the reference. To get the 5'-end or
     * the 3'-end of this annotation, use the {@link #getFivePrimePosition()}
     * method or the {@link #getThreePrimePosition()} method.
     * <p>
     * Annotation coordinates are zero-based, closed-open.
     */
    @Override
    public int getEnd();

    public int getFivePrimePosition();
    
    public int getThreePrimePosition();
    
    /**
     * The size of an annotation is the sum of the sizes of the annotation's
     * blocks. In typical usage where exons are represented as blocks and
     * introns are implied as the gaps between the blocks, this method would
     * return the total exonic size.
     */
    public int getSize();
    
    /**
     * The span of an annotation is simply the distance from the 5'-end to the
     * 3'-end. This method will include introns or gaps between blocks when
     * calculating the span.
     */
    public int getSpan();

    public Strand getStrand();

    public int getNumberOfBlocks();
    
    /**
     * @return an <code>Iterator</code> over this annotation's blocks or exons
     */
    public Iterator<Annotated> getBlockIterator();
    
    /**
     * @return a <code>Stream</code> of this annotation's blocks or exons
     */
    public Stream<Annotated> getBlockStream();

    public boolean overlaps(Annotated other);
    
    /**
     * Two annotations are considered adjacent if the two do not overlap, and
     * one begins where the other ends.
     * 
     * @param other - the other annotation
     */
    public boolean isAdjacentTo(Annotated other);
    
    /**
     * An annotation's body is the minimal contiguous annotation that contains
     * all of the annotation's exons. In other words, the body is what one gets
     * by "filling in" all of the introns.
     */
    public Annotated getBody();
    
    /**
     * Returns the difference between this annotation and another.
     * <p>
     * This annotation is the minuend. The annotation this method takes as an
     * argument is the subtrahend.
     * <p>
     * If the two annotations do not overlap, this method will simply return
     * this annotation, wrapped in an <code>Optional</code>.
     * <p>
     * This method takes strand into account when calculating the
     * difference. If two annotations are on opposite strands (positive and
     * negative), they do not overlap.
     * <p>
     * If the subtrahend completely contains the minuend, this method returns
     * an empty <code>Optional</code> instance to represent an "empty"
     * annotation.
     * 
     * @param other - the annotation to be subtracted from this annotation
     * @return an <code>Annotated</code> instance representing the difference
     * between these two annotations, wrapped in an <code>Optional</code> if it
     * exists; an empty <code>Optional</code> instance otherwise.
     */
    public Optional<Annotated> minus(Annotated other);
    
    /**
     * Returns the intersection of this annotation with another.
     * <p>
     * Returns the intersection of this annotation with another, wrapped in an
     * <code>Optional</code>. If the two annotations do not overlap, this
     * method returns an empty <code>Optional</code>.
     * <p>
     * This method takes orientation into consideration. A positive annotation
     * does not intersect a negative annotation, regardless of their
     * coordinates, because they are on different strands.
     * 
     * @param other - the other annotation
     * @return the intersection of this annotation with another
     */
    public Optional<Annotated> intersect(Annotated other);
    
    /**
     * Returns <code>true</code> if this annotation fully contains another
     * annotation.
     * <p>
     * This method takes orientation into consideration. A positive annotation
     * does not contain a negative annotation, regardless of their coordinates,
     * because they are on different strands.
     * 
     * @param other - the other annotation
     * @returns <code>true</code> if this annotation fully contains another
     * annotation
     */
    public boolean contains(Annotated other);
    
    /**
     * Returns this introns of this annotation as a single
     * <code>Annotated</code> object, wrapped in an <code>Optional</code>.
     * <p>
     * If this annotation has no introns, an empty <code>Optional</code>
     * instance will be returned.
     * <p>
     * This method returns the introns as a single annotation. If you need to
     * handle the introns individually, see {@link #getIntronIterator()} or
     * {@link #getIntronStream()}.
     * 
     * @return the introns of this annotation
     */
    public Optional<Annotated> getIntrons();

    /**
     * @return an <code>Iterator</code> over the introns of this annotation
     */
    public Iterator<Annotated> getIntronIterator();
    
    /**
     * @return the introns of this annotation as a <code>Stream</code>
     */
    public Stream<Annotated> getIntronStream();
    
    public boolean isUpstreamOf(Annotated other);

    public boolean isDownstreamOf(Annotated other);

    /**
     * Returns a BED12 <code>String</code> representation of this annotation.
     * <p>
     * The returned string is terminated with a newline, and is suitable for
     * writing to a BED file.
     * <p>
     * BED12 is the standard BED format with all twelve fields.
     * 
     * @see https://genome.ucsc.edu/FAQ/FAQformat.html#format1
     * @return a BED12 representation of this annotation 
     */
    public String toFormattedBedString();

    /**
     * Returns a BED <code>String</code> representation of this annotation with
     * the given number of fields.
     * <p>
     * The returned string is terminated with a newline, as is suitable for
     * writing to a BED file.
     * 
     * @see https://genome.ucsc.edu/FAQ/FAQformat.html#format1
     * @param numFields - the desired number of fields
     * @return a BED representation of this annotation with
     * <code>numFields</code> fields
     */
    public String toFormattedBedString(int numFields);
    
    // TODO: Make this method private when Java 9 comes out
    public int[] getBlockBoundaries();
}
