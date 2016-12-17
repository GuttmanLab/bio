package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Block;
import edu.caltech.lncrna.bio.annotation.BlockedAnnotation.BlockedBuilder;
import edu.caltech.lncrna.bio.annotation.Strand;

public class TestBlock {

    private static final String REF1 = "chr1";
    private static final int START1 = 0;
    private static final int END1 = 20;
    private static final Strand STRAND1 = Strand.POSITIVE;
    private static final int LENGTH1 = 20;

    private static final Block BLOCK1 = new Block(REF1, START1, END1, STRAND1);
    private static final Block BLOCK1_COPY = new Block(REF1, START1, END1, STRAND1);
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testBlockConstructorFailsWithNullReference() {
        thrown.expect(NullPointerException.class);
        new Block(null, 10, 20, Strand.POSITIVE);
    }
    
    @Test
    public void testBlockConstructorFailsWithNullStrand() {
        thrown.expect(NullPointerException.class);
        new Block("chr1", 10, 20, null);
    }
    
    @Test
    public void testBlockConstructorFailsWithNegativeCoords() {
        thrown.expect(IllegalArgumentException.class);
        new Block("chr1", -1, 20, Strand.POSITIVE);
    }
    
    @Test
    public void testBlockConstructorFailsWithZeroLength() {
        thrown.expect(IllegalArgumentException.class);
        new Block("chr1", 10, 10, Strand.POSITIVE);
    }
    
    @Test
    public void testBlockConstructorFailsWithInverseCoords() {
        thrown.expect(IllegalArgumentException.class);
        new Block("chr1", 20, 10, Strand.POSITIVE);
    }
    
    @Test
    public void testBlockConstructorFailsWithInvalidStrand() {
        thrown.expect(IllegalArgumentException.class);
        new Block("chr1", 10, 20, Strand.INVALID);
    }
    
    @Test
    public void testNewStrandConstructorFailsWithNullAnnotation() {
        thrown.expect(NullPointerException.class);
        new Block(null, Strand.BOTH);
    }
    
    @Test
    public void testNewStrandConstructorFailsWithNullStrand() {
        thrown.expect(NullPointerException.class);
        new Block(new Block("chr1", 10, 20, Strand.POSITIVE), null);
    }
    
    @Test
    public void testNewStrandConstructor() {
        Block b = new Block(new Block("chr1", 10, 20, Strand.POSITIVE), Strand.NEGATIVE);
        assertThat(b.getReferenceName(), is("chr1"));
        assertThat(b.getStart(), is(10));
        assertThat(b.getEnd(), is(20));
        assertThat(b.getStrand(), is(Strand.NEGATIVE));
    }
    
    @Test
    public void testBlockGetReferenceName() {
        assertThat(BLOCK1.getReferenceName(), is(REF1));
    }
    
    @Test
    public void testBlockGetStart() {
        assertThat(BLOCK1.getStart(), is(START1));
    }
    
    @Test
    public void testBlockGetEnd() {
        assertThat(BLOCK1.getEnd(), is(END1));
    }
    
    @Test
    public void testBlockGetStrand() {
        assertThat(BLOCK1.getStrand(), is(STRAND1));
    }
    
    @Test
    public void testBlockGetSize() {
        assertThat(BLOCK1.getSize(), is(LENGTH1));
    }
    
    @Test
    public void testBlockGetSpan() {
        assertThat(BLOCK1.getSpan(), is(LENGTH1));
    }
    
    @Test
    public void testBlockEqualObjectCopy() {
        assertThat(BLOCK1 == BLOCK1_COPY, is(false));
    }
    
    @Test
    public void testBlockEqualIdentity() {
        assertThat(BLOCK1, is(BLOCK1));
    }
    
    @Test
    public void testBlockEqualCopy() {
        assertThat(BLOCK1, is(BLOCK1_COPY));
    }
    
    @Test
    public void testDoesNotOverlapNull() {
        Block block = new Block("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.overlaps(null), is(false));
    }
    
    @Test
    public void testOverlapsItself() {
        Block block = new Block("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.overlaps(block), is(true));
    }
    
    @Test
    public void testOverlapsAnother() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr2", 150, 250, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(true));
    }
    
    @Test
    public void testDoesNotOverlapComplement() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr2", 100, 200, Strand.NEGATIVE);
        assertThat(b1.overlaps(b2), is(false));
    }
    
    @Test
    public void testOverlapsBlockWithBothStrands() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr2", 100, 200, Strand.BOTH);
        assertThat(b1.overlaps(b2), is(true));
    }
    
    @Test
    public void testDoesNotOverlapDifferentReference() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr3", 100, 200, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(false));
    }
    
    @Test
    public void testDoesNotOverlapAdjacentBlocks() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr2", 0, 100, Strand.POSITIVE);
        Block b3 = new Block("chr2", 200, 300, Strand.POSITIVE);
        assertThat(b1.overlaps(b2), is(false));
        assertThat(b1.overlaps(b3), is(false));
    }
    
    @Test
    public void testDoesNotIntersectNull() {
        Block block = new Block("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.intersect(null).isPresent(), is(false));
    }
    
    @Test
    public void testIntersectsItself() {
        Block block = new Block("chr2", 100, 200, Strand.POSITIVE);
        assertThat(block.intersect(block).get(), is(block));
    }
    
    @Test
    public void testIntersectsAnother() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr2", 150, 250, Strand.POSITIVE);
        Block intersection = new Block("chr2", 150, 200, Strand.POSITIVE);
        assertThat(b1.intersect(b2).get(), is(intersection));
    }
    
    @Test
    public void testDoesNotIntersectComplement() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr2", 100, 200, Strand.NEGATIVE);
        assertThat(b1.intersect(b2).isPresent(), is(false));
    }
    
    @Test
    public void testIntersectsBlockWithBothStrands() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr2", 100, 200, Strand.BOTH);
        assertThat(b1.intersect(b2).get(), is(b1));
    }
    
    @Test
    public void testDoesNotIntersectDifferentReference() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr3", 100, 200, Strand.POSITIVE);
        assertThat(b1.intersect(b2).isPresent(), is(false));
    }
    
    @Test
    public void testDoesNotIntersectAdjacentBlocks() {
        Block b1 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Block b2 = new Block("chr2", 0, 100, Strand.POSITIVE);
        Block b3 = new Block("chr2", 200, 300, Strand.POSITIVE);
        assertThat(b1.intersect(b2).isPresent(), is(false));
        assertThat(b1.intersect(b3).isPresent(), is(false));
    }
    
    @Test
    public void testGetBlockIterator() {
        Block b = new Block("chr1", 100, 200, Strand.POSITIVE);
        Iterator<Block> iter = b.getBlockIterator();
        assertThat(iter.hasNext(), is(true));
        Block nextBlock = iter.next();
        assertThat(nextBlock, is(b));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testGetBlockStream() {
        Block b = new Block("chr1", 100, 200, Strand.POSITIVE);
        Stream<Block> stream = b.getBlockStream();
        assertThat(stream.count(), is(1L));
        stream = b.getBlockStream();
        assertThat(stream.findFirst().get(), is(b));
    }

    @Test
    public void testBlockContainsSelf() {
        Block b= new Block("chr1", 100, 200, Strand.POSITIVE);
       assertThat(b.contains(b), is(true));
    }
    
    @Test
    public void testBlockContainsBlock() {
        Block big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block small = new Block("chr1", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockDoesNotContainDifferentChromosomeBlock() {
        Block big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block small = new Block("chr2", 125, 175, Strand.POSITIVE);
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockDoesNotContainOverlappingEdgeBlock() {
        Block big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block small = new Block("chr1", 50, 150, Strand.POSITIVE);
        assertThat(big.overlaps(small), is(true));
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockDoesNotContainNonOverlappingBlock() {
        Block big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block small = new Block("chr1", 0, 50, Strand.POSITIVE);
        assertThat(big.overlaps(small), is(false));
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testBlockContainsBlockedAnnotation() {
        Block big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 100, 120, Strand.POSITIVE))
                .addBlock(new Block("chr1", 150, 175, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(true));
    }
    
    @Test
    public void testBlockDoesNotContainOverlappingBlockedAnnotation() {
        Block big = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated small = (new BlockedBuilder())
                .addBlock(new Block("chr1", 95, 120, Strand.POSITIVE))
                .addBlock(new Block("chr1", 150, 175, Strand.POSITIVE))
                .build();
        assertThat(big.contains(small), is(false));
    }
    
    @Test
    public void testMinusWithNoOverlapSameStrandSameChromosome() {
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr1", 50, 100, Strand.POSITIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(1));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(200));
        assertThat(diffBlock.getStrand(), is(Strand.POSITIVE));
        assertThat(diffBlock.getReferenceName(), is ("chr1"));
    }
    
    @Test
    public void testMinusWithNoOverlapSameStrandDifferentChromosome() {
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr2", 100, 200, Strand.POSITIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(1));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(200));
        assertThat(diffBlock.getStrand(), is(Strand.POSITIVE));
        assertThat(diffBlock.getReferenceName(), is ("chr1"));
    }
    
    @Test
    public void testMinusWithNoOverlapDifferentStrandSameChromosome() {
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr2", 100, 200, Strand.NEGATIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(1));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(200));
        assertThat(diffBlock.getStrand(), is(Strand.POSITIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
    }
    
    @Test
    public void testMinusCompleteRemoval() {
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Optional<Annotated> diffBlock = block1.minus(block2);
        assertThat(diffBlock.isPresent(), is(false));
    }
    
    @Test
    public void testMinusCompleteRemovalNegativeMinusBoth() {
        Block block1 = new Block("chr1", 100, 200, Strand.NEGATIVE);
        Block block2 = new Block("chr1", 100, 200, Strand.BOTH);
        Optional<Annotated> diffBlock = block1.minus(block2);
        assertThat(diffBlock.isPresent(), is(false));
    }
    
    @Test
    public void testMinusCompleteRemovalPositiveMinusBoth() {
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr1", 100, 200, Strand.BOTH);
        Optional<Annotated> diffBlock = block1.minus(block2);
        assertThat(diffBlock.isPresent(), is(false));
    }
    
    @Test
    public void testMinusCompleteRemovalBothMinusNegative() {
        Block block1 = new Block("chr1", 100, 200, Strand.BOTH);
        Block block2 = new Block("chr1", 100, 200, Strand.NEGATIVE);
        Optional<Annotated> diffBlock = block1.minus(block2);
        assertThat(diffBlock.isPresent(), is(false));
    }
    
    @Test
    public void testMinusRemovalFromEndSameStrand() {
        Block block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Block block2 = new Block("chr1", 50, 150, Strand.POSITIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(1));
        assertThat(diffBlock.getStart(), is(150));
        assertThat(diffBlock.getEnd(), is(200));
        assertThat(diffBlock.getStrand(), is(Strand.POSITIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
    }
    
    @Test
    public void testMinusRemovalFromEndNegativeMinusBoth() {
        Block block1 = new Block("chr1", 100, 200, Strand.NEGATIVE);
        Block block2 = new Block("chr1", 50, 150, Strand.BOTH);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(1));
        assertThat(diffBlock.getStart(), is(150));
        assertThat(diffBlock.getEnd(), is(200));
        assertThat(diffBlock.getStrand(), is(Strand.NEGATIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
    }
    
    @Test
    public void testMinusRemovalFromEndBothMinusNegative() {
        Block block1 = new Block("chr1", 100, 200, Strand.BOTH);
        Block block2 = new Block("chr1", 50, 150, Strand.NEGATIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(1));
        assertThat(diffBlock.getStart(), is(150));
        assertThat(diffBlock.getEnd(), is(200));
        assertThat(diffBlock.getStrand(), is(Strand.NEGATIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
    }
    
    @Test
    public void testMinusRemovalFromMiddleSameStrand() {
        Block block1 = new Block("chr1", 100, 400, Strand.POSITIVE);
        Block block2 = new Block("chr1", 200, 300, Strand.POSITIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(2));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(400));
        assertThat(diffBlock.getStrand(), is(Strand.POSITIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
        Iterator<Block> blockIter = diffBlock.getBlockIterator();
        Block firstBlock = blockIter.next();
        assertThat(firstBlock.getStart(), is(100));
        assertThat(firstBlock.getEnd(), is(200));
        assertThat(firstBlock.getReferenceName(), is("chr1"));
        assertThat(firstBlock.getStrand(), is(Strand.POSITIVE));
        Block secondBlock = blockIter.next();
        assertThat(secondBlock.getStart(), is(300));
        assertThat(secondBlock.getEnd(), is(400));
        assertThat(secondBlock.getReferenceName(), is("chr1"));
        assertThat(secondBlock.getStrand(), is(Strand.POSITIVE));
    }
    
    @Test
    public void testMinusRemovalFromMiddleNegativeMinusBoth() {
        Block block1 = new Block("chr1", 100, 400, Strand.NEGATIVE);
        Block block2 = new Block("chr1", 200, 300, Strand.BOTH);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(2));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(400));
        assertThat(diffBlock.getStrand(), is(Strand.NEGATIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
        Iterator<Block> blockIter = diffBlock.getBlockIterator();
        Block firstBlock = blockIter.next();
        assertThat(firstBlock.getStart(), is(100));
        assertThat(firstBlock.getEnd(), is(200));
        assertThat(firstBlock.getReferenceName(), is("chr1"));
        assertThat(firstBlock.getStrand(), is(Strand.NEGATIVE));
        Block secondBlock = blockIter.next();
        assertThat(secondBlock.getStart(), is(300));
        assertThat(secondBlock.getEnd(), is(400));
        assertThat(secondBlock.getReferenceName(), is("chr1"));
        assertThat(secondBlock.getStrand(), is(Strand.NEGATIVE));
    }
    
    @Test
    public void testMinusRemovalFromMiddleBothMinusNegative() {
        Block block1 = new Block("chr1", 100, 400, Strand.BOTH);
        Block block2 = new Block("chr1", 200, 300, Strand.NEGATIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(2));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(400));
        assertThat(diffBlock.getStrand(), is(Strand.NEGATIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
        Iterator<Block> blockIter = diffBlock.getBlockIterator();
        Block firstBlock = blockIter.next();
        assertThat(firstBlock.getStart(), is(100));
        assertThat(firstBlock.getEnd(), is(200));
        assertThat(firstBlock.getReferenceName(), is("chr1"));
        assertThat(firstBlock.getStrand(), is(Strand.NEGATIVE));
        Block secondBlock = blockIter.next();
        assertThat(secondBlock.getStart(), is(300));
        assertThat(secondBlock.getEnd(), is(400));
        assertThat(secondBlock.getReferenceName(), is("chr1"));
        assertThat(secondBlock.getStrand(), is(Strand.NEGATIVE));
    }
    
    @Test
    public void testPerfectNonOverlappingTiling() {
        Block bigBlock = new Block("chr1", 0, 1000, Strand.BOTH);
        Iterator<Block> tiles = bigBlock.tile(100, 100);
        int numTiles = 10;
        for (int i = 0; i < numTiles; i++) {
            Block tile = tiles.next();
            assertThat(tile, is(new Block("chr1", i * 100, (i + 1) * 100, Strand.BOTH)));
        }
        assertThat(tiles.hasNext(), is(false));
    }
    
    @Test
    public void testImperfectNonOverlappingTiling() {
        Block bigBlock = new Block("chr1", 0, 1010, Strand.BOTH);
        Iterator<Block> tiles = bigBlock.tile(100, 100);
        int numTiles = 10;
        for (int i = 0; i < numTiles; i++) {
            Block tile = tiles.next();
            assertThat(tile, is(new Block("chr1", i * 100, (i + 1) * 100, Strand.BOTH)));
        }
        assertThat(tiles.hasNext(), is(false));
    }
}