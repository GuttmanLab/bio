package edu.caltech.lncrna.bio.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.annotation.BlockedAnnotation.BlockedBuilder;

public abstract class Annotation implements Annotated {
    
    protected final String ref;
    protected final Strand strand;
    protected final int start;
    protected final int end;
    
    public Annotation(AnnotationBuilder b) {
        this.ref = b.ref;
        this.strand = b.strand;
        this.start = b.start;
        this.end = b.end;
    }
    
    public Annotation(Annotated a) {
        Objects.requireNonNull(a, "Null annotation passed to constructor");
        this.ref = a.getReferenceName();
        this.strand = a.getStrand();
        this.start = a.getStart();
        this.end = a.getEnd();
    }
    
    public Annotation(Annotated a, Strand s) {
        Objects.requireNonNull(a, "Null annotation passed to constructor");
        Objects.requireNonNull(s, "Null strand passed to constructor");
        this.ref = a.getReferenceName();
        this.start = a.getStart();
        this.end = a.getEnd();
        this.strand = s;
    }
    
    public Annotation(String ref, int start, int end, Strand strand) {
        this.ref = Objects.requireNonNull(ref, "Null reference passed to constructor");
        this.strand = Objects.requireNonNull(strand, "Null strand passed to constructor");
        
        if (strand.equals(Strand.INVALID)) {
            throw new IllegalArgumentException("Invalid strand passed to constructor");
        }

        if (start >= end) {
            throw new IllegalArgumentException("Invalid coordinates passed to constructor. "
                    + "Start must be greater than end. start: " + start + ", end: " + end);
        }
        
        if (start < 0) {
            throw new IllegalArgumentException("Invalid coordinates passed to constructor. "
                    + "Start must be greater than or equal to 0. start: " + start);
        }

        this.start = start;
        this.end = end;
    }
    
    @Override
    public String getReferenceName() {
        return ref;
    }
    
    @Override
    public Strand getStrand() {
        return strand;
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
    public int getSize() {
        if (getNumberOfBlocks() == 1) {
            return getSpan();
        }
        
        return getBlockStream().mapToInt(x -> x.getSpan()).sum();
    }
    
    @Override
    public int getSpan() {
        return end - start;
    }
    
    @Override
    public int getFivePrimePosition() {
        switch (getStrand()) {
        case POSITIVE:
            return getStart();
        case NEGATIVE:
            return getEnd();
        default:
            throw new IllegalArgumentException("5'-position is not defined " +
                    "for strand " + getStrand().toString());
        }
    }
    
    @Override
    public int getThreePrimePosition() {
        switch (getStrand()) {
        case POSITIVE:
            return getEnd();
        case NEGATIVE:
            return getStart();
        default:
            throw new IllegalArgumentException("3'-position is not defined " +
                    "for strand " + getStrand().toString());
        }
    }
    
    @Override
    public Iterator<Annotated> iterator() {
        return getBlockIterator();
    }
    
    @Override
    public Optional<Annotated> getIntrons() {
        
        if (getNumberOfBlocks() == 1) {
            return Optional.empty();
        } else {
            return getBody().minus(this);
        }
    }
    
    @Override
    public Iterator<Annotated> getIntronIterator() {
        Optional<Annotated> introns = getIntrons();
        
        if (introns.isPresent()) {
            return introns.get().getBlockIterator();
        } else {
            return Collections.emptyIterator();
        }
    }
    
    @Override
    public Stream<Annotated> getIntronStream() {
        Optional<Annotated> introns = getIntrons();
        
        if (introns.isPresent()) {
            return introns.get().getBlockStream();
        } else {
            return Stream.empty();
        }
    }
    
    @Override
    public boolean overlaps(Annotated other) {
        return intersect(other).isPresent();
    }
    
    @Override
    public boolean isAdjacentTo(Annotated other) {
        return ref.equals(other.getReferenceName()) &&
               (start == other.getEnd() || end == other.getStart());
    }
    
    @Override
    public Optional<Annotated> intersect(Annotated other) {

        if (!hasOverlappingHull(other)) {
            return Optional.empty();
        }
        
        if (getNumberOfBlocks() == 1 && other.getNumberOfBlocks() == 1) {
            return Optional.of(new Block(ref, Math.max(start, other.getStart()),
                    Math.min(end, other.getEnd()), strand.intersect(other.getStrand())));
        }
        
        return mergeAnnotations(other, (a, b) -> a && b);
    }
    
    public Optional<Annotated> minus(Annotated other) {
        
        if (!hasOverlappingHull(other)) {
            return Optional.of(this);
        }
        
        return mergeAnnotations(other, (a, b) -> a && !b);
    }
    
    @Override
    public boolean contains(Annotated other) {
        if (!strand.contains(other.getStrand())) {
            return false; 
        }
        
        if (getNumberOfBlocks() == 1) {
            return ref.equals(other.getReferenceName()) &&
                   start <= other.getStart() &&
                   end >= other.getEnd();
        }
        
        return ref.equals(other.getReferenceName()) &&
               !other.minus(this).isPresent();
    }
    
    private boolean hasOverlappingHull(Annotated other) {
        if (other == null) {
            return false;
        }
        
        if (!ref.equals(other.getReferenceName())) {
            return false;
        }
        
        Strand intersectionStrand = strand.intersect(other.getStrand());
        if (intersectionStrand.equals(Strand.INVALID)) {
            return false;
        }
        
        return start < other.getEnd() && other.getStart() < end;
    }
    
    private Optional<Annotated> mergeAnnotations(Annotated other,
            BiFunction<Boolean, Boolean, Boolean> op) {
        List<Annotated> blocks = new ArrayList<>();

        int[] flattened = merge(other, op);
        for (int i = 0; i < flattened.length; i += 2) {
            blocks.add(new Block(getReferenceName(), flattened[i],
                    flattened[i + 1], strand.intersect(other.getStrand())));
        }
    
        switch (blocks.size()) {
        case 0:
            return Optional.empty();
        case 1:
            return Optional.of(blocks.get(0));
        default:
            return Optional.of((new BlockedBuilder()).addBlocks(blocks).build());
        }
    }
    
    protected int[] merge(Annotated other, BiFunction<Boolean, Boolean, Boolean> op) {
        
        // Flatten the annotations and add a sentinel value at the end
        int[] thisEndpoints = new int[getNumberOfBlocks() * 2 + 1];
        int idx = 0;
        Iterator<Annotated> blocks = getBlockIterator();
        while (blocks.hasNext()) {
            Annotated block = blocks.next();
            thisEndpoints[idx++] = block.getStart();
            thisEndpoints[idx++] = block.getEnd();
        }
        
        int[] otherEndpoints = new int[other.getNumberOfBlocks() * 2 + 1];
        idx = 0;
        blocks = other.getBlockIterator();
        while (blocks.hasNext()) {
            Annotated block = blocks.next();
            otherEndpoints[idx++] = block.getStart();
            otherEndpoints[idx++] = block.getEnd();
        }

        int sentinel = Math.max(thisEndpoints[thisEndpoints.length - 2],
                                otherEndpoints[otherEndpoints.length - 2]) + 1;
        thisEndpoints[thisEndpoints.length - 1] = sentinel;
        otherEndpoints[otherEndpoints.length - 1] = sentinel;
        
        // Go through the flattened annotations and at each endpoint, determine whether
        // it is in the result
        int thisIdx = 0;
        int otherIdx = 0;
        List<Integer> rtrnEndpoints = new ArrayList<Integer>();
        int scan = Math.min(thisEndpoints[thisIdx], otherEndpoints[otherIdx]);
        while (scan < sentinel) {
            boolean in_this = !((scan < thisEndpoints[thisIdx]) ^ (thisIdx % 2 == 1));
            boolean in_other = !((scan < otherEndpoints[otherIdx]) ^ (otherIdx % 2 == 1));
            boolean in_result = op.apply(in_this, in_other);
            
            if (in_result ^ (rtrnEndpoints.size() % 2 == 1)) {
                rtrnEndpoints.add(scan);
            }
            if (scan == thisEndpoints[thisIdx]) {
                thisIdx++;
            }
            if (scan == otherEndpoints[otherIdx]) {
                otherIdx++;
            }
            scan = Math.min(thisEndpoints[thisIdx], otherEndpoints[otherIdx]);
        }

        return rtrnEndpoints.stream().mapToInt(i -> i).toArray();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ref + ":");
        for (Annotated block : this) {
            sb.append("[" + block.getStart() + "-" + block.getEnd() + "]");
        }
        sb.append("(" + strand.toString() + ")");
        return sb.toString();
    }

    public abstract static class AnnotationBuilder {
        
        protected int start;
        protected int end;
        protected String ref;
        protected Strand strand;
        
        public AnnotationBuilder() { }
        
        /**
         * Builds and returns the <code>Annotation</code> represented by this
         * builder.
         */
        public abstract Annotated build();
    }
}
