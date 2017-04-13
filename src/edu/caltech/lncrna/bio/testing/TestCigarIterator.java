package edu.caltech.lncrna.bio.testing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.caltech.lncrna.bio.alignment.CigarIterator;
import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;

public class TestCigarIterator {
    
    public final static byte A = 65;
    public final static byte C = 67;
    public final static byte G = 71;
    public final static byte N = 78;
    public final static byte T = 84;
    
    public Cigar cigar;
    
    @Before
    public void setup() {
        List<CigarElement> elements = new ArrayList<>();
        elements.add(new CigarElement(6, CigarOperator.SOFT_CLIP));
        elements.add(new CigarElement(10, CigarOperator.MATCH_OR_MISMATCH));
        elements.add(new CigarElement(1, CigarOperator.DELETION));
        elements.add(new CigarElement(5, CigarOperator.MATCH_OR_MISMATCH));
        elements.add(new CigarElement(2, CigarOperator.INSERTION));
        elements.add(new CigarElement(12, CigarOperator.MATCH_OR_MISMATCH));
        elements.add(new CigarElement(2, CigarOperator.SOFT_CLIP));
        cigar = new Cigar(elements);
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testCigarIteratorConstructorFailsWithNullReference() {
        thrown.expect(NullPointerException.class);
        new CigarIterator(null);
    }
    
    @Test
    public void testCigarIterator() {
        CigarIterator ops = new CigarIterator(cigar);
        assertCigarIterations(ops, 6, CigarOperator.SOFT_CLIP);
        assertCigarIterations(ops, 10, CigarOperator.MATCH_OR_MISMATCH);
        assertCigarIterations(ops, 1, CigarOperator.DELETION);
        assertCigarIterations(ops, 5, CigarOperator.MATCH_OR_MISMATCH);
        assertCigarIterations(ops, 2, CigarOperator.INSERTION);
        assertCigarIterations(ops, 12, CigarOperator.MATCH_OR_MISMATCH);
        assertCigarIterations(ops, 2, CigarOperator.SOFT_CLIP);
        assertThat(ops.hasNext(), is(false));
    }
    
    public void assertCigarIterations(CigarIterator ops, int num, CigarOperator op) {
        CigarOperator tmp;
        for (int i = 0; i < num; i++) {
            tmp = ops.next();
            assertThat(tmp, is(op));
        }
    }
}
