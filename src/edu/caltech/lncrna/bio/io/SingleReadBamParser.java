package edu.caltech.lncrna.bio.io;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.caltech.lncrna.bio.alignment.SingleRead;
import edu.caltech.lncrna.bio.alignment.SingleReadAlignment;
import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.utils.CloseableFilteredIterator;
import edu.caltech.lncrna.bio.utils.CloseableIterator;
import htsjdk.samtools.SAMRecordIterator;

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
    
    @Override
    public Stream<SingleReadAlignment> getAlignmentStream() {
        return stream().map(x -> x.getAlignment())
                .filter(Optional::isPresent)
                .map(x -> x.get());
    }
    
    @Override
    public Iterator<SingleReadAlignment> getAlignmentIterator() {
        return getAlignmentStream().iterator();
    }
    
    public final class SingleReadIterator implements CloseableIterator<SingleRead> {
        
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