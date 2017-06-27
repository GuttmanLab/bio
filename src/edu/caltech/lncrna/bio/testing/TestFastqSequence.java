package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.sequence.FastqSequence;
import edu.caltech.lncrna.bio.sequence.PhredEncoding;
import edu.caltech.lncrna.bio.sequence.Sequence;

public class TestFastqSequence {
    
    protected static final String nl = System.getProperty("line.separator");
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testConstructorNullName() {
        thrown.expect(NullPointerException.class);
        new FastqSequence(null, "ACTGGATGCA", "IIIIIIIIII",
                PhredEncoding.SANGER);
    }
    
    @Test
    public void testConstructorNullBases() {
        thrown.expect(NullPointerException.class);
        new FastqSequence("fastq", null, "IIIIIIIIII", PhredEncoding.SANGER);
    }
    
    @Test
    public void testConstructorNullQualities() {
        thrown.expect(NullPointerException.class);
        new FastqSequence("fastq", "ACTGGATGCA", null, PhredEncoding.SANGER);
    }
    
    @Test
    public void testConstructorNullPhred() {
        thrown.expect(NullPointerException.class);
        new FastqSequence("fastq", "ACTGGATGCA", "IIIIIIIIII", null);
    }
    
    @Test
    public void testConstructorLengthMismatch() {
        thrown.expect(IllegalArgumentException.class);
        new FastqSequence("fastq", "ACTGGATGCA", "IIIIIIIII",
                PhredEncoding.SANGER);
    }

    @Test
    public void testGetBases() {
        Sequence a = new FastqSequence("fastq", "ATGGCTAGATC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(a.getBases(), is("ATGGCTAGATC"));
    }
    
    @Test
    public void testGetName() {
        Sequence a = new FastqSequence("fastq", "ATGGCTAGATC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(a.getName(), is("fastq"));
    }
    
    @Test
    public void testLength() {
        Sequence a = new FastqSequence("fastq", "ATGGCTAGATC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(a.length(), is(11));
    }
    
    @Test
    public void testIsPolyAPositiveAllA() {
        Sequence f = new FastqSequence("fastq", "AaAAaAaAAAa", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f.isPolyA(), is(true));
    }
    
    @Test
    public void testIsPolyAPositiveAllT() {
        Sequence f = new FastqSequence("fastq", "tTTTtTtTTTT", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f.isPolyA(), is(true));
    }
    
    @Test
    public void testIsPolyANegativeAAndT() {
        Sequence f = new FastqSequence("fastq", "AtAAttATTTa", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f.isPolyA(), is(false));
    }
    
    @Test
    public void testIsPolyANegativeGeneral() {
        Sequence f = new FastqSequence("fastq", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f.isPolyA(), is(false));
    }
    
    @Test
    public void testToFasta() {
        Sequence f = new FastqSequence("fastq", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f.toFasta(), is(">" + "fastq" + nl + "GAcgTATTAGC" + nl));
    }
    
    
    @Test
    public void testEqualsIdentity() {
        Sequence f = new FastqSequence("fastq", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f.equals(f), is(true));
    }
    
    @Test
    public void testEqualsNull() {
        Sequence f = new FastqSequence("fastq", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f.equals(null), is(false));
    }
    
    @Test
    public void testEqualsCopy() {
        Sequence f1 = new FastqSequence("fastq", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        Sequence f2 = new FastqSequence("fastq", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f1.equals(f2), is(true));
    }
    
    @Test
    public void testDifferentNameEqualsFalse() {
        Sequence f1 = new FastqSequence("fastq1", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        Sequence f2 = new FastqSequence("fastq2", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f1.equals(f2), is(false));
    }
    
    @Test
    public void testDifferentBasesEqualsFalse() {
        Sequence f1 = new FastqSequence("fastq", "GAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        Sequence f2 = new FastqSequence("fastq", "AAcgTATTAGC", "IIIIIIIIIII",
                PhredEncoding.SANGER);
        assertThat(f1.equals(f2), is(false));
    }
}