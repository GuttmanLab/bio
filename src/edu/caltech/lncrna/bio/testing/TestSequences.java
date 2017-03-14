package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
}