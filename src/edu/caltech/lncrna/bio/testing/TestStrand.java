package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.annotation.Strand;

public class TestStrand {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testStrandReverse() {
        assertThat(Strand.POSITIVE.reverse(), is(Strand.NEGATIVE));
        assertThat(Strand.NEGATIVE.reverse(), is(Strand.POSITIVE));
        assertThat(Strand.BOTH.reverse(), is(Strand.BOTH));
        assertThat(Strand.INVALID.reverse(), is(Strand.INVALID));
    }
    
    @Test
    public void testStrandIntersect() {
        Strand p = Strand.POSITIVE;
        Strand n = Strand.NEGATIVE;
        Strand b = Strand.BOTH;
        Strand i = Strand.INVALID;
        
        assertThat(p.intersect(null), is(i));
        assertThat(p.intersect(n), is(i));
        assertThat(p.intersect(p), is(p));
        assertThat(p.intersect(b), is(p));
        assertThat(p.intersect(i), is(i));
        
        assertThat(n.intersect(null), is(i));
        assertThat(n.intersect(n), is(n));
        assertThat(n.intersect(p), is(i));
        assertThat(n.intersect(b), is(n));
        assertThat(n.intersect(i), is(i));
        
        assertThat(b.intersect(null), is(i));
        assertThat(b.intersect(n), is(n));
        assertThat(b.intersect(p), is(p));
        assertThat(b.intersect(b), is(b));
        assertThat(b.intersect(i), is(i));
        
        assertThat(i.intersect(null), is(i));
        assertThat(i.intersect(n), is(i));
        assertThat(i.intersect(p), is(i));
        assertThat(i.intersect(b), is(i));
        assertThat(i.intersect(i), is(i));
    }
    
    @Test
    public void testStrandContains() {
        Strand p = Strand.POSITIVE;
        Strand n = Strand.NEGATIVE;
        Strand b = Strand.BOTH;
        Strand i = Strand.INVALID;
        
        assertThat(p.contains(null), is(false));
        assertThat(p.contains(n), is(false));
        assertThat(p.contains(p), is(true));
        assertThat(p.contains(b), is(false));
        assertThat(p.contains(i), is(false));
        
        assertThat(n.contains(null), is(false));
        assertThat(n.contains(n), is(true));
        assertThat(n.contains(p), is(false));
        assertThat(n.contains(b), is(false));
        assertThat(n.contains(i), is(false));
        
        assertThat(b.contains(null), is(false));
        assertThat(b.contains(n), is(true));
        assertThat(b.contains(p), is(true));
        assertThat(b.contains(b), is(true));
        assertThat(b.contains(i), is(false));
        
        assertThat(i.contains(null), is(false));
        assertThat(i.contains(n), is(false));
        assertThat(i.contains(p), is(false));
        assertThat(i.contains(b), is(false));
        assertThat(i.contains(i), is(false));
    }
    
    @Test
    public void testStrandFromString() {
        assertThat(Strand.fromString("+"), is(Strand.POSITIVE));
        assertThat(Strand.fromString("-"), is(Strand.NEGATIVE));
        assertThat(Strand.fromString("."), is(Strand.BOTH));
    }
}