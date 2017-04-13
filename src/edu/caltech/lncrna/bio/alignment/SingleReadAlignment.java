package edu.caltech.lncrna.bio.alignment;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Annotation.AnnotationBuilder;
import edu.caltech.lncrna.bio.annotation.Strand;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMRecord;

/**
 * This class represents a single read from a SAM file for which a valid
 * alignment has been found.
 */
public final class SingleReadAlignment extends SamRecordImpl implements Alignment {

    private final Annotated annot;
    
    /**
     * Constructs an instance of a <code>SingleReadAligment</code> from an
     * htsjdk <code>SAMRecord</code> object.
     * @throws IllegalArgumentException if the input SAM record is not aligned.
     */
    public SingleReadAlignment(SAMRecord samRecord) {
        super(samRecord);
        
        if (!isMapped()) {
            throw new IllegalArgumentException("Attempted to construct " +
                    "SingleReadAlignment from unmapped SAMRecord.");
        }
        
        String ref = samRecord.getReferenceName();

        int start = samRecord.getAlignmentStart();
        Strand strand = isOnReverseStrand() ? Strand.NEGATIVE : Strand.POSITIVE;
        annot = (new AnnotationBuilder())
                .addAnnotationFromCigar(samRecord.getCigar(), ref, start, strand)
                .build();
        assert annot.getEnd() == samRecord.getAlignmentEnd() + 1:
            "BlockedAnnotation is not consistant with SAMRecord.";
    }

    @Override
    public String getReferenceName() {
        return annot.getReferenceName();
    }

    @Override
    public int getStart() {
        return annot.getStart();
    }

    @Override
    public int getEnd() {
        return annot.getEnd();
    }
    
    @Override
    public int getFivePrimePosition() {
        return annot.getFivePrimePosition();
    }
    
    @Override
    public int getThreePrimePosition() {
        return annot.getThreePrimePosition();
    }

    @Override
    public int getSize() {
        return annot.getSize();
    }

    @Override
    public int getSpan() {
        return annot.getSpan();
    }

    @Override
    public Strand getStrand() {
        return annot.getStrand();
    }
    
    @Override
    public int[] getBlockBoundaries() {
        return annot.getBlockBoundaries();
    }

    @Override
    public int getNumberOfBlocks() {
        return annot.getNumberOfBlocks();
    }

    @Override
    public Iterator<Annotated> iterator() {
        return getBlockIterator();
    }
    
    @Override
    public Iterator<Annotated> getBlockIterator() {
        return annot.getBlockIterator();
    }

    @Override
    public Stream<Annotated> getBlockStream() {
        return annot.getBlockStream();
    }

    @Override
    public boolean overlaps(Annotated other) {
        return annot.overlaps(other);
    }

    @Override
    public boolean isAdjacentTo(Annotated other) {
        return annot.isAdjacentTo(other);
    }
    
    @Override
    public boolean isUpstreamOf(Annotated other) {
        return annot.isUpstreamOf(other);
    }

    @Override
    public boolean isDownstreamOf(Annotated other) {
        return annot.isDownstreamOf(other);
    }

    @Override
    public Annotated getBody() {
        return annot.getBody();
    }

    @Override
    public Optional<Annotated> minus(Annotated other) {
        return annot.minus(other);
    }

    @Override
    public Optional<Annotated> intersect(Annotated other) {
        return annot.intersect(other);
    }

    @Override
    public boolean contains(Annotated other) {
        return annot.contains(other);
    }
    
    @Override
    public Optional<Annotated> getIntrons() {
        return annot.getIntrons();
    }

    @Override
    public Iterator<Annotated> getIntronIterator() {
        return annot.getIntronIterator();
    }

    @Override
    public Stream<Annotated> getIntronStream() {
        return annot.getIntronStream();
    }
    
    public String getCigarString() {
        return samRecord.getCigarString();
    }

    @Override
    public void writeTo(SAMFileWriter writer) {
        writer.addAlignment(samRecord);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof SingleReadAlignment)) {
            return false;
        }
        
        SingleReadAlignment other = (SingleReadAlignment) o;
        
        return samRecord.equals(other.samRecord) &&
               annot.equals(other.annot);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + samRecord.hashCode();
        hashCode = 37 * hashCode + annot.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString() {
        return annot.toString();
    }
    
}