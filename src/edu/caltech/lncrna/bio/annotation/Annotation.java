package edu.caltech.lncrna.bio.annotation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.TextCigarCodec;

public class Annotation implements Annotated {
    
    protected final String ref;
    protected final Strand strand;
    protected final int[] blockBoundaries;
    
    public Annotation(AnnotationBuilder b) {
        this.ref = b.ref;
        this.strand = b.strand;
        this.blockBoundaries = b.blockBoundaries;
    }
    
    public Annotation(Annotated a) {
        Objects.requireNonNull(a, "Null annotation passed to constructor");
        this.ref = a.getReferenceName();
        this.strand = a.getStrand();
        this.blockBoundaries = a.getBlockBoundaries();
    }
    
    public Annotation(Annotated a, Strand s) {
        Objects.requireNonNull(a, "Null annotation passed to constructor");
        Objects.requireNonNull(s, "Null strand passed to constructor");
        this.ref = a.getReferenceName();
        this.blockBoundaries = a.getBlockBoundaries();
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

        this.blockBoundaries = new int[] {start, end};
    }
    
    protected Annotation(String ref, int[] blockBoundaries, Strand strand) {
        this.ref = ref;
        this.blockBoundaries = blockBoundaries;
        this.strand = strand;
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
        return blockBoundaries[0];
    }
    
    @Override
    public int getEnd() {
        return blockBoundaries[blockBoundaries.length - 1];
    }
    
    @Override
    public int[] getBlockBoundaries() {
        return blockBoundaries;
    }
    
    @Override
    public int getNumberOfBlocks() {
        return blockBoundaries.length / 2;
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
        return getEnd() - getStart();
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
    public boolean isUpstreamOf(Annotated other) {
        if (!getReferenceName().equals(other.getReferenceName())) {
            return false;
        }
        
        switch (other.getStrand()) {
        case POSITIVE:
            return getThreePrimePosition() <= other.getFivePrimePosition() &&
                   getFivePrimePosition() <= other.getFivePrimePosition();
        case NEGATIVE:
            return getThreePrimePosition() >= other.getFivePrimePosition() &&
                   getFivePrimePosition() >= other.getFivePrimePosition();
        default:
            throw new IllegalArgumentException("\"Upstream\" is not defined " +
                    "for strand " + other.getStrand().toString());
        }
    }
    
    @Override
    public boolean isDownstreamOf(Annotated other) {
        if (!getReferenceName().equals(other.getReferenceName())) {
            return false;
        }
        
        switch (other.getStrand()) {
        case POSITIVE:
            return getFivePrimePosition() >= other.getThreePrimePosition() &&
                   getThreePrimePosition() >= other.getThreePrimePosition();
        case NEGATIVE:
            return getThreePrimePosition() <= other.getFivePrimePosition() &&
                   getFivePrimePosition() <= other.getFivePrimePosition();
        default:
            throw new IllegalArgumentException("\"Downstream\" is not defined " +
                    "for strand " + other.getStrand().toString());
        }
    }
    
    @Override
    public Annotated getBody() {
        return new Annotation(ref, getStart(), getEnd() , strand);
    }
    
    @Override
    public Iterator<Annotated> iterator() {
        return getBlockIterator();
    }
    
    @Override
    public Iterator<Annotated> getBlockIterator() {
        return new BlockIterator(this);
    }

    @Override
    public Stream<Annotated> getBlockStream() {
        Iterable<Annotated> iterable = () -> getBlockIterator();
        return StreamSupport.stream(iterable.spliterator(), false);
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
               (getStart() == other.getEnd() || getEnd() == other.getStart());
    }
    
    @Override
    public Optional<Annotated> intersect(Annotated other) {

        if (!hasOverlappingBodies(other)) {
            return Optional.empty();
        }
        
        Strand newStrand = strand.intersect(other.getStrand());
        
        if (getNumberOfBlocks() == 1 && other.getNumberOfBlocks() == 1) {
            return Optional.of(new Annotation(ref,
                    Math.max(getStart(), other.getStart()),
                    Math.min(getEnd(), other.getEnd()),
                    newStrand));
        }
        
        return mergeAnnotations(other, newStrand, (a, b) -> a && b);
    }
    
    public Optional<Annotated> minus(Annotated other) {
        
        if (!hasOverlappingBodies(other)) {
            return Optional.of(this);
        }
        
        return mergeAnnotations(other, strand, (a, b) -> a && !b);
    }
    
    @Override
    public boolean contains(Annotated other) {
        if (!strand.contains(other.getStrand())) {
            return false; 
        }
        
        if (getNumberOfBlocks() == 1) {
            return ref.equals(other.getReferenceName()) &&
                   getStart() <= other.getStart() &&
                   getEnd() >= other.getEnd();
        }
        
        return ref.equals(other.getReferenceName()) &&
               !other.minus(this).isPresent();
    }
    
    private boolean hasOverlappingBodies(Annotated other) {
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
        
        return getStart() < other.getEnd() && other.getStart() < getEnd();
    }
    
    private Optional<Annotated> mergeAnnotations(Annotated other,
            Strand strand, BiFunction<Boolean, Boolean, Boolean> op) {

        int[] newBlockBoundaries = merge(other, op);
    
        if (newBlockBoundaries.length == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new Annotation(other.getReferenceName(),
                    newBlockBoundaries, strand));
        }
    }
    
    protected int[] merge(Annotated other, BiFunction<Boolean, Boolean, Boolean> op) {
        
        // Add a sentinel value at the end of the block boundaries
        int[] thisBoundaries = new int[getNumberOfBlocks() * 2 + 1];
        for (int i = 0; i < blockBoundaries.length; i++) {
            thisBoundaries[i] = blockBoundaries[i];
        }
        int[] otherBoundaries = new int[other.getNumberOfBlocks() * 2 + 1];
        for (int i = 0; i < other.getBlockBoundaries().length; i++) {
            otherBoundaries[i] = other.getBlockBoundaries()[i];
        }

        int sentinel = Math.max(thisBoundaries[thisBoundaries.length - 2],
                                otherBoundaries[otherBoundaries.length - 2]) + 1;
        thisBoundaries[thisBoundaries.length - 1] = sentinel;
        otherBoundaries[otherBoundaries.length - 1] = sentinel;
        
        // Go through the boundaries and at each point, determine whether
        // it is in the result
        int thisIdx = 0;
        int otherIdx = 0;
        List<Integer> rtrnEndpoints = new ArrayList<Integer>();
        int scan = Math.min(thisBoundaries[thisIdx], otherBoundaries[otherIdx]);
        while (scan < sentinel) {
            boolean in_this = !((scan < thisBoundaries[thisIdx]) ^ (thisIdx % 2 == 1));
            boolean in_other = !((scan < otherBoundaries[otherIdx]) ^ (otherIdx % 2 == 1));
            boolean in_result = op.apply(in_this, in_other);
            
            if (in_result ^ (rtrnEndpoints.size() % 2 == 1)) {
                rtrnEndpoints.add(scan);
            }
            if (scan == thisBoundaries[thisIdx]) {
                thisIdx++;
            }
            if (scan == otherBoundaries[otherIdx]) {
                otherIdx++;
            }
            scan = Math.min(thisBoundaries[thisIdx], otherBoundaries[otherIdx]);
        }

        return rtrnEndpoints.stream().mapToInt(i -> i).toArray();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof Annotation)) {
            return false;
        }
        
        Annotation other = (Annotation) o;
        
        return ref.equals(other.ref) &&
               strand.equals(other.strand) &&
               Arrays.equals(blockBoundaries, other.getBlockBoundaries());
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + ref.hashCode();
        hashCode = 37 * hashCode + strand.hashCode();
        hashCode = 37 * hashCode + Arrays.hashCode(blockBoundaries);
        return hashCode;
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

    public static class AnnotationBuilder {
        
        protected int[] blockBoundaries;
        protected List<Annotated> blocks;
        protected String ref;
        protected Strand strand;
        
        public AnnotationBuilder() {
            blocks = new ArrayList<>();
        }
        
        /**
         * Adds all the <code>Block</code>s in the given
         * <code>Collection</code> to this builder.
         * @param bs - the <code>Collection</code> of <code>Block</code>s to
         * add
         * @return this builder for method-chaining
         */
        public AnnotationBuilder addAnnotations(Collection<Annotated> annots) {
            for (Annotated annot : annots) {
                addAnnotation(annot);
            }
            return this;
        }
        
        /**
         * Adds a <code>Block</code> to this builder.
         * @param b - the <code>Block</code> to add
         * @return this builder for method-chaining
         */
        public AnnotationBuilder addAnnotation(Annotated annot) {
            for (Annotated block : annot) {
                blocks.add(block);
            }
            return this;
        }
        
        public AnnotationBuilder addAnnotationFromCigar(Cigar cigar,
                String ref, int start, Strand strand) {
            
            List<CigarElement> elements = cigar.getCigarElements();
            
            int currentOffset = start;
            
            for (CigarElement element : elements) {
                CigarOperator op = element.getOperator();
                int length = element.getLength();
                
                switch (op) {
                case D:
                case EQ:
                case M:
                case X:
                    int blockEnd = currentOffset + length;
                    addAnnotation(new Annotation(ref, currentOffset,
                                                 blockEnd, strand));
                    currentOffset = blockEnd;
                    break;
                case N:
                    currentOffset += length;
                    break;
                default:
                    // Skip H, I, P and S.
                    break;
                }
            }
            
            return this;
        }
        
        public AnnotationBuilder addAnnotationFromCigar(String string, String ref,
                int start, Strand strand) {
            Cigar cigar = TextCigarCodec.decode(string);
            return addAnnotationFromCigar(cigar, ref, start, strand);
        }
        
        /**
         * Builds and returns the <code>Annotation</code> represented by this
         * builder.
         */
        public Annotation build() {
            
            if (blocks.isEmpty()) {
                throw new IllegalArgumentException("Attempted to build an " +
                        "Annotation with no blocks.");
            }

            if (blocks.size() > 1) {
                checkBlockConsistency();
                mergeBlockListAndUpdateMemberVariables();
            } else {
                updateMemberVariablesWithSingleBlock();
            }

            return new Annotation(this);
        }
        
        /**
         * Merges overlapping <code>Block</code>s in this builders block-list,
         * and sets the member variables of this builder to correspond with the
         * <code>Blocks</code> in its block-list.
         * <p>
         * The affected member variables are the start, the end, the reference
         * name, and the strand. For example, after calling this method, the start
         * coordinate of this builder will equal the start coordinate of its first
         * <code>Block</code>.
         * @throws AssertionError if this builder does not have more than one block
         */
        protected void mergeBlockListAndUpdateMemberVariables() {
            assert blocks.size() > 1: "Annotation must have more than one " +
                    "block, but has " + blocks.size() + " blocks.";
            Collections.sort(blocks, Comparator.comparing(Annotated::getStart)
                    .thenComparing(Annotated::getEnd));
            
            Deque<Annotated> mergedBlocks = new ArrayDeque<>();
            mergedBlocks.push(blocks.get(0));

            for (int i = 1; i < blocks.size(); i++) {
                Annotated currentBlock = blocks.get(i);
                if (mergedBlocks.peek().overlaps(currentBlock) || mergedBlocks.peek().isAdjacentTo(currentBlock)) {
                    currentBlock = merge(mergedBlocks.pop(), currentBlock);
                }
                mergedBlocks.push(currentBlock);
            }
            
            blocks.clear();

            while (mergedBlocks.size() != 0) {
                blocks.add(mergedBlocks.removeLast());
            }
            
            ref = blocks.get(0).getReferenceName();
            strand = blocks.get(0).getStrand();
            blockBoundaries = new int[blocks.size() * 2];
            int pos = 0;
            
            for (Annotated block : blocks) {
                blockBoundaries[pos] = block.getStart();
                blockBoundaries[pos + 1] = block.getEnd();
                pos += 2;
            }
        }
        
        /**
         * Sets the member variables of this builder to correspond with the
         * single <code>Block</code> in its block-list.
         * <p>
         * The affected member variables are the start, the end, the reference
         * name, and the strand. For example, after calling this method, the
         * start coordinate of this builder will equal the start coordinate of
         * its <code>Block</code>.
         * @throws AssertionError if this builder does not have exactly one
         * block
         */
        protected void updateMemberVariablesWithSingleBlock() {
            assert blocks.size() == 1: "Annotation must have one block, but " +
                    "has " + blocks.size() + " blocks.";
            Annotated b = blocks.get(0);
            blockBoundaries = b.getBlockBoundaries();
            ref = b.getReferenceName();
            strand = b.getStrand();
        }
        
        /**
         * Checks if the <code>Block</code>s in this builder are consistent.
         * <p>
         * The <code>Block</code>s are consistent if they have the same
         * reference name and the same orientation.
         * @throws IllegalArgumentException if all of the <code>Block</code>s
         * do not have the same reference name
         * @throws IllegalArgumentException if all of the <code>Block</code>s
         * do not have the same orientation
         */
        protected void checkBlockConsistency() {
            if (blocks.size() <= 1) {
                return;
            }
            String correctRef = null;
            Strand correctStrand = null;
            
            for (Annotated block : blocks) {
                
                if (correctRef == null) {
                    correctRef = block.getReferenceName();
                } else if (!block.getReferenceName().equals(correctRef)) {
                    throw new IllegalArgumentException("Blocks in this annotation do " +
                            "not have the same reference: " + block.getReferenceName() +
                            " vs " + correctRef);
                }
                
                if (correctStrand == null) {
                    correctStrand = block.getStrand();
                } else if (!block.getStrand().equals(correctStrand)) {
                    throw new IllegalArgumentException("Blocks in this annotation do " +
                            "not have the same orientation: " + block.getStrand().toString() +
                            " vs " + correctStrand.toString());
                }
            }
        }
        
        /**
         * Helper method to merge two <code>Block</code>s together.
         * @param annotated - the first block
         * @param currentBlock - the second block
         * @throws AssertionError if <code>Block</code>s do not overlap and
         * are not adjacent
         * @throws AssertionError if <code>Block</code>s are not on the same
         * reference
         * @throws AssertionError if <code>Block</code>s are not on the same
         * strand
         */
        protected Annotated merge(Annotated annotated, Annotated currentBlock) {
            assert (annotated.getReferenceName().equals(currentBlock.getReferenceName())) :
                "Blocks are not on the same reference.";
            assert (annotated.overlaps(currentBlock) || annotated.isAdjacentTo(currentBlock)) :
                "Blocks do not overlap and are not adjacent.";
            assert (annotated.getStrand().equals(currentBlock.getStrand())) :
                "Blocks are not on the same strand.";
            return new Annotation(annotated.getReferenceName(),
                             Math.min(annotated.getStart(), currentBlock.getStart()),
                             Math.max(annotated.getEnd(), currentBlock.getEnd()),
                             annotated.getStrand());
        }
    }
    
    protected final static class BlockIterator implements Iterator<Annotated> {

        private final Annotated annot;
        private int blockNum = 0;
        
        public BlockIterator(Annotated annot) {
            this.annot = annot;
        }
        
        @Override
        public boolean hasNext() {
            return blockNum < annot.getNumberOfBlocks();
        }

        @Override
        public Annotated next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int start = annot.getBlockBoundaries()[blockNum * 2];
            int end = annot.getBlockBoundaries()[blockNum * 2 + 1];
            blockNum++;
            return new Annotation(annot.getReferenceName(), start, end,
                                  annot.getStrand());
        }
        
    }
}
