package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.caltech.lncrna.bio.sequence.Sequences;

public class TestSequences {

    private final String SEQ = "ACTGactgnN";
    private String RC_SEQ = "NncagtCAGT";
    
    @Test
    public void testReverseComplement() {
        assertThat(Sequences.reverseComplement(SEQ), is(RC_SEQ));
    }
}