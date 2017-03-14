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
    public void testBlockConstructorFailsWithInvalidBlock() {
        thrown.expect(NullPointerException.class);
        new Block(null);
    }
    
    @Test
    public void testBlockConstructorWithValidBlock() {
        Annotated b1 = new Block("chr1", 10, 20, Strand.BOTH);
        Annotated b2 = new Block(b1);
        assertThat(b2, is(b1));
    }
    
    @Test
    public void testBlockConstructorWithValidBlockedAnnotation() {
        Annotated oldAnnotation = (new BlockedBuilder())
                .addBlock(new Block("chr1", 10, 20, Strand.BOTH))
                .addBlock(new Block("chr1", 30, 40, Strand.BOTH))
                .build();
        Annotated newBlock = new Block(oldAnnotation);
        Annotated cmpBlock = new Block("chr1", 10, 40, Strand.BOTH);
        assertThat(newBlock, is(cmpBlock));
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
        Annotated b = new Block(new Block("chr1", 10, 20, Strand.POSITIVE), Strand.NEGATIVE);
        assertThat(b.getReferenceName(), is("chr1"));
        assertThat(b.getStart(), is(10));
        assertThat(b.getEnd(), is(20));
        assertThat(b.getStrand(), is(Strand.NEGATIVE));
    }
    
    @Test
    public void testBlockGetReferenceName() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getReferenceName(), is("chr1"));
    }
    
    @Test
    public void testBlockGetStart() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getStart(), is(10));
    }
    
    @Test
    public void testBlockGetEnd() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getEnd(), is(20));
    }
    
    @Test
    public void testBlockGetStrand() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getStrand(), is(Strand.POSITIVE));
    }
    
    @Test
    public void testBlockGetSize() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getSize(), is(10));
    }
    
    @Test
    public void testBlockGetSpan() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getSpan(), is(10));
    }
    
    @Test
    public void testBlockEqualObjectCopy() {
        Annotated b1 = new Block("chr1", 10, 20, Strand.POSITIVE);
        Annotated b2 = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b1 == b2, is(false));
    }
    
    @Test
    public void testBlockEqualIdentity() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b, is(b));
    }
    
    @Test
    public void testBlockEqualCopy() {
        Annotated b1 = new Block("chr1", 10, 20, Strand.POSITIVE);
        Annotated b2 = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b1, is(b2));
    }
    
    @Test
    public void testBlockNotEqualNonBlock() {
        Annotated b1 = new Block("chr1", 10, 20, Strand.POSITIVE);
        Annotated b2 = (new BlockedBuilder())
                .addBlock(new Block("chr1", 10, 20, Strand.POSITIVE))
                .build();
        assertThat(b1.equals(b2), is(false));
    }
    
    @Test
    public void testBlockNotEqualsDifferentChromosomes() {
        Annotated b1 = new Block("chr1", 10, 20, Strand.POSITIVE);
        Annotated b2 = new Block("chr2", 10, 20, Strand.POSITIVE);
        assertThat(b1.equals(b2), is(false));
    }
    
    @Test
    public void testBlockNotEqualsDifferentStart() {
        Annotated b1 = new Block("chr1", 10, 20, Strand.POSITIVE);
        Annotated b2 = new Block("chr1", 11, 20, Strand.POSITIVE);
        assertThat(b1.equals(b2), is(false));
    }
    
    @Test
    public void testBlockNotEqualsDifferentEnd() {
        Annotated b1 = new Block("chr1", 10, 20, Strand.POSITIVE);
        Annotated b2 = new Block("chr1", 10, 21, Strand.POSITIVE);
        assertThat(b1.equals(b2), is(false));
    }
    
    @Test
    public void testBlockNotEqualsDifferentStrand() {
        Annotated b1 = new Block("chr1", 10, 20, Strand.POSITIVE);
        Annotated b2 = new Block("chr1", 10, 20, Strand.NEGATIVE);
        assertThat(b1.equals(b2), is(false));
    }
    
    @Test
    public void testBlockGetBody() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getBody(), is(b));
    }
    
    @Test
    public void testGetFivePrimePositionPositveStrand() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getFivePrimePosition(), is(10));
    }
    
    @Test
    public void testGetThreePrimePositionPositveStrand() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getThreePrimePosition(), is(20));
    }
    
    @Test
    public void testGetFivePrimePositionNegativeStrand() {
        Annotated b = new Block("chr1", 10, 20, Strand.NEGATIVE);
        assertThat(b.getFivePrimePosition(), is(20));
    }
    
    @Test
    public void testGetThreePrimePositionNegativeStrand() {
        Annotated b = new Block("chr1", 10, 20, Strand.NEGATIVE);
        assertThat(b.getThreePrimePosition(), is(10));
    }
    
    @Test
    public void testGetFivePrimePositionBothStrand() {
        thrown.expect(IllegalArgumentException.class);
        (new Block("chr1", 10, 20, Strand.BOTH)).getFivePrimePosition();
    }
    
    @Test
    public void testGetThreePrimePositionBothStrand() {
        thrown.expect(IllegalArgumentException.class);
        (new Block("chr1", 10, 20, Strand.BOTH)).getThreePrimePosition();
    }
    
    @Test
    public void testBlockGetIntrons() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getIntrons(), is(Optional.empty()));
    }
    
    @Test
    public void testBlockGetIntronIterator() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getIntronIterator().hasNext(), is(false));
    }
    
    @Test
    public void testBlockGetIntronStream() {
        Annotated b = new Block("chr1", 10, 20, Strand.POSITIVE);
        assertThat(b.getIntronStream().count(), is(0L));
    }
    

    
    @Test
    public void testGetBlockIterator() {
        Annotated b = new Block("chr1", 100, 200, Strand.POSITIVE);
        Iterator<Annotated> iter = b.getBlockIterator();
        assertThat(iter.hasNext(), is(true));
        Annotated nextBlock = iter.next();
        assertThat(nextBlock, is(b));
        assertThat(iter.hasNext(), is(false));
    }
    
    @Test
    public void testGetBlockStream() {
        Annotated b = new Block("chr1", 100, 200, Strand.POSITIVE);
        Stream<Annotated> stream = b.getBlockStream();
        assertThat(stream.count(), is(1L));
        stream = b.getBlockStream();
        assertThat(stream.findFirst().get(), is(b));
    }


    
    @Test
    public void testMinusWithNoOverlapSameStrandSameChromosome() {
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 50, 100, Strand.POSITIVE);
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
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr2", 100, 200, Strand.POSITIVE);
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
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr2", 100, 200, Strand.NEGATIVE);
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
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Optional<Annotated> diffBlock = block1.minus(block2);
        assertThat(diffBlock.isPresent(), is(false));
    }
    
    @Test
    public void testMinusCompleteRemovalNegativeMinusBoth() {
        Annotated block1 = new Block("chr1", 100, 200, Strand.NEGATIVE);
        Annotated block2 = new Block("chr1", 100, 200, Strand.BOTH);
        Optional<Annotated> diffBlock = block1.minus(block2);
        assertThat(diffBlock.isPresent(), is(false));
    }
    
    @Test
    public void testMinusCompleteRemovalPositiveMinusBoth() {
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 100, 200, Strand.BOTH);
        Optional<Annotated> diffBlock = block1.minus(block2);
        assertThat(diffBlock.isPresent(), is(false));
    }
    
    @Test
    public void testMinusCompleteRemovalBothMinusNegative() {
        Annotated block1 = new Block("chr1", 100, 200, Strand.BOTH);
        Annotated block2 = new Block("chr1", 100, 200, Strand.NEGATIVE);
        Optional<Annotated> diffBlock = block1.minus(block2);
        assertThat(diffBlock.isPresent(), is(false));
    }
    
    @Test
    public void testMinusRemovalFromEndSameStrand() {
        Annotated block1 = new Block("chr1", 100, 200, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 50, 150, Strand.POSITIVE);
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
        Annotated block1 = new Block("chr1", 100, 200, Strand.NEGATIVE);
        Annotated block2 = new Block("chr1", 50, 150, Strand.BOTH);
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
        Annotated block1 = new Block("chr1", 100, 200, Strand.BOTH);
        Annotated block2 = new Block("chr1", 50, 150, Strand.NEGATIVE);
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
        Annotated block1 = new Block("chr1", 100, 400, Strand.POSITIVE);
        Annotated block2 = new Block("chr1", 200, 300, Strand.POSITIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(2));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(400));
        assertThat(diffBlock.getStrand(), is(Strand.POSITIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
        Iterator<Annotated> blockIter = diffBlock.getBlockIterator();
        Annotated firstBlock = blockIter.next();
        assertThat(firstBlock.getStart(), is(100));
        assertThat(firstBlock.getEnd(), is(200));
        assertThat(firstBlock.getReferenceName(), is("chr1"));
        assertThat(firstBlock.getStrand(), is(Strand.POSITIVE));
        Annotated secondBlock = blockIter.next();
        assertThat(secondBlock.getStart(), is(300));
        assertThat(secondBlock.getEnd(), is(400));
        assertThat(secondBlock.getReferenceName(), is("chr1"));
        assertThat(secondBlock.getStrand(), is(Strand.POSITIVE));
    }
    
    @Test
    public void testMinusRemovalFromMiddleNegativeMinusBoth() {
        Annotated block1 = new Block("chr1", 100, 400, Strand.NEGATIVE);
        Annotated block2 = new Block("chr1", 200, 300, Strand.BOTH);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(2));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(400));
        assertThat(diffBlock.getStrand(), is(Strand.NEGATIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
        Iterator<Annotated> blockIter = diffBlock.getBlockIterator();
        Annotated firstBlock = blockIter.next();
        assertThat(firstBlock.getStart(), is(100));
        assertThat(firstBlock.getEnd(), is(200));
        assertThat(firstBlock.getReferenceName(), is("chr1"));
        assertThat(firstBlock.getStrand(), is(Strand.NEGATIVE));
        Annotated secondBlock = blockIter.next();
        assertThat(secondBlock.getStart(), is(300));
        assertThat(secondBlock.getEnd(), is(400));
        assertThat(secondBlock.getReferenceName(), is("chr1"));
        assertThat(secondBlock.getStrand(), is(Strand.NEGATIVE));
    }
    
    @Test
    public void testMinusRemovalFromMiddleBothMinusNegative() {
        Annotated block1 = new Block("chr1", 100, 400, Strand.BOTH);
        Annotated block2 = new Block("chr1", 200, 300, Strand.NEGATIVE);
        Annotated diffBlock = block1.minus(block2)
                                    .orElseThrow(() -> new IllegalArgumentException("No diff present"));
        assertThat(diffBlock.getNumberOfBlocks(), is(2));
        assertThat(diffBlock.getStart(), is(100));
        assertThat(diffBlock.getEnd(), is(400));
        assertThat(diffBlock.getStrand(), is(Strand.NEGATIVE));
        assertThat(diffBlock.getReferenceName(), is("chr1"));
        Iterator<Annotated> blockIter = diffBlock.getBlockIterator();
        Annotated firstBlock = blockIter.next();
        assertThat(firstBlock.getStart(), is(100));
        assertThat(firstBlock.getEnd(), is(200));
        assertThat(firstBlock.getReferenceName(), is("chr1"));
        assertThat(firstBlock.getStrand(), is(Strand.NEGATIVE));
        Annotated secondBlock = blockIter.next();
        assertThat(secondBlock.getStart(), is(300));
        assertThat(secondBlock.getEnd(), is(400));
        assertThat(secondBlock.getReferenceName(), is("chr1"));
        assertThat(secondBlock.getStrand(), is(Strand.NEGATIVE));
    }

    @Test
    public void testTileNegativeWindowSize() {
        thrown.expect(IllegalArgumentException.class);
        Block b = new Block("chr1", 0, 1000, Strand.POSITIVE);
        b.tile(-1, 100);
    }
    
    @Test
    public void testTileNegativeStepSize() {
        thrown.expect(IllegalArgumentException.class);
        Block b = new Block("chr1", 0, 1000, Strand.POSITIVE);
        b.tile(100, -1);
    }
    
    @Test
    public void testPerfectNonOverlappingTiling() {
        Block bigBlock = new Block("chr1", 0, 1000, Strand.BOTH);
        Iterator<Annotated> tiles = bigBlock.tile(100, 100);
        int numTiles = 10;
        for (int i = 0; i < numTiles; i++) {
            Annotated tile = tiles.next();
            assertThat(tile, is(new Block("chr1", i * 100, (i + 1) * 100, Strand.BOTH)));
        }
        assertThat(tiles.hasNext(), is(false));
    }
    
    @Test
    public void testImperfectNonOverlappingTiling() {
        Block bigBlock = new Block("chr1", 0, 1010, Strand.BOTH);
        Iterator<Annotated> tiles = bigBlock.tile(100, 100);
        int numTiles = 10;
        for (int i = 0; i < numTiles; i++) {
            Annotated tile = tiles.next();
            assertThat(tile, is(new Block("chr1", i * 100, (i + 1) * 100, Strand.BOTH)));
        }
        assertThat(tiles.hasNext(), is(false));
    }
}