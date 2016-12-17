package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import edu.caltech.lncrna.bio.alignment.ReadPair;
import edu.caltech.lncrna.bio.alignment.SingleRead;
import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.utils.CloseableFilteredIterator;
import edu.caltech.lncrna.bio.utils.CloseableIterator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

public class PairedEndBamParser extends BamParser<ReadPair> {
    
    private final CloseableIterator<ReadPair> iterator;

    public PairedEndBamParser(Path p) {
        super(p);
        SamReaderFactory samReaderFactory = SamReaderFactory.makeDefault()
                .validationStringency(ValidationStringency.SILENT);
        SamReader samReader = samReaderFactory.open(p.toFile());
        iterator = new PairedEndIterator(samReader.iterator());
    }
    
    public PairedEndBamParser(Path p, Annotation overlappingAnnotation) {
        super(p);
        SamReaderFactory samReaderFactory = SamReaderFactory.makeDefault()
                .validationStringency(ValidationStringency.SILENT);
        SamReader samReader = samReaderFactory.open(p.toFile());
        PairedEndIterator underlyingIterator = new PairedEndIterator(
                samReader.queryOverlapping(overlappingAnnotation.getReferenceName(),
                                           overlappingAnnotation.getStart(),
                                           overlappingAnnotation.getEnd()));
        Predicate<ReadPair> pred = x -> x.getAlignment().isPresent() &&
                x.getAlignment().get().overlaps(overlappingAnnotation);
        iterator = new CloseableFilteredIterator<ReadPair>(underlyingIterator, pred);
    }

    @Override
    public void close() {
        iterator.close();
    }
    
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public ReadPair next() {
        return iterator.next();
    }
    
    public class PairedEndIterator implements CloseableIterator<ReadPair> {

        private final SAMRecordIterator underlyingIterator;
        private final Map<String, SingleRead> unpairedCache;
        private ReadPair next;
        private String cacheReference = null;
        
        public PairedEndIterator(SAMRecordIterator underlyingIterator) {
            this.underlyingIterator = underlyingIterator;
            unpairedCache = new HashMap<String, SingleRead>();
            findNext();
        }
        
        public boolean hasNext() {
            return next != null;
        }
        
        public ReadPair next() {
            if (!hasNext()) {
                throw new NoSuchElementException("PairedEndIterator.next() " +
                        "called with no next element.");
            }
            ReadPair rtrn = next;
            findNext();
            return rtrn;
        }
        
        private void findNext() {
            next = null;
            while (underlyingIterator.hasNext() && next == null) {
                SAMRecord samRecord = underlyingIterator.next();
                resetCacheIfNewReference(samRecord.getReferenceName());
                checkCacheForMateAndPairUpIfFound(samRecord);
            }
        }
        
        private void resetCacheIfNewReference(String ref) {
            if (!ref.equalsIgnoreCase(cacheReference)) {
                cacheReference = ref;
                unpairedCache.clear();
            }
        }
        
        private void checkCacheForMateAndPairUpIfFound(SAMRecord samRecord) {
            SingleRead fragment = new SingleRead(samRecord);
            SingleRead mate = unpairedCache.remove(samRecord.getReadName());
            if (mate == null) {
                unpairedCache.put(samRecord.getReadName(), fragment);
            } else {
                next = new ReadPair(fragment, mate);
            }            
        }
        
        @Override
        public void close() {
            underlyingIterator.close();
        }
    }
}