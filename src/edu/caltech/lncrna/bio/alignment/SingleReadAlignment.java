package edu.caltech.lncrna.bio.alignment;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Block;
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
        // SAM records are one-based inclusive
        int start = samRecord.getAlignmentStart() - 1;
        int end = samRecord.getAlignmentEnd();
        Strand strand = isOnReverseStrand() ? Strand.NEGATIVE : Strand.POSITIVE;
        annot = new Block(ref, start, end, strand);
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
    public int getNumberOfBlocks() {
        return annot.getNumberOfBlocks();
    }

    @Override
    public Iterator<Block> getBlockIterator() {
        return annot.getBlockIterator();
    }

    @Override
    public Stream<Block> getBlockStream() {
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
    public Annotated getHull() {
        return annot.getHull();
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
    public int getPositionRelativeToFivePrime(int absolutePosition) {
        return annot.getPositionRelativeToFivePrime(absolutePosition);
    }
    
    public String getCigarString() {
        return samRecord.getCigarString();
    }

    @Override
    public void writeTo(SAMFileWriter writer) {
        writer.addAlignment(samRecord);
    }
}