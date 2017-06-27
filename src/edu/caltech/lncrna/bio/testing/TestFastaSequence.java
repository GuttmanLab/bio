package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.sequence.FastaSequence;
import edu.caltech.lncrna.bio.sequence.Sequence;

public class TestFastaSequence {
    
    protected static final String nl = System.getProperty("line.separator");
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testConstructorNullName() {
        thrown.expect(NullPointerException.class);
        new FastaSequence(null, "ACTGGATGCA");
    }
    
    @Test
    public void testConstructorNullSequence() {
        thrown.expect(NullPointerException.class);
        new FastaSequence("Fasta1", null);
    }
    
    @Test
    public void testGetBases() {
        Sequence a = new FastaSequence("fasta", "ATGGCTAGATC");
        assertThat(a.getBases(), is("ATGGCTAGATC"));
    }
    
    @Test
    public void testGetName() {
        Sequence a = new FastaSequence("fasta", "ATGGCTAGATC");
        assertThat(a.getName(), is("fasta"));
    }
    
    @Test
    public void testGetNameAfterChangeName() {
        Sequence oldFasta = new FastaSequence("foo", "ATGGCTAGTCA");
        Sequence newFasta = oldFasta.changeName("bar");
        assertThat(newFasta.getName(), is("bar"));
    }
    
    @Test
    public void testGetBasesAfterChangeName() {
        Sequence oldFasta = new FastaSequence("foo", "ATGGCTAGTCA");
        Sequence newFasta = oldFasta.changeName("bar");
        assertThat(newFasta.getBases(), is("ATGGCTAGTCA"));
    }
    
    @Test
    public void testReverseComplementSameName() {
        Sequence oldFasta = new FastaSequence("foo", "ATGGCTAGA");
        assertThat(oldFasta.reverseComplement(), 
                is(new FastaSequence("foo", "TCTAGCCAT")));
    }
    
    @Test
    public void testReverseComplementDifferentName() {
        Sequence oldFasta = new FastaSequence("foo", "ATGGCTAGA");
        assertThat(oldFasta.reverseComplement("bar"), 
                is(new FastaSequence("bar", "TCTAGCCAT")));
    }
    
    //TODO subsequence tests
    
    @Test
    public void testLength() {
        Sequence f = new FastaSequence("foo", "ATGATGCACA");
        assertThat(f.length(), is(10));
    }
    
    @Test
    public void testIsPolyAPositiveAllA() {
        Sequence f = new FastaSequence("foo", "AaaAAAaAAA");
        assertThat(f.isPolyA(), is(true));
    }
    
    @Test
    public void testIsPolyAPositiveAllT() {
        Sequence f = new FastaSequence("foo", "TttTTtTTT");
        assertThat(f.isPolyA(), is(true));
    }
    
    @Test
    public void testIsPolyANegativeAAndT() {
        Sequence f = new FastaSequence("foo", "AtatATtattat");
        assertThat(f.isPolyA(), is(false));
    }
    
    @Test
    public void testIsPolyANegativeGeneral() {
        Sequence f = new FastaSequence("foo", "GgtcgatGATcg");
        assertThat(f.isPolyA(), is(false));
    }
    
    @Test
    public void testToFasta() {
        Sequence f = new FastaSequence("foo", "AGTTTAGATA");
        assertThat(f.toFasta(), is(">" + "foo" + nl + "AGTTTAGATA" + nl));
    }
    
    
    @Test
    public void testEqualsIdentity() {
        Sequence f = new FastaSequence("foo", "AGGTAGAGA");
        assertThat(f.equals(f), is(true));
    }
    
    @Test
    public void testEqualsNull() {
        Sequence f = new FastaSequence("foo", "AGGTAGAGA");
        assertThat(f.equals(null), is(false));
    }
    
    @Test
    public void testEqualsCopy() {
        Sequence f1 = new FastaSequence("foo", "AGGTAGAGA");
        Sequence f2 = new FastaSequence("foo", "AGGTAGAGA");
        assertThat(f1.equals(f2), is(true));
    }
    
    @Test
    public void testDifferentNameEqualsFalse() {
        Sequence f1 = new FastaSequence("foo1", "AGGTAGAGA");
        Sequence f2 = new FastaSequence("foo2", "AGGTAGAGA");
        assertThat(f1.equals(f2), is(false));
    }
    
    @Test
    public void testDifferentBasesEqualsFalse() {
        Sequence f1 = new FastaSequence("foo", "TAGGTAGAGA");
        Sequence f2 = new FastaSequence("foo", "CAGGTAGAGA");
        assertThat(f1.equals(f2), is(false));
    }
}