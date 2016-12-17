package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.function.Predicate;

import edu.caltech.lncrna.bio.alignment.SingleRead;
import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.utils.CloseableFilteredIterator;
import edu.caltech.lncrna.bio.utils.CloseableIterator;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

public final class SingleReadBamParser extends BamParser<SingleRead> {
    
    private final CloseableIterator<SingleRead> iterator;
    
    public SingleReadBamParser(Path p) {
        super(p);
        iterator = new SingleReadIterator(getSamReaderFromPath(p).iterator());
    }
    
    public SingleReadBamParser(Path p, Annotation overlappingAnnotation) {
        super(p);
        SingleReadIterator samIterator = new SingleReadIterator(
                getSamReaderFromPath(p).queryOverlapping(
                        overlappingAnnotation.getReferenceName(),
                        overlappingAnnotation.getStart(),
                        overlappingAnnotation.getEnd()));
        Predicate<SingleRead> pred = x -> x.getAlignment().isPresent() &&
                x.getAlignment().get().overlaps(overlappingAnnotation);
        iterator = new CloseableFilteredIterator<SingleRead>(samIterator, pred);
    }
    
    private SamReader getSamReaderFromPath(Path p) {
        return SamReaderFactory.makeDefault()
                .validationStringency(ValidationStringency.SILENT).open(p);
    }

    @Override
    public void close() {
        iterator.close();
    }

    @Override
    public SingleRead next() {
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    public class SingleReadIterator implements CloseableIterator<SingleRead> {
        
        protected SAMRecordIterator underlyingIterator;

        public SingleReadIterator(SAMRecordIterator underlyingIterator) {
            this.underlyingIterator = underlyingIterator;
        }
 
        @Override
        public boolean hasNext() {
            return underlyingIterator.hasNext();
        }

        @Override
        public SingleRead next() {
            return new SingleRead(underlyingIterator.next());
        }

        @Override
        public void close() {
            underlyingIterator.close();
        }
    }
}