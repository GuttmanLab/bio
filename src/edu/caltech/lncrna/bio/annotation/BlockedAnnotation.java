package edu.caltech.lncrna.bio.annotation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    protected final List<Block> blocks;
    
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
        List<Block> tmp = new ArrayList<>();
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
    
    BlockedAnnotation(String ref, int start, int end, Strand strand, List<Block> blocks) {
        super(ref, start, end, strand);
        this.blocks = Collections.unmodifiableList(blocks);
    }
    
    @Override
    public int getSize() {
        return getBlockStream().mapToInt(b -> b.getSize()).sum();
    }

    @Override
    public int getNumberOfBlocks() {
        return blocks.size();
    }

    @Override
    public Iterator<Block> getBlockIterator() {
        return blocks.iterator();
    }
    
    @Override
    public Stream<Block> getBlockStream() {
        final Spliterator<Block> s =
                Spliterators.spliteratorUnknownSize(getBlockIterator(),
                                                    Spliterator.ORDERED);
        return StreamSupport.stream(s, false);
    }
    
    /**
     * Gets the introns contained in this as an <code>Annotated</code> wrapped
     * in an <code>Optional</code>.
     * <p>
     * If this annotation has no introns, this method returns an empty
     * <code>Optional</code>.
     */
    public Optional<Annotated> getIntrons() {
        if (blocks.size() == 1) {
            return Optional.empty();
        }
        return getHull().minus(this);
    }
    
    /**
     * Gets an <code>Iterator</code> over the introns of this
     * <code>Annotation</code>.
     * <p>
     * Returns an empty <code>Iterator</code> if there are no introns.
     */
    public Iterator<Block> getIntronBlockIterator() {
        if (blocks.size() == 1) {
            return Collections.emptyIterator();
        }
        Annotated introns = getHull().minus(this).get();
        return introns.getBlockIterator();
    }

    /**
     * Gets the introns of this <code>Annotation</code> as a 
     * <code>Stream</code>.
     */
    public Stream<Block> getIntronBlockStream() {
        final Spliterator<Block> introns =
                Spliterators.spliteratorUnknownSize(getIntronBlockIterator(),
                                                    Spliterator.ORDERED);
        return StreamSupport.stream(introns, false);
    }
    
    @Override
    public Annotated getHull() {
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
        for (Block b : blocks) {
            hashCode = 37 * hashCode + b.hashCode();
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

        protected List<Block> blocks = new ArrayList<>();

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
        public BlockedBuilder addBlocks(Collection<Block> bs) {
            bs.iterator().forEachRemaining(blocks::add);
            return this;
        }
        
        /**
         * Adds a <code>Block</code> to this builder.
         * @param b - the <code>Block</code> to add
         * @return this builder for method-chaining
         */
        public BlockedBuilder addBlock(Block b) {
            blocks.add(b);
            return this;
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
            Block b = blocks.get(0);
            start = b.start;
            end = b.end;
            ref = b.ref;
            strand = b.strand;
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
            Collections.sort(blocks, Comparator.comparing(Block::getStart)
                    .thenComparing(Block::getEnd));
            
            Deque<Block> mergedBlocks = new ArrayDeque<>();
            mergedBlocks.push(blocks.get(0));
            for (int i = 1; i < blocks.size(); i++) {
                Block currentBlock = blocks.get(i);
                if (mergedBlocks.peek().overlaps(currentBlock) || mergedBlocks.peek().isAdjacentTo(currentBlock)) {
                    currentBlock = merge(mergedBlocks.pop(), currentBlock);
                }
                mergedBlocks.push(currentBlock);
            }
            
            blocks.clear();
            
            Block upstreamBlock = mergedBlocks.peekLast();
            Block downstreamBlock = mergedBlocks.peekFirst();
            start = upstreamBlock.start;
            end = downstreamBlock.end;
            ref = upstreamBlock.ref;
            strand = upstreamBlock.strand;

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
            Iterator<Block> iter = blocks.iterator();
            while (iter.hasNext()) {
                Block b = iter.next();
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
         * @param a - the first block
         * @param b - the second block
         * @throws AssertionError if <code>Block</code>s do not overlap and
         * are not adjacent
         * @throws AssertionError if <code>Block</code>s are not on the same
         * reference
         * @throws AssertionError if <code>Block</code>s are not on the same
         * strand
         */
        protected Block merge(Block a, Block b) {
            assert (a.getReferenceName().equals(b.getReferenceName())) :
                "Blocks are not on the same reference.";
            assert (a.overlaps(b) || a.isAdjacentTo(b)) :
                "Blocks do not overlap and are not adjacent.";
            assert (a.getStrand().equals(b.getStrand())) :
                "Blocks are not on the same strand.";
            return new Block(a.getReferenceName(),
                             Math.min(a.getStart(), b.getStart()),
                             Math.max(a.getEnd(), b.getEnd()),
                             a.getStrand());
        }
    }
}