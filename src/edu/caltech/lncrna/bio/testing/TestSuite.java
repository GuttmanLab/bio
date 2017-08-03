package edu.caltech.lncrna.bio.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    
    // alignment package
    TestPairedEndReadAlignment.class,
    TestSingleRead.class,
    TestSingleReadAlignment.class,
    
    // annotation package
    TestAnnotation.class,
    TestAnnotationBuilder.class,
    TestAnnotationContainment.class,
    TestAnnotationIntersection.class,
    TestAnnotationOverlap.class,
    TestCigarIterator.class,
    TestStrand.class,
    TestWindowIterator.class,
    
    // datastructures package
    TestGenomeTree.class,
    TestSimpleIntervalTree.class,
    TestDegenerateIntervalTree.class,
    
    // io package
    TestBamWriter.class,
    TestBedParser.class,
    TestFastaParser.class,
    TestFastqParser.class,
    TestPairedEndBamParser.class,
    TestSingleReadBamParser.class,
    
    // sequence package
    TestFastaSequence.class,
    TestFastqSequence.class,
    TestSequences.class
})

public class TestSuite{}