package edu.caltech.lncrna.bio.annotation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.TextCigarCodec;

/**
 * This class represents an <code>Annotation</code> which is made up of
 * <code>Block</code>s.
 * <p>
 * Most constructors for this class are not exposed. To construct a
 * <code>BlockedAnnotation</code>, use a {@link BlockedBuilder}:
 * <pre>
 * <code>
 * BlockedAnnotation b = (new BlockedBuilder())
 *     .addBlock(new Block("chr2", 1300, 1350, Strand.POSITIVE))
 *     .addBlock(new Block("chr2", 1400, 1450, Strand.POSITIVE))
 *     .build();
 * </code>
 * </pre>
 */
public class BlockedAnnotation extends Annotation {

    protected final List<Annotated> blocks;
    
    protected BlockedAnnotation(BlockedBuilder b) {
        super(b);
        this.blocks = Collections.unmodifiableList(b.blocks);
    }
    
    /**
     * Constructs a new instance of a <code>BlockedAnnotation</code> with the
     * same blocks as in <code>b</code>.
     * @param b - the copied annotation
     */
    public BlockedAnnotation(Annotated b) {
        super(b);
        List<Annotated> tmp = new ArrayList<>();
        b.getBlockIterator().forEachRemaining(tmp::add);
        blocks = Collections.unmodifiableList(tmp);
    }
    
    /**
     * Constructs a new instance of a <code>BlockedAnnotation</code> with the
     * same blocks as in <code>annot</code>, but with a different orientation.
     * @param annot - the copied annotation
     * @param strand - the new orientation
     */
    public BlockedAnnotation(Annotated annot, Strand strand) {
        super(annot, strand);
        List<Block> tmp = new ArrayList<>();
        annot.getBlockStream()
             .map(a -> new Block(a, strand))
             .forEach(tmp::add);
        blocks = Collections.unmodifiableList(tmp);
    }
    
    BlockedAnnotation(String ref, int start, int end, Strand strand, List<Annotated> blocks) {
        super(ref, start, end, strand);
        this.blocks = Collections.unmodifiableList(blocks);
    }

    @Override
    public int getNumberOfBlocks() {
        return blocks.size();
    }

    @Override
    public Iterator<Annotated> getBlockIterator() {
        return blocks.iterator();
    }
    
    @Override
    public Stream<Annotated> getBlockStream() {
        final Spliterator<Annotated> s =
                Spliterators.spliteratorUnknownSize(getBlockIterator(),
                                                    Spliterator.ORDERED);
        return StreamSupport.stream(s, false);
    }
    
    @Override
    public Annotated getBody() {
        return new Block(ref, start, end, strand);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof BlockedAnnotation)) {
            return false;
        }
        
        BlockedAnnotation other = (BlockedAnnotation) o;
        
        return ref.equals(other.ref) &&
               start == other.start &&
               end == other.end &&
               strand.equals(other.strand) &&
               blocks.equals(other.blocks);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + ref.hashCode();
        hashCode = 37 * hashCode + strand.hashCode();
        hashCode = 37 * hashCode + start;
        hashCode = 37 * hashCode + end;
        for (Annotated block : blocks) {
            hashCode = 37 * hashCode + block.hashCode();
        }
        return hashCode;
    }
    
    /**
     * A builder class for constructing {@link BlockedAnnotation}s.
     * <p>
     * An object of this class can be loaded with <code>Block</code>s, and will
     * construct the corresponding <code>BlockedAnnotation</code> when its
     * <code>build()</code> method is invoked. Any disagreement among the
     * <code>Block</code>s (for example, conflicting reference names) will
     * result in an exception being thrown when <code>build()</code> is
     * executed. Overlapping <code>Block</code>s will be merged.
     */
    public static class BlockedBuilder extends AnnotationBuilder {

        protected List<Annotated> blocks = new ArrayList<>();

        /**
         * Constructs a new builder containing no <code>Block</code>s.
         */
        public BlockedBuilder() {
            super();
        }

        /**
         * Adds all the <code>Block</code>s in the given
         * <code>Collection</code> to this builder.
         * @param bs - the <code>Collection</code> of <code>Block</code>s to
         * add
         * @return this builder for method-chaining
         */
        public BlockedBuilder addBlocks(Collection<Annotated> bs) {
            bs.iterator().forEachRemaining(blocks::add);
            return this;
        }
        
        /**
         * Adds a <code>Block</code> to this builder.
         * @param b - the <code>Block</code> to add
         * @return this builder for method-chaining
         */
        public BlockedBuilder addBlock(Annotated b) {
            blocks.add(b);
            return this;
        }
        
        public BlockedBuilder addBlocksFromCigar(Cigar cigar, String ref,
                int start, Strand strand) {
            
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
                    addBlock(new Block(ref, currentOffset, blockEnd, strand));
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
        
        public BlockedBuilder addBlocksFromCigar(String string, String ref,
                int start, Strand strand) {
            Cigar cigar = TextCigarCodec.decode(string);
            return addBlocksFromCigar(cigar, ref, start, strand);
        }
        
        /**
         * {@inheritDoc}
         * <p>
         * Information about the returned <code>Annotation</code> (reference
         * name, <code>Strand</code>, etc.) is derived from the
         * <code>Block</code>s within this builder. 
         * @throws IllegalArgumentException if this builder contains no
         * <code>Block</code>s
         * @throws IllegalArgumentException if all of this builder's
         * <code>Block</code>s do not have the same strandedness
         * @throws IllegalArgumentException if all of this builder's
         * <code>Block</code>s do not have the same reference name
         */
        @Override
        public BlockedAnnotation build() {
            
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

            return new BlockedAnnotation(this);
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
            start = b.getStart();
            end = b.getEnd();
            ref = b.getReferenceName();
            strand = b.getStrand();
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
            
            Annotated upstreamBlock = mergedBlocks.peekLast();
            Annotated downstreamBlock = mergedBlocks.peekFirst();
            start = upstreamBlock.getStart();
            end = downstreamBlock.getEnd();
            ref = upstreamBlock.getReferenceName();
            strand = upstreamBlock.getStrand();

            // blocks.addAll(mergedBlocks) adds the blocks in
            // downstream-to-upstream order (probably because of pop).
            // Prefer to have them in the other order.
            while (mergedBlocks.size() != 0) {
                blocks.add(mergedBlocks.removeLast());
            }
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
            Iterator<Annotated> iter = blocks.iterator();
            while (iter.hasNext()) {
                Annotated b = iter.next();
                if (correctRef == null) {
                    correctRef = b.getReferenceName();
                } else if (!b.getReferenceName().equals(correctRef)) {
                    throw new IllegalArgumentException("Blocks in this annotation do " +
                            "not have the same reference: " + b.getReferenceName() +
                            " vs " + correctRef);
                }
                
                if (correctStrand == null) {
                    correctStrand = b.getStrand();
                } else if (!b.getStrand().equals(correctStrand)) {
                    throw new IllegalArgumentException("Blocks in this annotation do " +
                            "not have the same orientation: " + b.getStrand().toString() +
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
        protected Block merge(Annotated annotated, Annotated currentBlock) {
            assert (annotated.getReferenceName().equals(currentBlock.getReferenceName())) :
                "Blocks are not on the same reference.";
            assert (annotated.overlaps(currentBlock) || annotated.isAdjacentTo(currentBlock)) :
                "Blocks do not overlap and are not adjacent.";
            assert (annotated.getStrand().equals(currentBlock.getStrand())) :
                "Blocks are not on the same strand.";
            return new Block(annotated.getReferenceName(),
                             Math.min(annotated.getStart(), currentBlock.getStart()),
                             Math.max(annotated.getEnd(), currentBlock.getEnd()),
                             annotated.getStrand());
        }
    }
}