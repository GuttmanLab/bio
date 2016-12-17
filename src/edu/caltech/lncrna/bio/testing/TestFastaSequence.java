package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.sequence.FastaSequence;

public class TestFastaSequence {

    private static String NAME1 = "name1";
    private static String NAME2 = "name2";
    private static String SEQ1 = "ACTGACTGN";
    private static String COPY_OF_SEQ1 = "ACTGACTGN";
    private static String RC_SEQ1 = "NCAGTCAGT";
    private static String SEQ2 = "TGATNTGAT";
    private static String ALL_A = "AAAAAAAA";
    private static String ALL_T = "TTTTTTTT";
    private static String A_AND_T = "ATATATTA";
    
    private static FastaSequence F1 = new FastaSequence(NAME1, SEQ1);
    private static FastaSequence COPY_OF_F1 = new FastaSequence(NAME1, COPY_OF_SEQ1);
    private static FastaSequence F2 = new FastaSequence(NAME2, SEQ2);
    private static FastaSequence F_AS = new FastaSequence(NAME1, ALL_A);
    private static FastaSequence F_TS = new FastaSequence(NAME1, ALL_T);
    private static FastaSequence F_ATS = new FastaSequence(NAME1, A_AND_T);
    
    protected static final String nl = System.getProperty("line.separator");
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testConstructorNullName() {
        thrown.expect(IllegalArgumentException.class);
        new FastaSequence(null, SEQ1);
    }
    
    @Test
    public void testConstructorNullSequence() {
        thrown.expect(IllegalArgumentException.class);
        new FastaSequence(NAME1, null);
    }
    
    @Test
    public void testGetBases() {
        assertThat(F1.getBases(), is(SEQ1));
    }
    
    @Test
    public void testGetName() {
        assertThat(F1.getName(), is(NAME1));
    }
    
    @Test
    public void testGetNameAfterChangeName() {
        FastaSequence newFasta = F1.changeName(NAME2);
        assertThat(newFasta.getName(), is(NAME2));
    }
    
    @Test
    public void testGetBasesAfterChangeName() {
        FastaSequence newFasta = F1.changeName(NAME2);
        assertThat(newFasta.getBases(), is(SEQ1));
    }
    
    @Test
    public void testReverseComplementSameName() {
        assertThat(F1.reverseComplement(),
                is(new FastaSequence(NAME1, RC_SEQ1)));
    }
    
    @Test
    public void testReverseComplementDifferentName() {
        assertThat(F1.reverseComplement(NAME2),
                is(new FastaSequence(NAME2, RC_SEQ1)));
    }
    
    //TODO subsequence tests
    
    @Test
    public void testLength() {
        assertThat(F1.length(), is(SEQ1.length()));
    }
    
    @Test
    public void testIsPolyAPositiveAllA() {
        assertThat(F_AS.isPolyA(), is(true));
    }
    
    @Test
    public void testIsPolyAPositiveAllT() {
        assertThat(F_TS.isPolyA(), is(true));
    }
    
    @Test
    public void testIsPolyANegativeAAndT() {
        assertThat(F_ATS.isPolyA(), is(false));
    }
    
    @Test
    public void testIsPolyANegativeGeneral() {
        assertThat(F1.isPolyA(), is(false));
    }
    
    @Test
    public void testToFasta() {
        assertThat(F1.toFasta(), is(">" + NAME1 + nl + SEQ1));
    }
    
    @Test
    public void testToFormattedString() {
        assertThat(F1.toFormattedString(), is(">" + NAME1 + nl + SEQ1));
    }
    
    @Test
    public void testToString() {
        assertThat(F1.toString(), is(NAME1 + ": " + SEQ1));
    }
    
    @Test
    public void testEqualsIdentity() {
        assertThat(F1.equals(F1), is(true));
    }
    
    @Test
    public void testEqualsNull() {
        assertThat(F1.equals(null), is(false));
    }
    
    @Test
    public void testEqualsCopy() {
        assertThat(F1.equals(COPY_OF_F1), is(true));
    }
    
    @Test
    public void testEqualsFalse() {
        assertThat(F1.equals(F2), is(false));
    }
}