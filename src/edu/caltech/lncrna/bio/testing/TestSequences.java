package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.sequence.Base;
import edu.caltech.lncrna.bio.sequence.Sequences;

public class TestSequences {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testReverseComplement() {
        assertThat(Sequences.reverseComplement("ACTGactgnN"), is("NncagtCAGT"));
    }
    
    @Test
    public void testComplementUnknownCharacter() {
        thrown.expect(IllegalArgumentException.class);
        Sequences.complement('f');
    }
    
    @Test
    public void testBases() {
        assertThat(Base.of('A'), is(Base.A));
        assertThat(Base.of('a'), is(Base.A));
        assertThat(Base.of('C'), is(Base.C));
        assertThat(Base.of('c'), is(Base.C));
        assertThat(Base.of('G'), is(Base.G));
        assertThat(Base.of('g'), is(Base.G));
        assertThat(Base.of('N'), is(Base.N));
        assertThat(Base.of('n'), is(Base.N));
        assertThat(Base.of('T'), is(Base.T));
        assertThat(Base.of('t'), is(Base.T));
        assertThat(Base.of('X'), is(Base.INVALID));
    }
}