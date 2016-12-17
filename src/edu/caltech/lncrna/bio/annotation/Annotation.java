package edu.caltech.lncrna.bio.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

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
        return end - start;
    }
    
    @Override
    public int getSpan() {
        return getSize();
    }
    
    @Override
    public int getPositionRelativeToFivePrime(int absolutePosition) {
        switch (strand) {
        case POSITIVE:
            // fallthrough: treat BOTH and POSITIVE the same
        case BOTH:
            return absolutePosition - start;
        case NEGATIVE:
            return end - absolutePosition - 1; // -1 because of half-open interval
        default:
            throw new IllegalArgumentException("5' not defined for an annotation " +
                    "with orientation: " + strand.toString());
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
        List<Block> blocks = new ArrayList<>();

        int[] flattened = merge(other, op);
        for (int i = 0; i < flattened.length; i += 2) {
            blocks.add(new Block(getReferenceName(), flattened[i],
                    flattened[i + 1], strand.intersect(other.getStrand())));
        }
    
        if (blocks.isEmpty()) {
            return Optional.empty();
        }
    
        return blocks.isEmpty()
                ? Optional.empty()
                : Optional.of((new BlockedAnnotation.BlockedBuilder()).addBlocks(blocks).build());
    }
    
    private int[] merge(Annotated other, BiFunction<Boolean, Boolean, Boolean> op) {
        
        // Flatten the annotations and add a sentinel value at the end
        int[] thisEndpoints = new int[getNumberOfBlocks() * 2 + 1];
        int idx = 0;
        Iterator<Block> blocks = getBlockIterator();
        while (blocks.hasNext()) {
            Block block = blocks.next();
            thisEndpoints[idx++] = block.getStart();
            thisEndpoints[idx++] = block.getEnd();
        }
        
        int[] otherEndpoints = new int[other.getNumberOfBlocks() * 2 + 1];
        idx = 0;
        blocks = other.getBlockIterator();
        while (blocks.hasNext()) {
            Block block = blocks.next();
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