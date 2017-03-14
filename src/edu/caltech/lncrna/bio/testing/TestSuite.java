package edu.caltech.lncrna.bio.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    // annotation package
    TestAnnotationBuilder.class,
    TestAnnotationContainment.class,
    TestAnnotationIntersection.class,
    TestAnnotationOverlap.class,
    TestBlock.class,
    TestBlockedAnnotation.class,
    TestStrand.class,
    TestWindowIterator.class,
    
    // datastructures package
    TestIntervalTree.class,
    
    // io package
    TestPairedEndBamParser.class,
    
    // sequence package
    TestSequences.class
})

/*
@Suite.SuiteClasses({
    // datastructures package
    TestIntervalSetTree.class,
    TestIntervalTree.class,
    
    // annotation package
    TestBlock.class,
    TestBlockedAnnotation.class,
    
    // sequence package
    TestSequences.class,
    TestFastaSequence.class,
    
    // io package
    TestFastaParser.class,
    TestSingleReadBamParser.class,
    TestPairedEndBamParser.class
})*/

public class TestSuite{}